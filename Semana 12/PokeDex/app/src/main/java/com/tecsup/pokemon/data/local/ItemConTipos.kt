package com.tecsup.pokemon.data.local

import androidx.room.Embedded

// Resultado de la consulta paginada: un Pokémon + sus tipos separados por coma ("fire,flying")
data class ItemConTipos(
    @Embedded val item: ItemEntity,
    val tipos: String?
)