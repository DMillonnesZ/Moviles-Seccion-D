package com.millones.campusgps.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.millones.campusgps.model.Perfil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// usuarios/{uid} -> nombre, carrera, foto (Base64 de ~320 px; sin Storage ni plan Blaze)
class PerfilRepositoryFirestore(
    db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val fotos: AlmacenFotos
) : PerfilRepository {

    private val coleccion = db.collection("usuarios")

    override val perfiles: Flow<Map<String, Perfil>> =
        coleccion.escuchar().map { instantanea ->
            instantanea.documents.associate { doc ->
                doc.id to Perfil(
                    uid = doc.id,
                    nombre = doc.getString("nombre").orEmpty(),
                    carrera = doc.getString("carrera").orEmpty(),
                    foto = doc.getString("foto")?.takeIf { it.isNotBlank() }
                )
            }
        }

    override fun miUid(): String? = auth.currentUser?.uid

    override suspend fun guardarDatos(nombre: String, carrera: String): Result<Unit> = runCatching {
        val uid = miUid() ?: error("Inicia sesión para editar tu perfil")
        coleccion.document(uid)
            .set(mapOf("nombre" to nombre, "carrera" to carrera), SetOptions.merge())
            .await()
    }.map { }

    override suspend fun cambiarFoto(fotoUri: String): Result<Unit> = runCatching {
        val uid = miUid() ?: error("Inicia sesión para cambiar tu foto")
        val base64 = fotos.comprimirBase64(fotoUri, ladoMaximo = 320, calidad = 75)
        coleccion.document(uid).set(mapOf("foto" to base64), SetOptions.merge()).await()
    }.map { }

    override suspend fun quitarFoto(): Result<Unit> = runCatching {
        val uid = miUid() ?: error("Inicia sesión para cambiar tu foto")
        coleccion.document(uid).set(mapOf("foto" to FieldValue.delete()), SetOptions.merge()).await()
    }.map { }
}
