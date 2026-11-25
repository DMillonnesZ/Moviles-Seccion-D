package com.millones.murocampus.data.remote

import com.millones.murocampus.model.Usuario
import retrofit2.http.GET

// [E] Ejemplo resuelto
interface UsuarioApi {
    @GET("users")
    suspend fun listar(): List<Usuario>
}