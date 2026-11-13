package com.tecsup.millones.model

data class Usuario(
    val nombre: String,
    val correo: String
)

val usuarioActual = Usuario(
    nombre = "Daniel Millones",
    correo = "daniel.millones@tecsup.edu.pe"
)
