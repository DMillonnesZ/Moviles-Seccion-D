package com.tecsup.pokemon.data.local

import androidx.room.Entity
import androidx.room.Index

// Relación muchos a muchos: un Pokémon puede tener 2 tipos
// y un tipo tiene muchos Pokémon
@Entity(
    tableName = "item_tipo",
    primaryKeys = ["itemId", "tipo"],
    indices = [Index("tipo")]
)
data class ItemTipoEntity(
    val itemId: Int,
    val tipo: String
)