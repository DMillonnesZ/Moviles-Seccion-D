package com.millones.campusgps.data

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    // Correo del usuario conectado, o null si no hay sesión
    val usuarioActivo: StateFlow<String?>

    // Nombre que el usuario escribió al registrarse, o null si no tiene
    val nombreActivo: StateFlow<String?>

    suspend fun iniciarSesion(correo: String, clave: String): Result<Unit>
    suspend fun registrar(nombre: String, correo: String, clave: String): Result<Unit>
    suspend fun recuperarClave(correo: String): Result<Unit>
    fun cerrarSesion()
}
