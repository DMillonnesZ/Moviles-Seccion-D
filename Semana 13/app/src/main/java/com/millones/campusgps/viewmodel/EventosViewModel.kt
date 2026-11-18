package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.EventosRepository
import com.millones.campusgps.data.LugaresRepository
import com.millones.campusgps.model.AvisoMapa
import com.millones.campusgps.model.Evento
import com.millones.campusgps.model.FiltroEventos
import com.millones.campusgps.model.Lugar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

// Un evento que empezó hace menos de esto todavía se considera "en curso"
private const val MS_EN_CURSO = 2 * 60 * 60 * 1000L

class EventosViewModel(
    private val eventosRepo: EventosRepository,
    lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _aviso = MutableStateFlow<AvisoMapa?>(null)
    val aviso: StateFlow<AvisoMapa?> = _aviso.asStateFlow()

    private val _filtro = MutableStateFlow(FiltroEventos.PROXIMOS)
    val filtro: StateFlow<FiltroEventos> = _filtro.asStateFlow()

    private val todos: StateFlow<List<Evento>> = eventosRepo.eventos
        .onEach { _cargando.value = false }
        .catch {
            _cargando.value = false
            _aviso.value = AvisoMapa("No se pudieron cargar los eventos", false)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Eventos según el filtro elegido
    val eventos: StateFlow<List<Evento>> = combine(todos, _filtro) { lista, filtro ->
        filtrar(lista, filtro)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Lugares registrados: para elegir dónde será el evento
    val lugares: StateFlow<List<Lugar>> = lugaresRepo.lugares
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Formulario de nuevo evento
    private val _creando = MutableStateFlow(false)
    val creando: StateFlow<Boolean> = _creando.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    fun elegirFiltro(filtro: FiltroEventos) {
        _filtro.value = filtro
    }

    fun abrirFormulario() {
        _creando.value = true
    }

    fun cerrarFormulario() {
        if (!_guardando.value) _creando.value = false
    }

    fun crear(
        titulo: String,
        descripcion: String,
        fecha: Long?,
        lugar: Lugar?,
        fotoUri: String?
    ) {
        val limpio = titulo.trim()
        val error = when {
            limpio.isBlank() -> "Escribe el nombre del evento"
            limpio.length > 80 -> "El nombre puede tener hasta 80 letras"
            descripcion.length > 500 -> "La descripción puede tener hasta 500 letras"
            fecha == null -> "Elige la fecha y la hora"
            fecha < System.currentTimeMillis() -> "La fecha ya pasó"
            else -> null
        }
        if (error != null || fecha == null) {
            _aviso.value = AvisoMapa(error ?: "Revisa los datos", false)
            return
        }

        _guardando.value = true
        viewModelScope.launch {
            eventosRepo.crear(
                titulo = limpio,
                descripcion = descripcion.trim(),
                fecha = fecha,
                lugarNombre = lugar?.nombre.orEmpty(),
                latitud = lugar?.latitud,
                longitud = lugar?.longitud,
                fotoUri = fotoUri
            ).onSuccess {
                _creando.value = false
                _aviso.value = AvisoMapa("$limpio se publicó", true)
            }.onFailure {
                _aviso.value = AvisoMapa(it.message ?: "No se pudo crear el evento", false)
            }
            _guardando.value = false
        }
    }

    fun alternarAsistencia(evento: Evento) {
        viewModelScope.launch {
            eventosRepo.alternarAsistencia(evento.id, evento.asisto)
                .onFailure { _aviso.value = AvisoMapa(it.message ?: "No se pudo actualizar", false) }
        }
    }

    fun eliminar(evento: Evento) {
        viewModelScope.launch {
            eventosRepo.eliminar(evento.id)
                .onSuccess { _aviso.value = AvisoMapa("Evento eliminado", true) }
                .onFailure { _aviso.value = AvisoMapa(it.message ?: "No se pudo eliminar", false) }
        }
    }

    fun limpiarAviso() {
        _aviso.value = null
    }

    private fun filtrar(lista: List<Evento>, filtro: FiltroEventos): List<Evento> {
        val ahora = System.currentTimeMillis()
        val vigentes = lista.filter { it.fecha >= ahora - MS_EN_CURSO }
        return when (filtro) {
            FiltroEventos.PROXIMOS -> vigentes
            FiltroEventos.HOY -> vigentes.filter { mismoDia(it.fecha, ahora) }
            FiltroEventos.SEMANA -> vigentes.filter { it.fecha <= ahora + 7 * 24 * 60 * 60 * 1000L }
            FiltroEventos.ASISTIRE -> vigentes.filter { it.asisto }
        }
    }

    private fun mismoDia(a: Long, b: Long): Boolean {
        val ca = Calendar.getInstance().apply { timeInMillis = a }
        val cb = Calendar.getInstance().apply { timeInMillis = b }
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
                ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }
}
