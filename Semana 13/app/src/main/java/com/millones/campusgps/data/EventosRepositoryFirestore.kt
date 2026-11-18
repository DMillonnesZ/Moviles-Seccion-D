package com.millones.campusgps.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.millones.campusgps.model.Evento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date

// eventos/{id} -> titulo, descripcion, fecha, lugarNombre, lat, lng, foto (Base64),
//                 autorUid, autorNombre, asistentes (lista de uid), creadoEn
class EventosRepositoryFirestore(
    db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val fotos: AlmacenFotos
) : EventosRepository {

    private val coleccion = db.collection("eventos")

    override val eventos: Flow<List<Evento>> =
        coleccion.orderBy("fecha", Query.Direction.ASCENDING).escuchar().map { instantanea ->
            val mio = auth.currentUser?.uid
            instantanea.documents.mapNotNull { doc ->
                val titulo = doc.getString("titulo") ?: return@mapNotNull null
                val fecha = doc.getTimestamp("fecha")?.toDate()?.time ?: return@mapNotNull null
                val asistentes = (doc.get("asistentes") as? List<*>)?.filterIsInstance<String>().orEmpty()
                val autorUid = doc.getString("autorUid").orEmpty()
                Evento(
                    id = doc.id,
                    titulo = titulo,
                    descripcion = doc.getString("descripcion").orEmpty(),
                    fecha = fecha,
                    lugarNombre = doc.getString("lugarNombre").orEmpty(),
                    latitud = doc.getDouble("lat"),
                    longitud = doc.getDouble("lng"),
                    foto = doc.getString("foto")?.takeIf { it.isNotBlank() },
                    autorUid = autorUid,
                    autorNombre = doc.getString("autorNombre").orEmpty(),
                    asistentes = asistentes.size,
                    asisto = mio != null && mio in asistentes,
                    esMio = mio != null && autorUid == mio
                )
            }
        }

    override suspend fun crear(
        titulo: String,
        descripcion: String,
        fecha: Long,
        lugarNombre: String,
        latitud: Double?,
        longitud: Double?,
        fotoUri: String?
    ): Result<Unit> = runCatching {
        val usuario = auth.currentUser ?: error("Inicia sesión para crear eventos")
        val datos = hashMapOf<String, Any>(
            "titulo" to titulo,
            "descripcion" to descripcion,
            "fecha" to Timestamp(Date(fecha)),
            "lugarNombre" to lugarNombre,
            "autorUid" to usuario.uid,
            "autorNombre" to nombreVisible(usuario.displayName, usuario.email),
            "asistentes" to emptyList<String>(),
            "creadoEn" to FieldValue.serverTimestamp()
        )
        if (latitud != null && longitud != null) {
            datos["lat"] = latitud
            datos["lng"] = longitud
        }
        if (fotoUri != null) datos["foto"] = fotos.comprimirBase64(fotoUri, ladoMaximo = 720)
        coleccion.add(datos).await()
    }.map { }

    override suspend fun alternarAsistencia(id: String, asisto: Boolean): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Inicia sesión para asistir")
        val cambio = if (asisto) FieldValue.arrayRemove(uid) else FieldValue.arrayUnion(uid)
        coleccion.document(id).update("asistentes", cambio).await()
    }.map { }

    override suspend fun eliminar(id: String): Result<Unit> = runCatching {
        coleccion.document(id).delete().await()
    }.map { }
}
