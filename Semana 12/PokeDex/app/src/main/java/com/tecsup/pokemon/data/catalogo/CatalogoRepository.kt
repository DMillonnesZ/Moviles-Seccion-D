package com.tecsup.pokemon.data.catalogo

import com.tecsup.pokemon.domain.model.Anuncio
import com.tecsup.pokemon.domain.model.Deshabilitados
import kotlinx.coroutines.flow.Flow

// Catálogo global: Pokémon y tipos descontinuados + anuncio para todos
interface CatalogoRepository {
    // En vivo; vacío si no hay nada descontinuado o no se puede leer
    val deshabilitados: Flow<Deshabilitados>

    // Lectura única (para el Detalle)
    suspend fun leerDeshabilitados(): Deshabilitados

    // En vivo; null = sin anuncio
    val anuncio: Flow<Anuncio?>

    // ----- Solo admins -----
    suspend fun cambiarPokemon(id: Int, nombre: String, disponible: Boolean): Result<Unit>
    suspend fun cambiarTipo(nombre: String, nombreEs: String, disponible: Boolean): Result<Unit>
    suspend fun publicarAnuncio(texto: String): Result<Unit>
    suspend fun quitarAnuncio(): Result<Unit>
}