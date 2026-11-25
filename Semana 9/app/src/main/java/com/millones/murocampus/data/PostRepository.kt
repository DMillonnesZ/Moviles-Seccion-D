package com.millones.murocampus.data

import com.millones.murocampus.data.remote.PostApi
import com.millones.murocampus.model.Comentario
import com.millones.murocampus.model.NuevoPost
import com.millones.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

// Contrato: no conoce Retrofit ni la UI
interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>
    suspend fun borrar(id: Int): Result<Unit>
}

// Implementación: la única que usa PostApi
class PostRepositoryRemote(private val api: PostApi) : PostRepository {

    // Evita repetir el try/catch en cada función
    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            throw e // nunca se traga la cancelación
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun listar(): Result<List<Post>> =
        llamar { api.listar() }

    override suspend fun detalle(id: Int): Result<Post> =
        llamar { api.detalle(id) }

    override suspend fun comentarios(id: Int): Result<List<Comentario>> =
        llamar { api.comentarios(id) }

    override suspend fun porUsuario(userId: Int): Result<List<Post>> =
        llamar { api.porUsuario(userId) }

    override suspend fun crear(nuevo: NuevoPost): Result<Post> =
        llamar { api.crear(nuevo) }

    override suspend fun borrar(id: Int): Result<Unit> =
        llamar {
            val respuesta = api.borrar(id)
            // Con Response<Unit> un 404 o 500 no lanza excepción: hay que revisarlo
            if (!respuesta.isSuccessful) throw HttpException(respuesta)
        }
}