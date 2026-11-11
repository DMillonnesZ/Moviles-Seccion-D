package com.tecsup.pokemon.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Deben terminar en /
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    private const val TCG_URL = "https://api.tcgdex.net/v2/"

    // Muestra en Logcat las peticiones (útil para depurar)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    // Un solo cliente HTTP compartido por las dos APIs
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // PokeAPI
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // TCGdex
    val tcg: TcgApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TCG_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TcgApiService::class.java)
    }
}