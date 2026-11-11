package com.tecsup.pokemon.domain.model

// Carta del juego de cartas coleccionables
data class Carta(
    val id: String,
    val nombre: String,
    val imagenPequena: String,   // para el carrusel (245 x 337)
    val imagenGrande: String     // para verla en grande (600 x 825)
)