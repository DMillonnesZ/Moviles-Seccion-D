package com.tecsup.pokemon.domain.model

// Cómo evoluciona un Pokémon (para filtrar las cadenas)
enum class MetodoEvolucion(val etiqueta: String) {
    NIVEL("Nivel"),
    PIEDRA("Piedra"),
    INTERCAMBIO("Intercambio"),
    AMISTAD("Amistad"),
    OTROS("Otros")
}