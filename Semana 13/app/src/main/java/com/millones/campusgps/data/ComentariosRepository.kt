package com.millones.campusgps.data

import com.millones.campusgps.model.Comentario
import com.millones.campusgps.model.TipoObjetivo
import kotlinx.coroutines.flow.Flow

// Opiniones con calificación sobre lugares y eventos (pestaña Chat)
interface ComentariosRepository {
    // Las más recientes primero, en tiempo real
    val comentarios: Flow<List<Comentario>>

    suspend fun publicar(
        tipo: TipoObjetivo,
        objetivoId: String,
        objetivoNombre: String,
        calificacion: Int,
        texto: String
    ): Result<Unit>

    suspend fun eliminar(id: String): Result<Unit>
}
