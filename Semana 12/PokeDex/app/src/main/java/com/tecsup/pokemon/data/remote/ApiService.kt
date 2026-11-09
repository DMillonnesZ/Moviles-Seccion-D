package com.tecsup.pokemon.data.remote

import com.tecsup.pokemon.data.remote.dto.PokemonDto
import com.tecsup.pokemon.data.remote.dto.TipoDetalleDto
import com.tecsup.pokemon.data.remote.dto.TipoListaDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Todos los tipos (limit para traerlos en una sola página)
    @GET("type/")
    suspend fun listarTipos(@Query("limit") limit: Int = 100): TipoListaDto

    // Primer endpoint: datos del tipo y sus Pokémon
    @GET("type/{nombre}")
    suspend fun listarPorTipo(@Path("nombre") nombre: String): TipoDetalleDto

    // Segundo endpoint: detalle de un Pokémon
    @GET("pokemon/{id}")
    suspend fun obtenerPokemon(@Path("id") id: Int): PokemonDto
}