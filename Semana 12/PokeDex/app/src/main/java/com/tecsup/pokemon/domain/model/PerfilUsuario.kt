package com.tecsup.pokemon.domain.model

// Datos del perfil del entrenador (Firebase Auth + documento usuarios/{uid})
data class PerfilUsuario(
    val uid: String,
    val correo: String,
    val nombre: String,
    val rol: Rol,
    val foto: String? = null,             // JPEG de 256x256 en Base64 (null = sin foto)
    val creadoEn: Long? = null,           // Fecha de creación de la cuenta en milisegundos
    val activo: Boolean = true,           // false = cuenta suspendida por un admin
    val motivoSuspension: String? = null  // Lo que escribió el admin al suspender
)