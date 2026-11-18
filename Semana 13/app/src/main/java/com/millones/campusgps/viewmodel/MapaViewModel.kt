package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.LugaresRepository
import com.millones.campusgps.data.UbicacionRepository
import com.millones.campusgps.data.UbicacionesRepository
import com.millones.campusgps.model.AvisoMapa
import com.millones.campusgps.model.BorradorLugar
import com.millones.campusgps.model.LugarConDistancia
import com.millones.campusgps.model.PersonaEnVivo
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
import kotlinx.coroutines.flow.collect
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

// Cada cuánto se sube la ubicación en vivo cuando el usuario la comparte
private const val MS_ENTRE_PUBLICACIONES = 10_000L

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository,
    private val ubicacionesRepo: UbicacionesRepository
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

    private val _aviso = MutableStateFlow<AvisoMapa?>(null)
    val aviso: StateFlow<AvisoMapa?> = _aviso.asStateFlow()

    // Lugares de todos los usuarios con su distancia (ordenados por cercanía cuando hay ubicación)
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

    // Lugares dentro de RADIO_CERCANOS (vacía mientras no hay ubicación)
    val cercanos: StateFlow<List<LugarConDistancia>> = lugares
        .map { lista -> lista.filter { (it.metros ?: Float.MAX_VALUE) <= RADIO_CERCANOS } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Lugar más cercano dentro de RADIO_LUGAR_ACTUAL = ubicación actual del usuario (null = ninguno)
    val lugarActual: StateFlow<LugarConDistancia?> = lugares
        .map { lista -> lista.firstOrNull { (it.metros ?: Float.MAX_VALUE) <= RADIO_LUGAR_ACTUAL } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Otras personas que comparten su ubicación en vivo
    val personas: StateFlow<List<PersonaEnVivo>> = ubicacionesRepo.personas()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ¿El usuario comparte su ubicación? Empieza apagado cada vez (privacidad)
    private val _compartiendo = MutableStateFlow(false)
    val compartiendo: StateFlow<Boolean> = _compartiendo.asStateFlow()
    private var ultimaPublicacion = 0L

    private val _seleccionado = MutableStateFlow<String?>(null)
    val seleccionado: StateFlow<String?> = _seleccionado.asStateFlow()

    // Punto del mapa que se está registrando (formulario abierto)
    private val _borrador = MutableStateFlow<BorradorLugar?>(null)
    val borrador: StateFlow<BorradorLugar?> = _borrador.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    init {
        // Trae los lugares de todos en tiempo real mientras el mapa esté vivo
        viewModelScope.launch {
            lugaresRepo.sincronizar()
                .catch { _aviso.value = AvisoMapa("Sin conexión: mostrando los lugares guardados", false) }
                .collect()
        }
    }

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

        // Si la comparte, se sube cada MS_ENTRE_PUBLICACIONES
        val ahora = System.currentTimeMillis()
        if (_compartiendo.value && ahora - ultimaPublicacion >= MS_ENTRE_PUBLICACIONES) {
            ultimaPublicacion = ahora
            runCatching { ubicacionesRepo.publicar(nueva) }
        }

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

    fun seleccionar(id: String?) {
        _seleccionado.value = id
    }

    // ----- Compartir ubicación en vivo -----

    fun alternarCompartir() {
        if (_compartiendo.value) {
            _compartiendo.value = false
            ubicacionesRepo.dejarDeCompartir()
            _aviso.value = AvisoMapa("Dejaste de compartir tu ubicación", true)
            return
        }
        val aqui = _posicion.value
        if (aqui == null) {
            _aviso.value = AvisoMapa("Primero ubícate con el botón azul", false)
            return
        }
        _compartiendo.value = true
        ultimaPublicacion = System.currentTimeMillis()
        viewModelScope.launch {
            runCatching { ubicacionesRepo.publicar(aqui) }
                .onSuccess { _aviso.value = AvisoMapa("Los demás ya ven tu ubicación", true) }
                .onFailure {
                    _compartiendo.value = false
                    _aviso.value = AvisoMapa("No se pudo compartir tu ubicación", false)
                }
        }
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
        val limpio = titulo.trim()
        when {
            limpio.isBlank() -> {
                _aviso.value = AvisoMapa("Escribe un título para el lugar", false)
                return
            }
            limpio.length > 60 -> {
                _aviso.value = AvisoMapa("El título puede tener hasta 60 letras", false)
                return
            }
        }
        _guardando.value = true
        viewModelScope.launch {
            lugaresRepo.guardar(
                nombre = limpio,
                descripcion = descripcion.trim(),
                categoria = categoria,
                latitud = punto.latitud,
                longitud = punto.longitud,
                fotoUri = fotoUri
            ).onSuccess {
                _borrador.value = null
                _aviso.value = AvisoMapa("$limpio se guardó y ya lo ven los demás", true)
            }.onFailure {
                _aviso.value = AvisoMapa(it.message ?: "No se pudo guardar el lugar", false)
            }
            _guardando.value = false
        }
    }

    // "Tu ubicación": solo se puede marcar en favoritos si estás dentro del radio de un lugar
    fun marcarLugarActual() {
        val actual = lugarActual.value
        if (actual == null) {
            _aviso.value = AvisoMapa(
                "Acércate a menos de ${RADIO_LUGAR_ACTUAL.toInt()} m de un lugar registrado",
                false
            )
            return
        }
        viewModelScope.launch {
            lugaresRepo.alternarFavorito(actual.lugar.id)
                .onSuccess {
                    _aviso.value = AvisoMapa(
                        if (actual.lugar.favorito) "${actual.lugar.nombre} salió de favoritos"
                        else "${actual.lugar.nombre} se marcó como favorito",
                        true
                    )
                }
                .onFailure { mostrarError(it, "No se pudo actualizar el favorito") }
        }
    }

    // ----- Acciones sobre un lugar -----

    fun alternarFavorito(id: String) {
        viewModelScope.launch {
            lugaresRepo.alternarFavorito(id).onFailure { mostrarError(it, "No se pudo actualizar el favorito") }
        }
    }

    fun subirFoto(id: String, uri: String) {
        viewModelScope.launch {
            lugaresRepo.cambiarFoto(id, uri)
                .onSuccess { _aviso.value = AvisoMapa("Foto actualizada", true) }
                .onFailure { mostrarError(it, "No se pudo guardar la foto") }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            lugaresRepo.eliminar(id)
                .onSuccess {
                    if (_seleccionado.value == id) _seleccionado.value = null
                    _aviso.value = AvisoMapa("Lugar eliminado", true)
                }
                .onFailure { mostrarError(it, "No se pudo eliminar el lugar") }
        }
    }

    fun limpiarAviso() {
        _aviso.value = null
    }

    private fun mostrarError(error: Throwable, porDefecto: String) {
        _aviso.value = AvisoMapa(error.message ?: porDefecto, false)
    }

    // Al salir del mapa (cerrar la app o la sesión) deja de compartir la ubicación
    override fun onCleared() {
        if (_compartiendo.value) ubicacionesRepo.dejarDeCompartir()
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
