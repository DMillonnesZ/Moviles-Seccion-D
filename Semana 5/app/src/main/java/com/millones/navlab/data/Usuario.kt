package com.millones.navlab.data

data class Usuario(
    val id: String,
    val email: String,
    val password: String,
    val nombreCompleto: String,
    val carrera: String,
    val facultad: String,
    val telefono: String,
    val ciclo: String,
    val avatarUrl: String,
    val biografia: String
)