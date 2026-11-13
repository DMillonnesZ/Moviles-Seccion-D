package com.tecsup.pokemon.domain.model

// Lo que puede hacer un admin con una cuenta
enum class TipoAccion { SUSPENDER, ACTIVAR, HACER_ADMIN, QUITAR_ADMIN, RESTABLECER_CLAVE }

// Una entrada del registro de acciones (colección registro_admin)
data class AccionRegistro(
    val id: String,
    val adminNombre: String,
    val usuarioNombre: String,
    val tipo: TipoAccion,
    val detalle: String?,   // motivo de la suspensión o correo del restablecimiento
    val fecha: Long?        // milisegundos
)