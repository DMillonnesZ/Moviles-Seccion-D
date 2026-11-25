package com.millones.murocampus.data.remote

import com.millones.murocampus.model.Comentario
import com.millones.murocampus.model.NuevoPost
import com.millones.murocampus.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {

    @GET("posts")
    suspend fun listar(): List<Post>

    @GET("posts/{id}")
    suspend fun detalle(@Path("id") id: Int): Post

    @GET("posts/{id}/comments")
    suspend fun comentarios(@Path("id") id: Int): List<Comentario>

    @GET("posts")
    suspend fun porUsuario(@Query("userId") userId: Int): List<Post>

    // Paginación: un userId nulo no se envía, así que sirve para "Todos" y para un usuario
    @GET("posts")
    suspend fun pagina(
        @Query("userId") userId: Int?,
        @Query("_start") inicio: Int,
        @Query("_limit") limite: Int
    ): List<Post>

    @POST("posts")
    suspend fun crear(@Body nuevo: NuevoPost): Post

    // DELETE posts/{id} → respuesta vacía con código 200.
    // Response<Unit> permite revisar el código en vez de lanzar excepción.
    @DELETE("posts/{id}")
    suspend fun borrar(@Path("id") id: Int): Response<Unit>
}