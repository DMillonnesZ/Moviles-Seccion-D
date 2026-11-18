package com.millones.campusgps.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Pedidos para abrir un lugar o un evento desde otra pestaña (Chat o Perfil):
// la pestaña que lo pide lo deja aquí y cambia de pestaña; el Mapa o Eventos lo abre
// y lo marca como atendido. Es solo estado de navegación de la UI.
object SolicitudesNavegacion {
    private val _lugar = MutableStateFlow<String?>(null)
    val lugar: StateFlow<String?> = _lugar.asStateFlow()

    private val _evento = MutableStateFlow<String?>(null)
    val evento: StateFlow<String?> = _evento.asStateFlow()

    fun abrirLugar(id: String) {
        _lugar.value = id
    }

    fun abrirEvento(id: String) {
        _evento.value = id
    }

    fun lugarAtendido() {
        _lugar.value = null
    }

    fun eventoAtendido() {
        _evento.value = null
    }
}
