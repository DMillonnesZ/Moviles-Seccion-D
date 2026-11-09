package com.tecsup.pokemon.data.remote.dto

// Respuesta de GET type/
data class TipoListaDto(
    val results: List<RecursoDto>
)

// Respuesta de GET type/{nombre}
data class TipoDetalleDto(
    val id: Int,
    val name: String,
    val names: List<NombreIdiomaDto>,     // nombre del tipo en cada idioma
    val pokemon: List<PokemonEnTipoDto>
)

data class NombreIdiomaDto(
    val name: String,                     // ej: "Fuego"
    val language: RecursoDto              // ej: language.name = "es"
)

data class PokemonEnTipoDto(
    val pokemon: RecursoDto
)

// Par nombre + url que la PokeAPI repite en muchas respuestas
data class RecursoDto(
    val name: String,
    val url: String
)