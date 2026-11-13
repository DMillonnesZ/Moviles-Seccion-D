package com.tecsup.pokemon.data.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.tecsup.pokemon.domain.model.PerfilUsuario
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

// Tiempo máximo esperando a Firestore; sin internet el cambio queda guardado
// en el teléfono y Firebase lo sube solo cuando vuelve la conexión
private const val ESPERA_GUARDADO_MS = 8_000L

// Implementación real del contrato con Firebase Auth + Firestore.
// Documento de cada usuario: usuarios/{uid} con rol, nombre, correo, activo,
// motivoSuspension, foto, creadoEn y ultimoAcceso.
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val usuario = auth.signInWithEmailAndPassword(correo, clave).await().user
            ?: throw IllegalStateException("No se pudo iniciar sesión")
        val doc = documento(usuario.uid).get().await()

        // Cuenta suspendida por un admin: se cierra la sesión y no entra
        if (doc.getBoolean("activo") == false) {
            auth.signOut()
            throw CuentaSuspendidaException(doc.getString("motivoSuspension"))
        }

        registrarAcceso(usuario.uid, correo)
        Result.success(Sesion(usuario.uid, correo, rolDe(doc)))
    } catch (e: CuentaSuspendidaException) {
        Result.failure(e)
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(Exception("Correo o contraseña incorrectos"))
    } catch (e: FirebaseAuthInvalidUserException) {
        Result.failure(Exception("Correo o contraseña incorrectos"))
    } catch (e: FirebaseNetworkException) {
        Result.failure(Exception("No hay conexión a internet"))
    } catch (e: Exception) {
        Result.failure(Exception("No se pudo iniciar sesión"))
    }

    // Crea la cuenta y su documento en Firestore con rol "usuario" y activa
    override suspend fun registrar(nombre: String, correo: String, clave: String): Result<Sesion> = try {
        val usuario = auth.createUserWithEmailAndPassword(correo, clave).await().user
            ?: throw IllegalStateException("No se pudo crear la cuenta")

        documento(usuario.uid)
            .set(
                mapOf(
                    "rol" to "usuario",
                    "nombre" to nombre,
                    "correo" to correo,
                    "activo" to true,
                    "creadoEn" to FieldValue.serverTimestamp(),
                    "ultimoAcceso" to FieldValue.serverTimestamp()
                )
            )
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
        val correo = usuario.email ?: ""
        val doc = try {
            documento(usuario.uid).get().await()
        } catch (e: Exception) {
            // Sin internet y sin caché: entra como usuario
            return Sesion(usuario.uid, correo, Rol.USUARIO)
        }

        // La suspendieron mientras la app estaba cerrada
        if (doc.getBoolean("activo") == false) {
            auth.signOut()
            throw CuentaSuspendidaException(doc.getString("motivoSuspension"))
        }

        registrarAcceso(usuario.uid, correo)
        return Sesion(usuario.uid, correo, rolDe(doc))
    }

    override fun cerrarSesion() = auth.signOut()

    // ----- Perfil -----

    // Escucha el documento usuarios/{uid}: cada cambio (nombre, foto, suspensión) llega al instante
    override fun perfil(): Flow<PerfilUsuario?> = callbackFlow {
        val usuario = auth.currentUser
        if (usuario == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val correo = usuario.email ?: ""
        // Datos básicos por si Firestore no responde
        val basico = PerfilUsuario(
            uid = usuario.uid,
            correo = correo,
            nombre = correo.substringBefore("@"),
            rol = Rol.USUARIO,
            creadoEn = usuario.metadata?.creationTimestamp
        )
        val registro = documento(usuario.uid)
            .addSnapshotListener { doc, error ->
                if (error != null || doc == null) {
                    trySend(basico)
                    return@addSnapshotListener
                }
                trySend(
                    basico.copy(
                        nombre = doc.getString("nombre")?.takeIf { it.isNotBlank() } ?: basico.nombre,
                        rol = rolDe(doc),
                        foto = doc.getString("foto"),
                        activo = doc.getBoolean("activo") ?: true,
                        motivoSuspension = doc.getString("motivoSuspension")
                    )
                )
            }
        awaitClose { registro.remove() }
    }

    override suspend fun actualizarNombre(nombre: String): Result<Unit> =
        guardarEnUsuario(mapOf("nombre" to nombre))

    override suspend fun actualizarFoto(fotoBase64: String?): Result<Unit> =
        guardarEnUsuario(mapOf("foto" to (fotoBase64 ?: FieldValue.delete())))

    override suspend fun cambiarClave(actual: String, nueva: String): Result<Unit> {
        val usuario = auth.currentUser ?: return Result.failure(Exception("No hay sesión activa"))
        val correo = usuario.email ?: return Result.failure(Exception("No hay sesión activa"))
        return try {
            // Firebase pide volver a confirmar la identidad antes de cambiar la clave
            usuario.reauthenticate(EmailAuthProvider.getCredential(correo, actual)).await()
            usuario.updatePassword(nueva).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("La contraseña nueva es muy débil"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("La contraseña actual es incorrecta"))
        } catch (e: FirebaseTooManyRequestsException) {
            Result.failure(Exception("Demasiados intentos, espera un momento"))
        } catch (e: FirebaseNetworkException) {
            Result.failure(Exception("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo cambiar la contraseña"))
        }
    }

    // ----- Ayudantes -----

    private fun documento(uid: String): DocumentReference = db.collection("usuarios").document(uid)

    private fun rolDe(doc: DocumentSnapshot): Rol =
        if (doc.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO

    // Guarda el correo (para que el admin lo vea) y la hora del último acceso.
    // No espera la respuesta: si no hay internet, Firestore lo sube después.
    private fun registrarAcceso(uid: String, correo: String) {
        documento(uid).set(
            mapOf("correo" to correo, "ultimoAcceso" to FieldValue.serverTimestamp()),
            SetOptions.merge()
        )
    }

    // Guarda solo los campos indicados sin tocar el rol ni el estado
    private suspend fun guardarEnUsuario(datos: Map<String, Any>): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No hay sesión activa"))
        return try {
            val tarea = documento(uid).set(datos, SetOptions.merge())
            withTimeoutOrNull(ESPERA_GUARDADO_MS) { tarea.await() }
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            val mensaje = if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                "No tienes permiso para guardar los cambios"
            } else {
                "No se pudo guardar"
            }
            Result.failure(Exception(mensaje))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo guardar"))
        }
    }
}