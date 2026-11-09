package com.tecsup.pokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de Pokémon
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val esFavorito: Boolean = false
)