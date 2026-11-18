package com.millones.campusgps.data

import kotlinx.coroutines.flow.StateFlow

// Ajustes del usuario guardados en el celular
interface AjustesRepository {
    // Radio (metros) de la pestaña "Cerca" del mapa
    val radioCercanos: StateFlow<Int>
    fun cambiarRadio(metros: Int)

    // Hora de la última visita al Chat (para contar las opiniones no leídas)
    val ultimaVisitaOpiniones: StateFlow<Long>
    fun marcarOpinionesVistas(hasta: Long)
}
