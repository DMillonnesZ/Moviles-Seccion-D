package com.millones.campusgps.data

import com.millones.campusgps.model.Lugar
import kotlinx.coroutines.flow.Flow

// Lugares compartidos entre todos los usuarios (título, descripción, categoría, foto, autor)
interface LugaresRepository {
    // Lugares de la caché local (siempre disponibles, también sin internet)
    val lugares: Flow<List<Lugar>>

    // Mientras se recolecte, escucha Firestore en tiempo real y actualiza la caché
    fun sincronizar(): Flow<Unit>

    // fotoUri = content:// del selector de fotos (texto para no atar el ViewModel a Android)
    suspend fun guardar(
        nombre: String,
        descripcion: String,
        categoria: String,
        latitud: Double,
        longitud: Double,
        fotoUri: String?
    ): Result<Unit>

    suspend fun alternarFavorito(id: String): Result<Unit>
    suspend fun cambiarFoto(id: String, fotoUri: String): Result<Unit>
    suspend fun eliminar(id: String): Result<Unit>
}
