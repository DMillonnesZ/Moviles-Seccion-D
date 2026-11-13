package com.tecsup.pokemon.data.admin

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.tecsup.pokemon.domain.model.AccionRegistro
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.TipoAccion
import com.tecsup.pokemon.domain.model.UsuarioAdmin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

private const val USUARIOS = "usuarios"
private const val ESPERA_MS = 8_000L

// Implementación con Firestore. Cada cambio de una cuenta y su entrada en el
// registro se guardan juntos (lote): o se guardan los dos o ninguno.
class FirebaseUsuariosRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UsuariosRepository {

    // Entradas del registro de acciones (compartido con el catálogo)
    private val registroAdmin = RegistroAdmin(auth, db)

    override val uidActual: String?
        get() = auth.currentUser?.uid

    override fun usuarios(): Flow<List<UsuarioAdmin>> = callbackFlow {
        val escucha = db.collection(USUARIOS).addSnapshotListener { snap, error ->
            if (error != null) {
                close(Exception(mensajeDe(error)))
                return@addSnapshotListener
            }
            val lista = snap?.documents.orEmpty()
                .map { it.aUsuario() }
                .sortedWith(
                    compareByDescending<UsuarioAdmin> { it.rol == Rol.ADMIN }
                        .thenBy { it.nombre.lowercase() }
                )
            trySend(lista)
        }
        awaitClose { escucha.remove() }
    }

    override fun registro(limite: Long): Flow<List<AccionRegistro>> = callbackFlow {
        val escucha = db.collection(RegistroAdmin.COLECCION)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .limit(limite)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    close(Exception(mensajeDe(error)))
                    return@addSnapshotListener
                }
                trySend(snap?.documents.orEmpty().mapNotNull { it.aAccion() })
            }
        awaitClose { escucha.remove() }
    }

    override suspend fun cambiarEstado(usuario: UsuarioAdmin, activo: Boolean, motivo: String?): Result<Unit> {
        val limpio = motivo?.trim()?.takeIf { it.isNotEmpty() }
        val cambios: Map<String, Any> = if (activo) {
            mapOf("activo" to true, "motivoSuspension" to FieldValue.delete())
        } else {
            mapOf("activo" to false, "motivoSuspension" to (limpio ?: FieldValue.delete()))
        }
        return aplicar(
            usuario = usuario,
            cambios = cambios,
            tipo = if (activo) TipoAccion.ACTIVAR else TipoAccion.SUSPENDER,
            detalle = if (activo) null else limpio
        )
    }

    override suspend fun cambiarRol(usuario: UsuarioAdmin, rol: Rol): Result<Unit> =
        aplicar(
            usuario = usuario,
            cambios = mapOf("rol" to if (rol == Rol.ADMIN) "admin" else "usuario"),
            tipo = if (rol == Rol.ADMIN) TipoAccion.HACER_ADMIN else TipoAccion.QUITAR_ADMIN,
            detalle = null
        )

    override suspend fun restablecerClave(usuario: UsuarioAdmin): Result<Unit> {
        val correo = usuario.correo
            ?: return Result.failure(Exception("${usuario.nombre} aún no tiene correo registrado"))
        if (auth.currentUser == null) return Result.failure(Exception("No hay sesión activa"))
        return try {
            auth.sendPasswordResetEmail(correo).await()
            // El registro no bloquea: si falla, el correo ya se envió
            runCatching {
                registroAdmin.nuevaEntrada()
                    .set(registroAdmin.datos(usuario.uid, usuario.nombre, TipoAccion.RESTABLECER_CLAVE, correo))
            }
            Result.success(Unit)
        } catch (e: FirebaseNetworkException) {
            Result.failure(Exception("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo enviar el correo"))
        }
    }

    // ----- Ayudantes -----

    // Cambia la cuenta y anota la acción en el registro, en un solo lote
    private suspend fun aplicar(
        usuario: UsuarioAdmin,
        cambios: Map<String, Any>,
        tipo: TipoAccion,
        detalle: String?
    ): Result<Unit> {
        val admin = auth.currentUser ?: return Result.failure(Exception("No hay sesión activa"))
        if (admin.uid == usuario.uid) return Result.failure(Exception("No puedes cambiar tu propia cuenta"))
        return try {
            val lote = db.batch()
            lote.update(db.collection(USUARIOS).document(usuario.uid), cambios)
            lote.set(registroAdmin.nuevaEntrada(), registroAdmin.datos(usuario.uid, usuario.nombre, tipo, detalle))
            // Sin internet el lote queda guardado y se sube después
            withTimeoutOrNull(ESPERA_MS) { lote.commit().await() }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mensajeDe(e)))
        }
    }

    private fun mensajeDe(e: Exception): String = when {
        e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ->
            "Sin permiso: revisa las reglas de Firestore"
        e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE ->
            "No hay conexión a internet"
        else -> "No se pudo completar la acción"
    }
}

// Documento usuarios/{uid} -> UsuarioAdmin
private fun DocumentSnapshot.aUsuario(): UsuarioAdmin {
    val correo = getString("correo")
    return UsuarioAdmin(
        uid = id,
        nombre = getString("nombre")?.takeIf { it.isNotBlank() }
            ?: correo?.substringBefore("@")
            ?: "Sin nombre",
        correo = correo,
        rol = if (getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO,
        activo = getBoolean("activo") ?: true,
        motivoSuspension = getString("motivoSuspension"),
        foto = getString("foto"),
        ultimoAcceso = getTimestamp("ultimoAcceso")?.toDate()?.time
    )
}

// Documento registro_admin/{id} -> AccionRegistro (ignora tipos desconocidos)
private fun DocumentSnapshot.aAccion(): AccionRegistro? {
    val nombreTipo = getString("tipo") ?: return null
    val tipo = TipoAccion.entries.firstOrNull { it.name == nombreTipo } ?: return null
    return AccionRegistro(
        id = id,
        adminNombre = getString("adminNombre") ?: "Admin",
        usuarioNombre = getString("usuarioNombre") ?: "",
        tipo = tipo,
        detalle = getString("detalle"),
        // Mientras el servidor confirma la hora, usa una estimada
        fecha = getTimestamp("fecha", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate()?.time
    )
}