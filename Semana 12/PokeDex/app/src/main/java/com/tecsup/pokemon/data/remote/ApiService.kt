package com.tecsup.pokemon.data.remote

import com.tecsup.pokemon.data.remote.dto.CadenaEvolutivaDto
import com.tecsup.pokemon.data.remote.dto.EspecieDto
import com.tecsup.pokemon.data.remote.dto.HabilidadDto
import com.tecsup.pokemon.data.remote.dto.PokemonDto
import com.tecsup.pokemon.data.remote.dto.TipoDetalleDto
import com.tecsup.pokemon.data.remote.dto.TipoListaDto
import com.tecsup.pokemon.data.remote.dto.TipoRelacionesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

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

    // ----- Detalle completo -----

    // Descripción, categoría, género y URL de la cadena evolutiva
    @GET("pokemon-species/{id}")
    suspend fun obtenerEspecie(@Path("id") id: Int): EspecieDto

    // Cadena evolutiva (la URL completa viene en la especie)
    @GET
    suspend fun obtenerCadenaEvolutiva(@Url url: String): CadenaEvolutivaDto

    // Relaciones de daño de un tipo (para las debilidades)
    @GET("type/{nombre}")
    suspend fun obtenerRelacionesTipo(@Path("nombre") nombre: String): TipoRelacionesDto

    // Nombre de una habilidad en cada idioma
    @GET("ability/{nombre}")
    suspend fun obtenerHabilidad(@Path("nombre") nombre: String): HabilidadDto
}