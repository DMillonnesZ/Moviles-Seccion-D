package com.millones.murocampus.data.remote

import com.millones.murocampus.model.Comentario
import com.millones.murocampus.model.NuevoPost
import com.millones.murocampus.model.Post
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {

    // GET posts → lista de 100 publicaciones
    @GET("posts")
    suspend fun listar(): List<Post>

    // GET posts/{id} → una publicación (404 si no existe)
    @GET("posts/{id}")
    suspend fun detalle(@Path("id") id: Int): Post

    // GET posts/{id}/comments → 5 comentarios por publicación
    @GET("posts/{id}/comments")
    suspend fun comentarios(@Path("id") id: Int): List<Comentario>

    // GET posts?userId=2 → publicaciones de un usuario
    @GET("posts")
    suspend fun porUsuario(@Query("userId") userId: Int): List<Post>

    // POST posts con JSON en el cuerpo → devuelve la publicación creada (id = 101)
    @POST("posts")
    suspend fun crear(@Body nuevo: NuevoPost): Post
}