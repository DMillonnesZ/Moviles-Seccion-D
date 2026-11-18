package com.millones.campusgps.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.millones.campusgps.model.Comentario
import com.millones.campusgps.model.TipoObjetivo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// comentarios/{id} -> texto, calificacion (1-5), tipo ("lugar" | "evento"), objetivoId,
//                     objetivoNombre, autorUid, autorNombre, creadoEn (hora del servidor)
class ComentariosRepositoryFirestore(
    db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ComentariosRepository {

    private val coleccion = db.collection("comentarios")

    override val comentarios: Flow<List<Comentario>> =
        coleccion.orderBy("creadoEn", Query.Direction.DESCENDING)
            .limit(MAXIMO)
            .escuchar()
            .map { instantanea ->
                val mio = auth.currentUser?.uid
                instantanea.documents.mapNotNull { doc ->
                    val tipo = TipoObjetivo.de(doc.getString("tipo")) ?: return@mapNotNull null
                    val autorUid = doc.getString("autorUid").orEmpty()
                    Comentario(
                        id = doc.id,
                        texto = doc.getString("texto").orEmpty(),
                        calificacion = (doc.getLong("calificacion") ?: 0L).toInt().coerceIn(1, 5),
                        autorUid = autorUid,
                        autorNombre = doc.getString("autorNombre").orEmpty(),
                        // Mientras el servidor confirma la hora, se usa la del celular
                        creadoEn = doc.getTimestamp("creadoEn")?.toDate()?.time ?: System.currentTimeMillis(),
                        tipo = tipo,
                        objetivoId = doc.getString("objetivoId").orEmpty(),
                        objetivoNombre = doc.getString("objetivoNombre").orEmpty(),
                        esMio = mio != null && autorUid == mio
                    )
                }
            }

    override suspend fun publicar(
        tipo: TipoObjetivo,
        objetivoId: String,
        objetivoNombre: String,
        calificacion: Int,
        texto: String
    ): Result<Unit> = runCatching {
        val usuario = auth.currentUser ?: error("Inicia sesión para opinar")
        coleccion.add(
            mapOf(
                "texto" to texto,
                "calificacion" to calificacion,
                "tipo" to tipo.valor,
                "objetivoId" to objetivoId,
                "objetivoNombre" to objetivoNombre,
                "autorUid" to usuario.uid,
                "autorNombre" to nombreVisible(usuario.displayName, usuario.email),
                "creadoEn" to FieldValue.serverTimestamp()
            )
        ).await()
    }.map { }

    override suspend fun eliminar(id: String): Result<Unit> = runCatching {
        coleccion.document(id).delete().await()
    }.map { }

    private companion object {
        const val MAXIMO = 200L
    }
}
