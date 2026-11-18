package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.EventosRepository
import com.millones.campusgps.data.LugaresRepository
import com.millones.campusgps.data.UbicacionRepository
import com.millones.campusgps.model.Posicion
import com.millones.campusgps.model.ResultadoBusqueda
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    lugaresRepo: LugaresRepository,
    private val ubicacionRepo: UbicacionRepository
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

    // ----- Búsqueda de ubicación (selector del mapa) -----
    private val _resultados = MutableStateFlow<List<ResultadoBusqueda>>(emptyList())
    val resultados: StateFlow<List<ResultadoBusqueda>> = _resultados.asStateFlow()

    private val _buscando = MutableStateFlow(false)
    val buscando: StateFlow<Boolean> = _buscando.asStateFlow()

    // true cuando ya se buscó (para mostrar "Sin resultados")
    private val _busquedaHecha = MutableStateFlow(false)
    val busquedaHecha: StateFlow<Boolean> = _busquedaHecha.asStateFlow()

    private var busqueda: Job? = null

    // Busca mientras se escribe (espera medio segundo de pausa). Primero los lugares
    // registrados que coinciden y luego las direcciones cercanas al centro del mapa.
    fun buscarUbicacion(texto: String, latitudCentro: Double, longitudCentro: Double, inmediato: Boolean = false) {
        busqueda?.cancel()
        val consulta = texto.trim()
        if (consulta.length < 3) {
            _resultados.value = emptyList()
            _buscando.value = false
            _busquedaHecha.value = false
            return
        }
        busqueda = viewModelScope.launch {
            if (!inmediato) delay(500)
            _buscando.value = true
            val minusculas = consulta.lowercase()
            val propios = lugares.value
                .filter { it.nombre.lowercase().contains(minusculas) }
                .take(3)
                .map {
                    ResultadoBusqueda(
                        titulo = it.nombre,
                        detalle = "Lugar registrado · ${it.categoria}",
                        latitud = it.latitud,
                        longitud = it.longitud,
                        esLugarRegistrado = true
                    )
                }
            val direcciones = ubicacionRepo.buscarDirecciones(consulta, Posicion(latitudCentro, longitudCentro))
            _resultados.value = propios + direcciones
            _buscando.value = false
            _busquedaHecha.value = true
        }
    }

    fun limpiarBusqueda() {
        busqueda?.cancel()
        _resultados.value = emptyList()
        _buscando.value = false
        _busquedaHecha.value = false
    }

    fun elegirFiltro(filtro: FiltroEventos) {
        _filtro.value = filtro
    }

    fun abrirFormulario() {
        _creando.value = true
    }

    fun cerrarFormulario() {
        if (!_guardando.value) _creando.value = false
    }

    // El lugar es obligatorio (registrado o punto en el mapa): así el evento sale en el mapa
    fun crear(
        titulo: String,
        descripcion: String,
        fecha: Long?,
        lugarNombre: String,
        latitud: Double?,
        longitud: Double?,
        fotoUri: String?
    ) {
        val limpio = titulo.trim()
        val error = when {
            limpio.isBlank() -> "Escribe el nombre del evento"
            limpio.length > 80 -> "El nombre puede tener hasta 80 letras"
            descripcion.length > 500 -> "La descripción puede tener hasta 500 letras"
            fecha == null -> "Elige la fecha y la hora"
            fecha < System.currentTimeMillis() -> "La fecha ya pasó"
            latitud == null || longitud == null -> "Elige dónde será el evento"
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
                lugarNombre = lugarNombre,
                latitud = latitud,
                longitud = longitud,
                fotoUri = fotoUri
            ).onSuccess {
                _creando.value = false
                _aviso.value = AvisoMapa("Evento publicado: ya aparece en el mapa", true)
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
