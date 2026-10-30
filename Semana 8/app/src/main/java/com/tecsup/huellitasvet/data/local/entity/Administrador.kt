package com.tecsup.huellitasvet.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "administradores",
    indices = [Index(value = ["codigo"], unique = true), Index(value = ["correo"], unique = true)]
)
data class Administrador(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,      // ej. "ADM001"
    val nombre: String,
    val apellido: String,
    val correo: String,
    val clave: String        // contraseña
)
