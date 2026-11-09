package com.tecsup.pokemon.data.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(Exception("Correo o contraseña incorrectos"))
    } catch (e: FirebaseAuthInvalidUserException) {
        Result.failure(Exception("Correo o contraseña incorrectos"))
    } catch (e: FirebaseNetworkException) {
        Result.failure(Exception("No hay conexión a internet"))
    } catch (e: Exception) {
        Result.failure(Exception("No se pudo iniciar sesión"))
    }

    // Crea la cuenta y su documento en Firestore con rol "usuario"
    override suspend fun registrar(nombre: String, correo: String, clave: String): Result<Sesion> = try {
        val usuario = auth.createUserWithEmailAndPassword(correo, clave).await().user
            ?: throw IllegalStateException("No se pudo crear la cuenta")

        db.collection("usuarios").document(usuario.uid)
            .set(mapOf("rol" to "usuario", "nombre" to nombre))
            .await()

        Result.success(Sesion(usuario.uid, correo, Rol.USUARIO))
    } catch (e: FirebaseAuthWeakPasswordException) {
        Result.failure(Exception("La contraseña es muy débil"))
    } catch (e: FirebaseAuthUserCollisionException) {
        Result.failure(Exception("Ese correo ya está registrado"))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(Exception("El correo no es válido"))
    } catch (e: FirebaseNetworkException) {
        Result.failure(Exception("No hay conexión a internet"))
    } catch (e: Exception) {
        Result.failure(Exception("No se pudo crear la cuenta"))
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