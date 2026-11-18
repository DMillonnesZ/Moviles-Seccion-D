package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.LugaresRepository
import com.millones.campusgps.data.UbicacionRepository
import com.millones.campusgps.model.AvisoMapa
import com.millones.campusgps.model.BorradorLugar
import com.millones.campusgps.model.LugarConDistancia
import com.millones.campusgps.model.Posicion
import com.millones.campusgps.model.TipoError
import com.millones.campusgps.model.UbicacionUi
import com.millones.campusgps.util.distanciaMetros
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Radio (metros) alrededor del usuario: el lugar registrado más cercano dentro de él
// se toma como su ubicación actual
const val RADIO_LUGAR_ACTUAL = 60f

// Radio (metros) de "lugares cercanos": solo estos aparecen en la pestaña Cerca y se pintan de verde
const val RADIO_CERCANOS = 300f

// Solo se vuelve a buscar la calle si el usuario se movió más que esto
private const val METROS_NUEVA_CALLE = 25f

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    // Posición en tiempo real del usuario
    private val _posicion = MutableStateFlow<Posicion?>(null)
    val posicion: StateFlow<Posicion?> = _posicion.asStateFlow()

    // Calle donde está el usuario ("Tu ubicación")
    private val _direccion = MutableStateFlow<String?>(null)
    val direccion: StateFlow<String?> = _direccion.asStateFlow()
    private var posicionDeLaCalle: Posicion? = null

    private var seguimiento: Job? = null

    // Lugares guardados con su distancia (ordenados por cercanía cuando hay ubicación)
    val lugares: StateFlow<List<LugarConDistancia>> = combine(
        lugaresRepo.lugares, _posicion
    ) { lista, posicion ->
        val conDistancia = lista.map { lugar ->
            LugarConDistancia(lugar, posicion?.let { distanciaMetros(it, lugar) })
        }
        if (posicion != null) conDistancia.sortedBy { it.metros } else conDistancia
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Sección "Lugares Favoritos"
    val favoritos: StateFlow<List<LugarConDistancia>> = lugares
        .map { lista -> lista.filter { it.lugar.favorito } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Lugares registrados dentro de RADIO_CERCANOS (vacía mientras no hay ubicación)
    val cercanos: StateFlow<List<LugarConDistancia>> = lugares
        .map { lista -> lista.filter { (it.metros ?: Float.MAX_VALUE) <= RADIO_CERCANOS } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Lugar registrado más cercano dentro del radio = ubicación actual del usuario (null = ninguno)
    val lugarActual: StateFlow<LugarConDistancia?> = lugares
        .map { lista -> lista.firstOrNull { (it.metros ?: Float.MAX_VALUE) <= RADIO_LUGAR_ACTUAL } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    // Punto del mapa que se está registrando (formulario abierto)
    private val _borrador = MutableStateFlow<BorradorLugar?>(null)
    val borrador: StateFlow<BorradorLugar?> = _borrador.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _aviso = MutableStateFlow<AvisoMapa?>(null)
    val aviso: StateFlow<AvisoMapa?> = _aviso.asStateFlow()

    // ----- Ubicación -----

    // Ubica al usuario (el mapa viaja ahí) y empieza a seguirlo en tiempo real
    fun buscar() {
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }

        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess { posicion ->
                    _estado.value = UbicacionUi.Lista(posicion)
                    actualizarPosicion(posicion)
                    seguirEnTiempoReal()
                }
                .onFailure { _estado.value = clasificarError(it) }
        }
    }

    private fun seguirEnTiempoReal() {
        if (seguimiento?.isActive == true) return
        seguimiento = viewModelScope.launch {
            ubicacionRepo.seguirPosicion()
                .catch { /* sin permiso o GPS apagado: se queda la última posición */ }
                .collect { actualizarPosicion(it) }
        }
    }

    private suspend fun actualizarPosicion(nueva: Posicion) {
        _posicion.value = nueva
        val anterior = posicionDeLaCalle
        if (anterior == null || distanciaMetros(anterior, nueva) > METROS_NUEVA_CALLE) {
            posicionDeLaCalle = nueva
            _direccion.value = ubicacionRepo.obtenerDireccion(nueva)
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    // ----- Registrar lugares -----

    // Mantener presionado el mapa: abre el formulario en ese punto y busca su calle
    fun nuevoLugarEn(latitud: Double, longitud: Double) {
        val nuevo = BorradorLugar(latitud, longitud)
        _borrador.value = nuevo
        viewModelScope.launch {
            val calle = ubicacionRepo.obtenerDireccion(Posicion(latitud, longitud))
            // Solo si el formulario sigue abierto en el mismo punto
            if (_borrador.value == nuevo) _borrador.value = nuevo.copy(direccion = calle)
        }
    }

    fun cancelarBorrador() {
        if (!_guardando.value) _borrador.value = null
    }

    fun guardarLugar(titulo: String, descripcion: String, categoria: String, fotoUri: String?) {
        val punto = _borrador.value ?: return
        if (titulo.isBlank()) {
            _aviso.value = AvisoMapa("Escribe un título para el lugar", false)
            return
        }
        _guardando.value = true
        viewModelScope.launch {
            lugaresRepo.guardar(
                nombre = titulo.trim(),
                descripcion = descripcion.trim(),
                categoria = categoria,
                latitud = punto.latitud,
                longitud = punto.longitud,
                fotoUri = fotoUri
            ).onSuccess {
                _borrador.value = null
                _aviso.value = AvisoMapa("${titulo.trim()} se guardó", true)
            }.onFailure {
                _aviso.value = AvisoMapa(it.message ?: "No se pudo guardar el lugar", false)
            }
            _guardando.value = false
        }
    }

    // "Tu ubicación": solo se puede registrar (marcar en favoritos) si estás dentro del radio
    // de un lugar que registraste
    fun marcarLugarActual() {
        val actual = lugarActual.value
        if (actual == null) {
            _aviso.value = AvisoMapa(
                "Acércate a menos de ${RADIO_LUGAR_ACTUAL.toInt()} m de un lugar que registraste",
                false
            )
            return
        }
        viewModelScope.launch {
            lugaresRepo.alternarFavorito(actual.lugar.id)
            _aviso.value = AvisoMapa(
                if (actual.lugar.favorito) "${actual.lugar.nombre} salió de favoritos"
                else "${actual.lugar.nombre} se marcó como favorito",
                true
            )
        }
    }

    // ----- Acciones sobre un lugar -----

    fun alternarFavorito(id: Int) {
        viewModelScope.launch { lugaresRepo.alternarFavorito(id) }
    }

    fun subirFoto(id: Int, uri: String) {
        viewModelScope.launch {
            lugaresRepo.cambiarFoto(id, uri)
                .onSuccess { _aviso.value = AvisoMapa("Foto actualizada", true) }
                .onFailure { _aviso.value = AvisoMapa(it.message ?: "No se pudo guardar la foto", false) }
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            lugaresRepo.eliminar(id)
            if (_seleccionado.value == id) _seleccionado.value = null
            _aviso.value = AvisoMapa("Lugar eliminado", true)
        }
    }

    fun limpiarAviso() {
        _aviso.value = null
    }

    private fun clasificarError(e: Throwable): UbicacionUi.Error = when (e) {
        is SecurityException -> UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
        else -> when (e.message) {
            "GPS_APAGADO" -> UbicacionUi.Error(
                "No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo",
                TipoError.GPS_APAGADO
            )
            else -> UbicacionUi.Error(
                e.message ?: "No se pudo obtener la ubicación",
                TipoError.OTRO
            )
        }
    }
}
