package com.tecsup.pokemon.data.admin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.pokemon.domain.model.TipoAccion
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

// Arma las entradas del registro de acciones (colección registro_admin).
// La usan la gestión de cuentas y la del catálogo.
class RegistroAdmin(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    // Documento nuevo dentro de registro_admin
    fun nuevaEntrada(): DocumentReference = db.collection(COLECCION).document()

    // Quién lo hizo, sobre qué, qué hizo y cuándo
    suspend fun datos(
        objetivoId: String?,
        objetivoNombre: String,
        tipo: TipoAccion,
        detalle: String?
    ): Map<String, Any?> = mapOf(
        "adminUid" to auth.currentUser?.uid,
        "adminNombre" to nombreAdmin(),
        "usuarioUid" to objetivoId,
        "usuarioNombre" to objetivoNombre,
        "tipo" to tipo.name,
        "detalle" to detalle,
        "fecha" to FieldValue.serverTimestamp()
    )

    // Nombre del admin actual (si no tiene, la parte del correo antes de @)
    suspend fun nombreAdmin(): String {
        val admin = auth.currentUser ?: return "Admin"
        val nombre = runCatching {
            withTimeoutOrNull(3_000) {
                db.collection("usuarios").document(admin.uid).get().await().getString("nombre")
            }
        }.getOrNull()
        return nombre?.takeIf { it.isNotBlank() } ?: admin.email?.substringBefore("@") ?: "Admin"
    }

    companion object {
        const val COLECCION = "registro_admin"
    }
}