package com.tecsup.pokemon.data.remote

import com.tecsup.pokemon.data.remote.dto.CartaDto
import retrofit2.http.GET
import retrofit2.http.Query

// Segunda API: TCGdex (cartas del juego de cartas coleccionables)
interface TcgApiService {

    // Busca cartas cuyo nombre contenga el texto (en inglés, donde hay más ilustraciones)
    @GET("en/cards")
    suspend fun buscarCartas(@Query("name") nombre: String): List<CartaDto>
}