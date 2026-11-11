package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.local.ItemConTipos
import com.tecsup.pokemon.data.local.ItemEntity
import com.tecsup.pokemon.data.local.TipoConCantidad
import com.tecsup.pokemon.data.local.TipoEntity
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

// ----- DTO -> Entity (para guardar en Room) -----

fun RecursoDto.aEntity(esFavorito: Boolean = false): ItemEntity {
    val id = idDesdeUrl(url)
    return ItemEntity(id = id, nombre = name, imagenUrl = imagenPokemon(id), esFavorito = esFavorito)
}

fun TipoDetalleDto.aEntity(): TipoEntity = TipoEntity(
    nombre = name,
    nombreEs = names.firstOrNull { it.language.name == "es" }?.name
        ?: name.replaceFirstChar { it.uppercase() },
    iconoUrl = iconoTipo(id)
)

// ----- Entity -> Modelo (para mostrar en la UI) -----

fun ItemEntity.aModelo(): Item = Item(
    id = id,
    nombre = nombre,
    imagenUrl = imagenUrl,
    esFavorito = esFavorito
)

// Pokémon con sus tipos: "fire,flying" -> ["fire", "flying"]
fun ItemConTipos.aModelo(): Item = item.aModelo().copy(
    tipos = tipos?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
)

fun TipoEntity.aModelo(): Tipo = Tipo(
    nombre = nombre,
    nombreEs = nombreEs,
    iconoUrl = iconoUrl
)

fun TipoConCantidad.aModelo(): Tipo = Tipo(
    nombre = tipo.nombre,
    nombreEs = tipo.nombreEs,
    iconoUrl = tipo.iconoUrl,
    cantidad = cantidad
)

// ----- DTO -> Modelo (el detalle no se guarda, se pide al tocar) -----

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