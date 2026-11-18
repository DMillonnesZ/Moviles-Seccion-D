package com.millones.campusgps.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.millones.campusgps.model.PersonaEnVivo
import com.millones.campusgps.model.Posicion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// ubicaciones/{uid} -> nombre, lat, lng, actualizadoEn.
// Una ubicación sin actualizar en MINUTOS_VIGENCIA se ignora (la persona cerró la app o se fue).
class UbicacionesRepositoryFirestore(
    db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UbicacionesRepository {

    private val coleccion = db.collection("ubicaciones")

    override fun personas(): Flow<List<PersonaEnVivo>> =
        coleccion.escuchar().map { instantanea ->
            val mio = auth.currentUser?.uid
            val limite = System.currentTimeMillis() - MINUTOS_VIGENCIA * 60_000L
            instantanea.documents.mapNotNull { doc ->
                if (doc.id == mio) return@mapNotNull null
                val hora = doc.getTimestamp("actualizadoEn")?.toDate()?.time ?: return@mapNotNull null
                if (hora < limite) return@mapNotNull null
                PersonaEnVivo(
                    uid = doc.id,
                    nombre = doc.getString("nombre") ?: "Estudiante",
                    latitud = doc.getDouble("lat") ?: return@mapNotNull null,
                    longitud = doc.getDouble("lng") ?: return@mapNotNull null,
                    actualizadoEn = hora
                )
            }
        }

    override suspend fun publicar(posicion: Posicion) {
        val usuario = auth.currentUser ?: return
        coleccion.document(usuario.uid).set(
            mapOf(
                "nombre" to nombreVisible(usuario.displayName, usuario.email),
                "lat" to posicion.latitud,
                "lng" to posicion.longitud,
                "actualizadoEn" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    override fun dejarDeCompartir() {
        val uid = auth.currentUser?.uid ?: return
        coleccion.document(uid).delete()
    }

    private companion object {
        const val MINUTOS_VIGENCIA = 10
    }
}
