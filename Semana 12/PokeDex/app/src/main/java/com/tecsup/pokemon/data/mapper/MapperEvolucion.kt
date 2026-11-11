package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.local.EtapaCadenaEntity
import com.tecsup.pokemon.data.remote.dto.DetalleEvolucionDto
import com.tecsup.pokemon.data.remote.dto.EslabonDto
import com.tecsup.pokemon.domain.model.CadenaEvolutiva
import com.tecsup.pokemon.domain.model.EtapaEvolucion
import com.tecsup.pokemon.domain.model.MetodoEvolucion
import com.tecsup.pokemon.util.Traductor

// Clasifica cómo evoluciona según los datos de la PokeAPI
fun metodoDe(detalle: DetalleEvolucionDto?): MetodoEvolucion = when (detalle?.trigger?.name) {
    "level-up" -> when {
        detalle.nivelMinimo != null -> MetodoEvolucion.NIVEL
        detalle.amistadMinima != null -> MetodoEvolucion.AMISTAD
        else -> MetodoEvolucion.OTROS
    }
    "use-item" ->
        if (detalle.item?.name?.endsWith("-stone") == true) MetodoEvolucion.PIEDRA
        else MetodoEvolucion.OTROS
    "trade" -> MetodoEvolucion.INTERCAMBIO
    else -> MetodoEvolucion.OTROS
}

// Árbol de la PokeAPI -> filas para Room (funciona con ramas, ej: Eevee)
fun EslabonDto.aEtapasCadena(cadenaId: Int): List<EtapaCadenaEntity> {
    val filas = mutableListOf<EtapaCadenaEntity>()

    fun recorrer(eslabon: EslabonDto, fase: Int, condicion: String, metodo: String) {
        filas += EtapaCadenaEntity(
            cadenaId = cadenaId,
            especieId = idDesdeUrl(eslabon.species.url),
            nombre = eslabon.species.name,
            condicion = condicion,
            metodo = metodo,
            fase = fase,
            orden = filas.size
        )
        eslabon.evolucionaA.forEach { siguiente ->
            val detalle = siguiente.detalles.firstOrNull()
            recorrer(
                eslabon = siguiente,
                fase = fase + 1,
                condicion = Traductor.condicionEs(
                    disparador = detalle?.trigger?.name,
                    nivelMinimo = detalle?.nivelMinimo,
                    objeto = detalle?.item?.name,
                    objetoEquipado = detalle?.objetoEquipado?.name,
                    amistadMinima = detalle?.amistadMinima,
                    momentoDelDia = detalle?.momento?.ifBlank { null }
                ),
                metodo = metodoDe(detalle).name
            )
        }
    }

    recorrer(this, fase = 0, condicion = "", metodo = "")
    // La clave es (cadena, especie): evita duplicados si la API repite una especie
    return filas.distinctBy { it.especieId }
}

// Filas de Room -> cadenas para la UI
fun List<EtapaCadenaEntity>.aCadenas(): List<CadenaEvolutiva> =
    groupBy { it.cadenaId }.map { (id, filas) ->
        val ordenadas = filas.sortedBy { it.orden }
        CadenaEvolutiva(
            id = id,
            etapas = ordenadas.map {
                EtapaEvolucion(
                    id = it.especieId,
                    nombre = it.nombre,
                    imagenUrl = imagenPokemon(it.especieId),
                    condicion = it.condicion,
                    fase = it.fase
                )
            },
            metodos = ordenadas
                .mapNotNull { fila -> MetodoEvolucion.entries.firstOrNull { it.name == fila.metodo } }
                .distinct()
        )
    }