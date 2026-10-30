package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ErroresAtencion(val diagnostico: String? = null, val peso: String? = null) {
    val hayErrores get() = diagnostico != null || peso != null
}

@OptIn(ExperimentalCoroutinesApi::class)
class AtenderCitaViewModel(private val repo: VetRepository) : ViewModel() {

    private val citaId = MutableStateFlow<Int?>(null)
    fun cargar(id: Int) { citaId.value = id }

    val cita: StateFlow<CitaConDetalle?> = citaId
        .filterNotNull()
        .flatMapLatest { repo.observarCita(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _errores = MutableStateFlow(ErroresAtencion())
    val errores: StateFlow<ErroresAtencion> = _errores.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /** Solo se puede atender una cita PENDIENTE o CONFIRMADA */
    fun sePuedeAtender(c: CitaConDetalle?): Boolean =
        c != null && (c.cita.estado == EstadoCita.PENDIENTE.name || c.cita.estado == EstadoCita.CONFIRMADA.name)

    fun registrar(diagnostico: String, tratamiento: String, pesoTexto: String, onExito: () -> Unit) {
        val c = cita.value
        if (!sePuedeAtender(c)) {
            _mensaje.value = "Esta cita ya no se puede atender"
            return
        }
        val peso = pesoTexto.replace(",", ".").toDoubleOrNull()
        val e = ErroresAtencion(
            diagnostico = if (diagnostico.trim().length < 5) "Mínimo 5 caracteres" else null,
            peso = if (peso == null || peso <= 0) "Debe ser mayor que 0" else null
        )
        _errores.value = e
        if (e.hayErrores) return

        viewModelScope.launch {
            // Transacción: cita → ATENDIDA + peso de la mascota actualizado
            repo.registrarAtencion(c!!.cita.id, diagnostico.trim(), tratamiento.trim(), peso!!)
            onExito()
        }
    }

    fun cancelar(onExito: () -> Unit) {
        val c = cita.value
        if (!sePuedeAtender(c)) {
            _mensaje.value = "Esta cita ya no se puede cancelar"
            return
        }
        viewModelScope.launch {
            repo.cambiarEstadoCita(c!!.cita.id, EstadoCita.CANCELADA)
            onExito()
        }
    }

    fun limpiarError(campo: String) {
        _errores.update { if (campo == "diagnostico") it.copy(diagnostico = null) else it.copy(peso = null) }
    }
}