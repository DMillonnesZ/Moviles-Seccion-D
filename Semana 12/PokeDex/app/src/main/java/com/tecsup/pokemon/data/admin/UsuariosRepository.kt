package com.tecsup.pokemon.data.admin

import com.tecsup.pokemon.domain.model.AccionRegistro
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.UsuarioAdmin
import kotlinx.coroutines.flow.Flow

// Gestión de cuentas desde el Panel Admin (solo funciona si quien la usa es admin)
interface UsuariosRepository {
    // uid del admin que tiene la sesión abierta
    val uidActual: String?

    // Todos los usuarios, en vivo (admins primero y luego por nombre)
    fun usuarios(): Flow<List<UsuarioAdmin>>

    // Últimas acciones de los admins, en vivo (más recientes primero)
    fun registro(limite: Long = 50): Flow<List<AccionRegistro>>

    // activo = false suspende (con motivo opcional); true la vuelve a activar
    suspend fun cambiarEstado(usuario: UsuarioAdmin, activo: Boolean, motivo: String? = null): Result<Unit>

    suspend fun cambiarRol(usuario: UsuarioAdmin, rol: Rol): Result<Unit>

    // Envía al usuario el correo de Firebase para crear una nueva contraseña
    suspend fun restablecerClave(usuario: UsuarioAdmin): Result<Unit>
}