package com.tecsup.pokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de tipos (fire, water...)
@Entity(tableName = "tipos")
data class TipoEntity(
    @PrimaryKey val nombre: String,
    val nombreEs: String,
    val iconoUrl: String
)