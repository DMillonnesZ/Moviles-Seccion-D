package com.millones.campusgps.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.millones.campusgps.data.local.LugarDao
import com.millones.campusgps.data.local.LugarEntity
import com.millones.campusgps.data.local.aModelo
import com.millones.campusgps.model.Lugar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Lugares compartidos: Firestore es la fuente (todos ven lo que registran los demás en
// tiempo real) y Room es la caché local que lee la pantalla.
//   lugares/{id}                       -> el lugar (foto reducida en Base64 dentro del documento)
//   usuarios/{uid}/favoritos/{idLugar} -> favoritos de cada usuario
class LugaresRepositoryNube(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dao: LugarDao,
    private val fotos: AlmacenFotos
) : LugaresRepository {

    private val coleccion = db.collection("lugares")

    private fun miUid(): String? = auth.currentUser?.uid

    private fun favoritosDe(uid: String) =
        db.collection("usuarios").document(uid).collection("favoritos")

    override val lugares: Flow<List<Lugar>> =
        dao.observarTodos().map { lista ->
            val mio = miUid()
            lista.map { it.aModelo(mio) }
        }

    override fun sincronizar(): Flow<Unit> {
        val uid = miUid() ?: return emptyFlow()
        val idsFavoritos = favoritosDe(uid).escuchar().map { instantanea ->
            instantanea.documents.map { it.id }.toSet()
        }
        return combine(coleccion.escuchar(), idsFavoritos) { instantanea, favoritos ->
            val entidades = instantanea.documents.mapNotNull { aEntidad(it, favoritos) }
            dao.reemplazarTodo(entidades)
        }
    }

    override suspend fun guardar(
        nombre: String,
        descripcion: String,
        categoria: String,
        latitud: Double,
        longitud: Double,
        fotoUri: String?
    ): Result<Unit> = runCatching {
        val usuario = auth.currentUser ?: error("Inicia sesión para guardar lugares")
        val datos = hashMapOf<String, Any>(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "categoria" to categoria,
            "lat" to latitud,
            "lng" to longitud,
            "autorUid" to usuario.uid,
            "autorNombre" to nombreVisible(usuario.displayName, usuario.email),
            "creadoEn" to FieldValue.serverTimestamp()
        )
        if (fotoUri != null) datos["foto"] = fotos.comprimirBase64(fotoUri)
        coleccion.add(datos).await()
    }.map { }

    override suspend fun alternarFavorito(id: String): Result<Unit> = runCatching {
        val uid = miUid() ?: error("Inicia sesión para usar favoritos")
        val referencia = favoritosDe(uid).document(id)
        if (dao.obtener(id)?.favorito == true) {
            referencia.delete().await()
        } else {
            referencia.set(mapOf("creadoEn" to FieldValue.serverTimestamp())).await()
        }
    }.map { }

    override suspend fun cambiarFoto(id: String, fotoUri: String): Result<Unit> = runCatching {
        verificarAutor(id, "cambiar la foto")
        coleccion.document(id).update("foto", fotos.comprimirBase64(fotoUri)).await()
    }.map { }

    override suspend fun eliminar(id: String): Result<Unit> = runCatching {
        verificarAutor(id, "eliminar este lugar")
        coleccion.document(id).delete().await()
        miUid()?.let { favoritosDe(it).document(id).delete() }
    }.map { }

    // Las reglas de Firestore también lo impiden; aquí se avisa con un mensaje claro
    private suspend fun verificarAutor(id: String, accion: String) {
        val lugar = dao.obtener(id) ?: error("El lugar ya no existe")
        if (lugar.autorUid != miUid()) error("Solo ${lugar.autorNombre.ifBlank { "su autor" }} puede $accion")
    }

    private suspend fun aEntidad(doc: DocumentSnapshot, favoritos: Set<String>): LugarEntity? {
        val nombre = doc.getString("nombre") ?: return null
        val latitud = doc.getDouble("lat") ?: return null
        val longitud = doc.getDouble("lng") ?: return null
        val foto = doc.getString("foto")
            ?.takeIf { it.isNotBlank() }
            ?.let { runCatching { fotos.guardarBase64(it, "remoto_${doc.id}") }.getOrNull() }

        return LugarEntity(
            id = doc.id,
            nombre = nombre,
            descripcion = doc.getString("descripcion").orEmpty(),
            categoria = doc.getString("categoria") ?: "Otro",
            latitud = latitud,
            longitud = longitud,
            favorito = doc.id in favoritos,
            foto = foto,
            autorUid = doc.getString("autorUid").orEmpty(),
            autorNombre = doc.getString("autorNombre").orEmpty(),
            // Mientras el servidor no confirma la hora, se usa la del celular
            creadoEn = doc.getTimestamp("creadoEn")?.toDate()?.time ?: System.currentTimeMillis()
        )
    }
}

// Nombre que ven los demás: el nombre de la cuenta o la parte antes de @ del correo
fun nombreVisible(nombre: String?, correo: String?): String =
    nombre?.takeIf { it.isNotBlank() }
        ?: correo?.substringBefore("@")
        ?: "Estudiante"
