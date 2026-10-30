package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.prefs.PreferenciasStore
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Horarios
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class PedirCitaViewModel(
    private val repo: VetRepository,
    prefs: PreferenciasStore
) : ViewModel() {

    private val duenoId = MutableStateFlow<Int?>(null)
    private val mascotaIdSel = MutableStateFlow<Int?>(null)
    private val _servicio = MutableStateFlow<Servicio?>(null)
    private val _fecha = MutableStateFlow(LocalDate.now())          // por defecto: hoy
    private val _hora = MutableStateFlow<String?>(null)
    private val _mensaje = MutableStateFlow<String?>(null)

    /** De hoy a 7 días */
    val fechas: List<LocalDate> = (0..7).map { LocalDate.now().plusDays(it.toLong()) }

    /** Los 18 horarios: 09:00 … 17:30 */
    val horarios: List<String> = Horarios.delDia

    fun cargar(duenoId: Int, mascotaId: Int?) {
        this.duenoId.value = duenoId
        if (mascotaId != null && mascotaIdSel.value == null) mascotaIdSel.value = mascotaId
    }

    // ===== Datos =====
    val mascotas: StateFlow<List<Mascota>> = duenoId
        .filterNotNull()
        .flatMapLatest { repo.observarMascotasDeDueno(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Solo servicios ACTIVOS */
    val servicios: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aceptarCitas: StateFlow<Boolean> = prefs.aceptarCitas
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    // ===== Selección =====
    /** La elegida, o la primera si aún no elige */
    val mascota: StateFlow<Mascota?> = combine(mascotas, mascotaIdSel) { lista, id ->
        lista.find { it.id == id } ?: lista.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val servicio: StateFlow<Servicio?> = _servicio.asStateFlow()
    val fecha: StateFlow<LocalDate> = _fecha.asStateFlow()
    val hora: StateFlow<String?> = _hora.asStateFlow()
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /** Cambia con la fecha (flatMapLatest) */
    val horasOcupadas: StateFlow<List<String>> = _fecha
        .flatMapLatest { repo.observarHorasOcupadas(it.toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Ocupadas + (si es hoy) las que ya pasaron */
    val horasDeshabilitadas: StateFlow<Set<String>> = combine(_fecha, horasOcupadas) { f, ocupadas ->
        val pasadas = if (f == LocalDate.now()) {
            val ahora = LocalTime.now()
            horarios.filter { !LocalTime.parse(it).isAfter(ahora) }
        } else emptyList()
        (ocupadas + pasadas).toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    /** Confirmar solo cuando todo está elegido */
    val puedeConfirmar: StateFlow<Boolean> =
        combine(mascota, _servicio, _hora, aceptarCitas) { m, s, h, acepta ->
            m != null && s != null && h != null && acepta
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // ===== Eventos =====
    fun elegirMascota(id: Int) { mascotaIdSel.value = id }
    fun elegirServicio(s: Servicio) { _servicio.value = s }

    /** Si cambia la fecha, se limpia la hora */
    fun elegirFecha(f: LocalDate) {
        _fecha.value = f
        _hora.value = null
        _mensaje.value = null
    }

    fun elegirHora(h: String) {
        if (h !in horasDeshabilitadas.value) {
            _hora.value = h
            _mensaje.value = null
        }
    }

    fun confirmar(onExito: () -> Unit) {
        val m = mascota.value
        val s = _servicio.value
        val f = _fecha.value
        val h = _hora.value
        val hoy = LocalDate.now()

        when {
            !aceptarCitas.value -> { _mensaje.value = "La clínica no está recibiendo citas"; return }
            m == null || s == null || h == null -> { _mensaje.value = "Completa todos los datos"; return }
            !s.activo -> { _mensaje.value = "Ese servicio no está disponible"; return }
            f.isBefore(hoy) || f.isAfter(hoy.plusDays(7)) -> { _mensaje.value = "Elige una fecha de hoy a 7 días"; return }
            h !in horarios -> { _mensaje.value = "Horario no válido"; return }
            LocalDateTime.of(f, LocalTime.parse(h)).isBefore(LocalDateTime.now()) -> {
                _mensaje.value = "Ese horario ya pasó"; _hora.value = null; return
            }
        }

        viewModelScope.launch {
            // Vuelve a consultar: ¿el horario sigue libre?
            val ocupadas = repo.observarHorasOcupadas(f.toString()).first()
            if (h!! in ocupadas) {
                _mensaje.value = "Ese horario se acaba de ocupar. Elige otro."
                _hora.value = null
                return@launch
            }
            repo.crearCita(m!!.id, s!!.id, f.toString(), h, nota = "")
            onExito()
        }
    }
}