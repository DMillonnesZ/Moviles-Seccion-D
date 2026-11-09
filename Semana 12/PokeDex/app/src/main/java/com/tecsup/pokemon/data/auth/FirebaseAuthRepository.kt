package com.tecsup.pokemon.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.tasks.await

// Implementación real del contrato con Firebase Auth + Firestore
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val usuario = auth.signInWithEmailAndPassword(correo, clave).await().user
            ?: throw IllegalStateException("No se pudo iniciar sesión")
        Result.success(leerSesion(usuario.uid, correo))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun sesionActual(): Sesion? {
        // Firebase Auth recuerda la sesión aunque se cierre la app
        val usuario = auth.currentUser ?: return null
        return try {
            leerSesion(usuario.uid, usuario.email ?: "")
        } catch (e: Exception) {
            // Si no se puede leer el rol (sin internet y sin caché), entra como usuario
            Sesion(usuario.uid, usuario.email ?: "", Rol.USUARIO)
        }
    }

    override fun cerrarSesion() = auth.signOut()

    // El rol se lee de Firestore: usuarios/{uid}.rol
    private suspend fun leerSesion(uid: String, correo: String): Sesion {
        val doc = db.collection("usuarios").document(uid).get().await()
        val rol = if (doc.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO
        return Sesion(uid, correo, rol)
    }
}