package com.millones.campusgps.data

import com.millones.campusgps.model.PersonaEnVivo
import com.millones.campusgps.model.Posicion
import kotlinx.coroutines.flow.Flow

// Ubicación en vivo compartida (opcional: cada usuario decide si la comparte)
interface UbicacionesRepository {
    // Otras personas que comparten su ubicación (sin incluir al usuario actual)
    fun personas(): Flow<List<PersonaEnVivo>>

    suspend fun publicar(posicion: Posicion)

    // Borra la ubicación del usuario: los demás dejan de verlo
    fun dejarDeCompartir()
}
