package com.millones.campusgps.data

import com.millones.campusgps.model.Perfil
import kotlinx.coroutines.flow.Flow

// Perfil público de los usuarios: nombre, carrera y foto (se ven en las opiniones)
interface PerfilRepository {
    // uid -> perfil de todos los usuarios que tienen perfil
    val perfiles: Flow<Map<String, Perfil>>

    fun miUid(): String?

    suspend fun guardarDatos(nombre: String, carrera: String): Result<Unit>

    // fotoUri = content:// del selector de fotos
    suspend fun cambiarFoto(fotoUri: String): Result<Unit>
    suspend fun quitarFoto(): Result<Unit>
}
