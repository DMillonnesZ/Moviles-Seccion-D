package com.millones.campusgps.data

import com.millones.campusgps.model.Posicion
import com.millones.campusgps.model.ResultadoBusqueda
import kotlinx.coroutines.flow.Flow

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>
    fun playServicesDisponible(): Boolean

    // Posición en tiempo real mientras alguien la escuche (requiere el permiso concedido)
    fun seguirPosicion(): Flow<Posicion>

    // Calle de esa posición (por ejemplo "Av. Javier Prado 1234"); null si no se encuentra
    suspend fun obtenerDireccion(posicion: Posicion): String?

    // Busca direcciones o sitios por nombre, priorizando los que están cerca de "cerca"
    suspend fun buscarDirecciones(texto: String, cerca: Posicion?): List<ResultadoBusqueda>
}
