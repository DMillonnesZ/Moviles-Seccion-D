package com.tecsup.pokemon.domain.model

// Pokémon y tipos que el admin retiró del catálogo (documento catalogo/deshabilitados)
data class Deshabilitados(
    val pokemon: Set<Int> = emptySet(),
    val tipos: Set<String> = emptySet()
)

// Mensaje que el admin publica para todos (documento catalogo/anuncio)
data class Anuncio(
    val texto: String,
    val autor: String,
    val fecha: Long?
)