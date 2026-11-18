package com.millones.campusgps.data

import com.millones.campusgps.model.Evento
import kotlinx.coroutines.flow.Flow

// Eventos del campus compartidos entre todos los usuarios
interface EventosRepository {
    // Todos los eventos ordenados por fecha, en tiempo real
    val eventos: Flow<List<Evento>>

    suspend fun crear(
        titulo: String,
        descripcion: String,
        fecha: Long,
        lugarNombre: String,
        latitud: Double?,
        longitud: Double?,
        fotoUri: String?
    ): Result<Unit>

    // asisto = estado actual: si ya asiste, deja de asistir y al revés
    suspend fun alternarAsistencia(id: String, asisto: Boolean): Result<Unit>

    suspend fun eliminar(id: String): Result<Unit>
}
