package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.domain.model.CadenaEvolutiva
import kotlinx.coroutines.flow.Flow

interface EvolucionRepository {
    // Cadenas guardadas en Room (funciona sin internet)
    val cadenas: Flow<List<CadenaEvolutiva>>

    // Tipos de cada Pokémon: 7 -> ["water"] (para los colores)
    val tiposPorPokemon: Flow<Map<Int, List<String>>>

    suspend fun cantidadGuardada(): Int

    // Descarga todas las cadenas; avisa el avance (hechas, total)
    suspend fun descargar(onProgreso: (hechas: Int, total: Int) -> Unit): Result<Int>
}