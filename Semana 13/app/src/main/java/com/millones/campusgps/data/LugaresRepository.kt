package com.millones.campusgps.data

import com.millones.campusgps.model.Lugar
import kotlinx.coroutines.flow.Flow

// Lugares que registra el usuario (título, descripción, categoría, foto y favorito)
interface LugaresRepository {
    val lugares: Flow<List<Lugar>>

    // fotoUri = content:// del selector de fotos (texto para no atar el ViewModel a Android)
    suspend fun guardar(
        nombre: String,
        descripcion: String,
        categoria: String,
        latitud: Double,
        longitud: Double,
        fotoUri: String?
    ): Result<Unit>

    suspend fun alternarFavorito(id: Int)
    suspend fun cambiarFoto(id: Int, fotoUri: String): Result<Unit>
    suspend fun eliminar(id: Int)
}
