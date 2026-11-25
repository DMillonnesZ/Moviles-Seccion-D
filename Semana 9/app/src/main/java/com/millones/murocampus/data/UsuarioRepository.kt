package com.millones.murocampus.data

import com.millones.murocampus.data.remote.UsuarioApi
import com.millones.murocampus.model.Usuario
import kotlinx.coroutines.CancellationException

// [E] Ejemplo resuelto
interface UsuarioRepository {
    suspend fun listar(): Result<List<Usuario>>
}

class UsuarioRepositoryRemote(private val api: UsuarioApi) : UsuarioRepository {
    override suspend fun listar(): Result<List<Usuario>> =
        try {
            Result.success(api.listar())
        } catch (e: CancellationException) {
            throw e // nunca se traga la cancelación
        } catch (e: Exception) {
            Result.failure(e)
        }
}