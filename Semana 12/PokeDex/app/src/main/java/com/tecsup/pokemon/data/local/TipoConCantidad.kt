package com.tecsup.pokemon.data.local

import androidx.room.Embedded

// Resultado de la consulta: un tipo + cuántos Pokémon tiene
data class TipoConCantidad(
    @Embedded val tipo: TipoEntity,
    val cantidad: Int
)