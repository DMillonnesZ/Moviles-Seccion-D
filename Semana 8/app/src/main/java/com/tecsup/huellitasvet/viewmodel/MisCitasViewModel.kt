package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MisCitasViewModel(private val repo: VetRepository) : ViewModel() {

    private val duenoId = MutableStateFlow<Int?>(null)
    fun cargar(id: Int) { duenoId.value = id }

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /** Todas las citas del dueño (se actualiza sola si el veterinario cambia algo) */
    private val citas: Flow<List<CitaConDetalle>> = duenoId
        .filterNotNull()
        .flatMapLatest { repo.observarCitasDeDueno(it) }

    private fun esActiva(c: CitaConDetalle) =
        c.cita.estado == EstadoCita.PENDIENTE.name || c.cita.estado == EstadoCita.CONFIRMADA.name

    /** Próximas = PENDIENTE y CONFIRMADA desde ahora (la más cercana primero) */
    val proximas: StateFlow<List<CitaConDetalle>> = citas
        .map { lista ->
            val ahora = LocalDateTime.now()
            lista.filter { esActiva(it) && !it.fechaHora().isBefore(ahora) }
                .sortedBy { it.fechaHora() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Historial = ATENDIDA, CANCELADA y las pasadas (la más reciente primero) */
    val historial: StateFlow<List<CitaConDetalle>> = citas
        .map { lista ->
            val ahora = LocalDateTime.now()
            lista.filter { !esActiva(it) || it.fechaHora().isBefore(ahora) }
                .sortedByDescending { it.fechaHora() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Total de atenciones (para el Perfil) */
    val atenciones: StateFlow<Int> = citas
        .map { l -> l.count { it.cita.estado == EstadoCita.ATENDIDA.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Regla: solo PENDIENTE o CONFIRMADA y si faltan MÁS de 2 horas */
    fun puedeCancelar(c: CitaConDetalle): Boolean {
        if (!esActiva(c)) return false
        val faltan = Duration.between(LocalDateTime.now(), c.fechaHora())
        return faltan > Duration.ofHours(2)
    }

    fun cancelar(c: CitaConDetalle) {
        if (!puedeCancelar(c)) {
            _mensaje.value = "Ya no puedes cancelar esta cita"
            return
        }
        viewModelScope.launch {
            repo.cambiarEstadoCita(c.cita.id, EstadoCita.CANCELADA)
            _mensaje.value = "Cita cancelada"
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }
}