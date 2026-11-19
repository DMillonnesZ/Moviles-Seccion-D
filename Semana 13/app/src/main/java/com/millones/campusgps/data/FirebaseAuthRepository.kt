package com.millones.campusgps.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    private val _usuario = MutableStateFlow(auth.currentUser?.email)
    override val usuarioActivo: StateFlow<String?> = _usuario.asStateFlow()

    private val _nombre = MutableStateFlow(auth.currentUser?.displayName)
    override val nombreActivo: StateFlow<String?> = _nombre.asStateFlow()

    init {
        auth.addAuthStateListener {
            _usuario.value = it.currentUser?.email
            _nombre.value = it.currentUser?.displayName
        }
    }

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Unit> =
        try {
            auth.signInWithEmailAndPassword(correo, clave).await()
            // Se actualiza ya, sin esperar al listener, para que el saludo tenga el nombre
            _usuario.value = auth.currentUser?.email
            _nombre.value = auth.currentUser?.displayName
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mensajeDe(e)))
        }

    // Crea la cuenta y guarda el nombre en el perfil de Firebase Auth (no necesita Firestore)
    override suspend fun registrar(nombre: String, correo: String, clave: String): Result<Unit> =
        try {
            val resultado = auth.createUserWithEmailAndPassword(correo, clave).await()
            val perfil = UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build()
            resultado.user?.updateProfile(perfil)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mensajeDe(e)))
        }

    // Firebase envía al correo un enlace para crear una contraseña nueva
    override suspend fun recuperarClave(correo: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(correo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mensajeDe(e)))
        }

    // Firebase crea la cuenta la primera vez y después solo inicia sesión.
    // El nombre viene de la cuenta de Google.
    override suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> =
        try {
            val credencial = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credencial).await()
            _usuario.value = auth.currentUser?.email
            _nombre.value = auth.currentUser?.displayName
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mensajeDe(e)))
        }

    override suspend fun actualizarNombre(nombre: String): Result<Unit> =
        try {
            val usuario = auth.currentUser ?: error("Sin sesión")
            val perfil = UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build()
            usuario.updateProfile(perfil).await()
            // El listener de sesión no avisa este cambio: se actualiza aquí
            _nombre.value = nombre
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mensajeDe(e)))
        }

    override fun cerrarSesion() {
        auth.signOut()
    }

    // Traduce los códigos de Firebase a mensajes en español
    private fun mensajeDe(e: Exception): String =
        when ((e as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido"
            "ERROR_WRONG_PASSWORD",
            "ERROR_USER_NOT_FOUND",
            "ERROR_INVALID_CREDENTIAL",
            "INVALID_LOGIN_CREDENTIALS" -> "Correo o contraseña incorrectos"
            "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Ese correo ya está registrado"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Sin conexión. Revisa tu internet e intenta de nuevo"
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Espera un momento"
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
                "Ese correo ya tiene cuenta con contraseña. Ingresa con tu correo"
            "ERROR_USER_DISABLED" -> "Esta cuenta está deshabilitada"
            else -> "No se pudo completar la operación. Intenta de nuevo"
        }
}
