package com.tecsup.pokemon.domain.model

// Usuario tal como lo ve el admin (documento usuarios/{uid})
data class UsuarioAdmin(
    val uid: String,
    val nombre: String,
    val correo: String?,                  // null si aún no inició sesión con esta versión
    val rol: Rol,
    val activo: Boolean,                  // false = cuenta suspendida
    val motivoSuspension: String?,
    val foto: String?,                    // Base64 (la misma del Perfil)
    val ultimoAcceso: Long?               // milisegundos (null = nunca)
)