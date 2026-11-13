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
    val stats: Map<String, Int>,
    val descripcion: String = "",
    val categoria: String = "",                     // "Pokémon Tortuguita"
    val genero: Pair<Double, Double>? = null,       // (% macho, % hembra) o null si no tiene
    val debilidades: List<Debilidad> = emptyList(),
    val evolucion: List<EtapaEvolucion> = emptyList()
)

// Tipo que le hace más daño: x2 o x4
data class Debilidad(
    val tipo: String,           // "fire"
    val multiplicador: Double   // 2.0 o 4.0
)

// Una etapa de la cadena evolutiva
data class EtapaEvolucion(
    val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val condicion: String,      // cómo llega a esta etapa: "Nv. 16", "Piedra Fuego"...
    val fase: Int               // 0 = básico, 1 = primera evolución, 2 = segunda
)