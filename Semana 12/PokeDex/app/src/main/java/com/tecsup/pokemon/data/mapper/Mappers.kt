package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.remote.dto.PokemonDto
import com.tecsup.pokemon.data.remote.dto.RecursoDto
import com.tecsup.pokemon.data.remote.dto.TipoDetalleDto
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.domain.model.Tipo

// ".../pokemon/6/" -> 6
fun idDesdeUrl(url: String): Int = url.trimEnd('/').substringAfterLast('/').toInt()

fun imagenPokemon(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

fun iconoTipo(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/types/generation-ix/scarlet-violet/small/$id.png"

// Pokémon dentro de un tipo -> Item de la UI
fun RecursoDto.aItem(): Item {
    val id = idDesdeUrl(url)
    return Item(id = id, nombre = name, imagenUrl = imagenPokemon(id))
}

// Tipo de la API -> Tipo del dominio (nombre en español si existe)
fun TipoDetalleDto.aTipo(): Tipo = Tipo(
    nombre = name,
    nombreEs = names.firstOrNull { it.language.name == "es" }?.name
        ?: name.replaceFirstChar { it.uppercase() },
    iconoUrl = iconoTipo(id)
)

// Detalle de la API -> ItemDetalle (altura en m y peso en kg)
fun PokemonDto.aDetalle(): ItemDetalle = ItemDetalle(
    id = id,
    nombre = name,
    imagenUrl = imagenPokemon(id),
    tipos = types.map { it.type.name },
    alturaM = height / 10.0,
    pesoKg = weight / 10.0,
    habilidades = abilities.map { it.ability.name },
    stats = stats.associate { it.stat.name to it.baseStat }
)