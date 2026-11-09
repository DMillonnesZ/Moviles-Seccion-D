package com.tecsup.pokemon.data.remote.dto

import com.google.gson.annotations.SerializedName

// Respuesta de GET pokemon/{id} (segundo endpoint: detalle)
data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int,                      // en decímetros
    val weight: Int,                      // en hectogramos
    val types: List<TipoSlotDto>,
    val abilities: List<HabilidadSlotDto>,
    val stats: List<StatDto>
)

data class TipoSlotDto(
    val type: RecursoDto
)

data class HabilidadSlotDto(
    val ability: RecursoDto
)

data class StatDto(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: RecursoDto
)