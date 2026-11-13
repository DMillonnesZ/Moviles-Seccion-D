package com.tecsup.pokemon.domain.model

data class Tipo(
    val nombre: String,     // "fire" (como en la API)
    val nombreEs: String,   // "Fuego"
    val iconoUrl: String,
    val cantidad: Int = 0   // cuántos Pokémon de este tipo hay guardados
)