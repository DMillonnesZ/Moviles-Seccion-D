package com.tecsup.pokemon.domain.model

// Opciones de orden de la lista de Pokémon
enum class OrdenPokemon(val etiqueta: String) {
    NUM_ASC("Número ↑"),
    NUM_DESC("Número ↓"),
    NOMBRE_ASC("Nombre A → Z"),
    NOMBRE_DESC("Nombre Z → A")
}