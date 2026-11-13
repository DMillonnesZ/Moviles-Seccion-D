package com.tecsup.pokemon.data.remote.dto

import com.google.gson.annotations.SerializedName

// ----- GET pokemon-species/{id}: descripción, categoría, género y cadena evolutiva -----

data class EspecieDto(
    @SerializedName("flavor_text_entries") val textos: List<TextoPokedexDto>,
    val genera: List<CategoriaDto>,
    @SerializedName("gender_rate") val tasaGenero: Int,
    @SerializedName("evolution_chain") val cadenaEvolutiva: UrlDto?
)

data class TextoPokedexDto(
    @SerializedName("flavor_text") val texto: String,
    val language: RecursoDto
)

data class CategoriaDto(
    val genus: String,                    // ej: "Pokémon Tortuguita"
    val language: RecursoDto
)

data class UrlDto(
    val url: String
)

// ----- GET evolution-chain/{id} -----

data class CadenaEvolutivaDto(
    val chain: EslabonDto
)

// Una etapa de la cadena y las que vienen después
data class EslabonDto(
    val species: RecursoDto,
    @SerializedName("evolution_details") val detalles: List<DetalleEvolucionDto>,
    @SerializedName("evolves_to") val evolucionaA: List<EslabonDto>
)

// Cómo llega a esta etapa
data class DetalleEvolucionDto(
    val trigger: RecursoDto?,
    @SerializedName("min_level") val nivelMinimo: Int?,
    val item: RecursoDto?,
    @SerializedName("held_item") val objetoEquipado: RecursoDto?,
    @SerializedName("min_happiness") val amistadMinima: Int?,
    @SerializedName("time_of_day") val momento: String?
)

// ----- GET type/{nombre}: relaciones de daño para calcular debilidades -----

data class TipoRelacionesDto(
    @SerializedName("damage_relations") val relaciones: RelacionesDanoDto
)

data class RelacionesDanoDto(
    @SerializedName("double_damage_from") val dobleDe: List<RecursoDto>,
    @SerializedName("half_damage_from") val mitadDe: List<RecursoDto>,
    @SerializedName("no_damage_from") val nadaDe: List<RecursoDto>
)

// ----- GET ability/{nombre}: nombre de la habilidad en cada idioma -----

data class HabilidadDto(
    val names: List<NombreIdiomaDto>
)