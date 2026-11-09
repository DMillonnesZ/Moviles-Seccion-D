package com.tecsup.pokemon.domain.model

data class Item(
    val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val esFavorito: Boolean = false
)

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