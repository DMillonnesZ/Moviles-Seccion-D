package com.millones.murocampus.data.remote

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }

    private val cliente = OkHttpClient.Builder()
        // TODO Paso 4 (1): addInterceptor(HttpLoggingInterceptor().apply { level = ... BODY })
        .build()

    val api: PostApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(cliente)
            // TODO Paso 4 (2): addConverterFactory(json.asConverterFactory(...))
            .build()
            .create(PostApi::class.java)
    }
}