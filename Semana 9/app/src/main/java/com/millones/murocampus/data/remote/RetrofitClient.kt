package com.millones.murocampus.data.remote

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }

    // Agrega Accept: application/json a todas las peticiones
    private val encabezadoAccept = Interceptor { chain ->
        val peticion = chain.request().newBuilder()
            .header("Accept", "application/json")
            .build()
        chain.proceed(peticion)
    }

    private val cliente = OkHttpClient.Builder()
        // Va ANTES del log: así el encabezado aparece en Logcat
        .addInterceptor(encabezadoAccept)
        // Solo para desarrollo: muestra el tráfico completo en Logcat
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    // Una sola instancia de Retrofit para todas las APIs
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val api: PostApi by lazy { retrofit.create(PostApi::class.java) }

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
}