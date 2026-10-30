package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModel(private val repo: VetRepository) : ViewModel() {

    /** Hoy y los próximos días */
    val fechas: List<LocalDate> = (0..6).map { LocalDate.now().plusDays(it.toLong()) }

    private val _fecha = MutableStateFlow(LocalDate.now())          // por defecto: hoy
    val fechaSeleccionada: StateFlow<LocalDate> = _fecha.asStateFlow()

    /** Citas del día elegido, ordenadas por hora (el DAO ya ordena) */
    val citas: StateFlow<List<CitaConDetalle>> = _fecha
        .flatMapLatest { repo.observarCitasPorFecha(it.toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFecha(f: LocalDate) { _fecha.value = f }

    /** PENDIENTE → CONFIRMADA */
    fun confirmar(c: CitaConDetalle) {
        if (c.cita.estado != EstadoCita.PENDIENTE.name) return
        viewModelScope.launch { repo.cambiarEstadoCita(c.cita.id, EstadoCita.CONFIRMADA) }
    }

    /** Desde PENDIENTE o CONFIRMADA → CANCELADA (ATENDIDA y CANCELADA son finales) */
    fun cancelar(c: CitaConDetalle) {
        val e = c.cita.estado
        if (e != EstadoCita.PENDIENTE.name && e != EstadoCita.CONFIRMADA.name) return
        viewModelScope.launch { repo.cambiarEstadoCita(c.cita.id, EstadoCita.CANCELADA) }
    }
}