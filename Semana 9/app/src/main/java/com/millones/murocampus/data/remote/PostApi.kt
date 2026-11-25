package com.millones.murocampus.data.remote

import com.millones.murocampus.model.Post
import retrofit2.http.GET

interface PostApi {
    @GET("posts")
    suspend fun listar(): List<Post>

    // TODO Paso 3: detalle (@Path), comentarios (@Path), porUsuario (@Query), crear (@Body)
}