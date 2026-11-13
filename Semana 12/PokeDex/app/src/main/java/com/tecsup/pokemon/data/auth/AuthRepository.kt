package com.tecsup.pokemon.data.auth

import com.tecsup.pokemon.domain.model.PerfilUsuario
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun registrar(nombre: String, correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?
    fun cerrarSesion()

    // ----- Perfil -----
    // Perfil del usuario actual; avisa cada vez que cambia
    fun perfil(): Flow<PerfilUsuario?>
    suspend fun actualizarNombre(nombre: String): Result<Unit>
    // null = quitar la foto
    suspend fun actualizarFoto(fotoBase64: String?): Result<Unit>
    // Pide la clave actual para confirmar que es el dueño de la cuenta
    suspend fun cambiarClave(actual: String, nueva: String): Result<Unit>
}