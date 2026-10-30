package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Horarios
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ReprogramarCitaViewModel(
    private val repo: VetRepository
) : ViewModel() {

    private val _citaId = MutableStateFlow<Int?>(null)
    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    private val _horaSeleccionada = MutableStateFlow<String?>(null)
    private val _mensaje = MutableStateFlow<String?>(null)

    /** De hoy a 7 días (8 días en total) */
    val fechas: List<LocalDate> = (0..7).map { LocalDate.now().plusDays(it.toLong()) }

    /** Los horarios del día */
    val horarios: List<String> = Horarios.delDia

    fun cargar(citaId: Int) {
        _citaId.value = citaId
    }

    val cita: StateFlow<CitaConDetalle?> = _citaId
        .filterNotNull()
        .flatMapLatest { repo.observarCita(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()
    val horaSeleccionada: StateFlow<String?> = _horaSeleccionada.asStateFlow()
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val horasOcupadas: StateFlow<List<String>> = _fechaSeleccionada
        .flatMapLatest { repo.observarHorasOcupadas(it.toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Ocupadas + pasadas (si es hoy).
     * Si la fecha elegida es la misma fecha de la cita actual, se quita la hora propia de la cita.
     */
    val horasDeshabilitadas: StateFlow<Set<String>> = combine(
        _fechaSeleccionada,
        horasOcupadas,
        cita
    ) { f, ocupadas, c ->
        val pasadas = if (f == LocalDate.now()) {
            val ahora = LocalTime.now()
            horarios.filter { !LocalTime.parse(it).isAfter(ahora) }
        } else emptyList()

        val setDeshabilitadas = (ocupadas + pasadas).toMutableSet()
        if (c != null && f.toString() == c.cita.fecha) {
            setDeshabilitadas.remove(c.cita.hora)
        }
        setDeshabilitadas.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val puedeConfirmar: StateFlow<Boolean> = combine(
        cita,
        _fechaSeleccionada,
        _horaSeleccionada,
        horasDeshabilitadas
    ) { c, f, h, deshab ->
        if (c == null || h == null) return@combine false
        if (c.cita.estado != "PENDIENTE" && c.cita.estado != "CONFIRMADA") return@combine false
        if (h in deshab) return@combine false
        if (f.toString() == c.cita.fecha && h == c.cita.hora) return@combine false
        true
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun elegirFecha(f: LocalDate) {
        _fechaSeleccionada.value = f
        _horaSeleccionada.value = null
        _mensaje.value = null
    }

    fun elegirHora(h: String) {
        if (h !in horasDeshabilitadas.value) {
            _horaSeleccionada.value = h
            _mensaje.value = null
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun confirmar(onExito: () -> Unit) {
        val c = cita.value ?: return
        val f = _fechaSeleccionada.value
        val h = _horaSeleccionada.value
        val hoy = LocalDate.now()

        // Regla: Solo citas PENDIENTE o CONFIRMADA
        if (c.cita.estado != "PENDIENTE" && c.cita.estado != "CONFIRMADA") {
            _mensaje.value = "Solo se pueden reprogramar citas pendientes o confirmadas"
            return
        }

        // Regla: Solo si faltan MÁS de 2 horas para la cita actual
        val minutosRestantes = Duration.between(LocalDateTime.now(), c.fechaHora()).toMinutes()
        if (minutosRestantes <= 120) {
            _mensaje.value = "Ya no puedes reprogramar esta cita"
            return
        }

        if (h == null) {
            _mensaje.value = "Selecciona un horario"
            return
        }

        if (f.isBefore(hoy) || f.isAfter(hoy.plusDays(7))) {
            _mensaje.value = "Elige una fecha de hoy a 7 días"
            return
        }

        // Regla: Si elige la misma fecha y hora que ya tenía
        if (f.toString() == c.cita.fecha && h == c.cita.hora) {
            _mensaje.value = "Elige un horario distinto"
            return
        }

        if (LocalDateTime.of(f, LocalTime.parse(h)).isBefore(LocalDateTime.now())) {
            _mensaje.value = "Ese horario ya pasó"
            _horaSeleccionada.value = null
            return
        }

        viewModelScope.launch {
            // Regla: Antes de guardar, vuelve a consultar si el horario sigue libre
            val ocupadas = repo.observarHorasOcupadas(f.toString()).first()
            val esMismaFechaOriginal = (f.toString() == c.cita.fecha)
            if (h in ocupadas && (!esMismaFechaOriginal || h != c.cita.hora)) {
                _mensaje.value = "Ese horario se acaba de ocupar. Elige otro."
                _horaSeleccionada.value = null
                return@launch
            }

            repo.reprogramarCita(c.cita.id, f.toString(), h)
            onExito()
        }
    }
}
