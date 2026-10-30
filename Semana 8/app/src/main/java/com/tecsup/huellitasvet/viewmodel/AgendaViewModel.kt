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
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModel(private val repo: VetRepository) : ViewModel() {

    /** Hoy y los próximos días */
    val fechas: List<LocalDate> = (0..6).map { LocalDate.now().plusDays(it.toLong()) }

    private val _fecha = MutableStateFlow(LocalDate.now())          // por defecto: hoy
    val fechaSeleccionada: StateFlow<LocalDate> = _fecha.asStateFlow()

    private val _mes = MutableStateFlow(YearMonth.now())
    val mes: StateFlow<YearMonth> = _mes.asStateFlow()

    private val _mostrarCalendario = MutableStateFlow(false)
    val mostrarCalendario: StateFlow<Boolean> = _mostrarCalendario.asStateFlow()

    /** Fechas con citas en el mes seleccionado */
    val diasConCitas: StateFlow<Set<String>> = _mes
        .flatMapLatest { m ->
            val inicio = m.atDay(1).toString()
            val fin = m.atEndOfMonth().toString()
            repo.observarFechasConCitas(inicio, fin).map { it.toSet() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    /** Citas del día elegido, ordenadas por hora (el DAO ya ordena) */
    val citas: StateFlow<List<CitaConDetalle>> = _fecha
        .flatMapLatest { repo.observarCitasPorFecha(it.toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFecha(f: LocalDate) { _fecha.value = f }

    fun alternarCalendario() {
        _mostrarCalendario.value = !_mostrarCalendario.value
    }

    fun mesAnterior() {
        _mes.value = _mes.value.minusMonths(1)
    }

    fun mesSiguiente() {
        _mes.value = _mes.value.plusMonths(1)
    }

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
