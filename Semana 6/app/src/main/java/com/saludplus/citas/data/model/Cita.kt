package com.saludplus.citas.data.model

data class Cita(
    val id: Int,
    val correoUsuario: String,
    val medicoId: Int,
    val especialidadId: Int,
    val fecha: String,
    val hora: String
)