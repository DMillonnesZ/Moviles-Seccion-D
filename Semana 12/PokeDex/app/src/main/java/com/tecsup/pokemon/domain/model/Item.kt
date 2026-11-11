package com.tecsup.pokemon.domain.model

// Pokémon en listas y favoritos
data class Item(
    val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val esFavorito: Boolean = false,
    val tipos: List<String> = emptyList()   // "fire", "flying"...
)

// Pokémon en el detalle
data class ItemDetalle(
    val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val tipos: List<String>,
    val alturaM: Double,
    val pesoKg: Double,
    val habilidades: List<String>,
    val stats: Map<String, Int>
)