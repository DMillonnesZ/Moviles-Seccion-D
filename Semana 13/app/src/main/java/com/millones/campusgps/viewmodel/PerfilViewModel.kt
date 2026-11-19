package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.AjustesRepository
import com.millones.campusgps.data.AuthRepository
import com.millones.campusgps.data.ComentariosRepository
import com.millones.campusgps.data.EventosRepository
import com.millones.campusgps.data.LugaresRepository
import com.millones.campusgps.data.PerfilRepository
import com.millones.campusgps.model.AvisoMapa
import com.millones.campusgps.model.Comentario
import com.millones.campusgps.model.EstadisticasPerfil
import com.millones.campusgps.model.Evento
import com.millones.campusgps.model.Lugar
import com.millones.campusgps.model.Perfil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Un evento que empezó hace menos de esto todavía se considera "en curso"
private const val MS_EN_CURSO = 2 * 60 * 60 * 1000L

// Perfil: foto, nombre, carrera, estadísticas, mis lugares, mis eventos y ajustes
class PerfilViewModel(
    private val authRepo: AuthRepository,
    private val perfilRepo: PerfilRepository,
    lugaresRepo: LugaresRepository,
    eventosRepo: EventosRepository,
    comentariosRepo: ComentariosRepository,
    private val ajustes: AjustesRepository
) : ViewModel() {

    val correo: StateFlow<String?> = authRepo.usuarioActivo
    val nombreCuenta: StateFlow<String?> = authRepo.nombreActivo

    // Mi perfil público (null si todavía no lo guardé nunca)
    val perfil: StateFlow<Perfil?> = perfilRepo.perfiles
        .map { it[perfilRepo.miUid()] }
        .catch { emit(null) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val lugares: StateFlow<List<Lugar>> = lugaresRepo.lugares
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val eventos: StateFlow<List<Evento>> = eventosRepo.eventos
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val comentarios: StateFlow<List<Comentario>> = comentariosRepo.comentarios
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Lugares que registré
    val misLugares: StateFlow<List<Lugar>> = lugares
        .map { lista -> lista.filter { it.esMio } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Próximos eventos a los que asistiré o que organizo
    val misEventos: StateFlow<List<Evento>> = eventos
        .map { lista ->
            val desde = System.currentTimeMillis() - MS_EN_CURSO
            lista.filter { (it.asisto || it.esMio) && it.fecha >= desde }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val estadisticas: StateFlow<EstadisticasPerfil> = combine(lugares, eventos, comentarios) { l, e, c ->
        val desde = System.currentTimeMillis() - MS_EN_CURSO
        EstadisticasPerfil(
            lugares = l.count { it.esMio },
            favoritos = l.count { it.favorito },
            asistire = e.count { it.asisto && it.fecha >= desde },
            opiniones = c.count { it.esMio }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EstadisticasPerfil())

    val radioCercanos: StateFlow<Int> = ajustes.radioCercanos

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _subiendoFoto = MutableStateFlow(false)
    val subiendoFoto: StateFlow<Boolean> = _subiendoFoto.asStateFlow()

    private val _editando = MutableStateFlow(false)
    val editando: StateFlow<Boolean> = _editando.asStateFlow()

    private val _aviso = MutableStateFlow<AvisoMapa?>(null)
    val aviso: StateFlow<AvisoMapa?> = _aviso.asStateFlow()

    fun abrirEdicion() {
        _editando.value = true
    }

    fun cerrarEdicion() {
        if (!_guardando.value) _editando.value = false
    }

    // Nombre en la cuenta (Auth) y en el perfil público (Firestore); carrera solo en el perfil
    fun guardarDatos(nombre: String, carrera: String) {
        val limpio = nombre.trim()
        val carreraLimpia = carrera.trim()
        val error = when {
            limpio.length < 2 -> "Escribe tu nombre"
            limpio.length > 40 -> "El nombre puede tener hasta 40 letras"
            carreraLimpia.length > 60 -> "La carrera puede tener hasta 60 letras"
            else -> null
        }
        if (error != null) {
            _aviso.value = AvisoMapa(error, false)
            return
        }
        _guardando.value = true
        viewModelScope.launch {
            val resultado = authRepo.actualizarNombre(limpio)
                .mapCatching { perfilRepo.guardarDatos(limpio, carreraLimpia).getOrThrow() }
            resultado
                .onSuccess {
                    _editando.value = false
                    _aviso.value = AvisoMapa("Perfil actualizado", true)
                }
                .onFailure { _aviso.value = AvisoMapa(it.message ?: "No se pudo guardar", false) }
            _guardando.value = false
        }
    }

    fun cambiarFoto(uri: String) {
        _subiendoFoto.value = true
        viewModelScope.launch {
            perfilRepo.cambiarFoto(uri)
                .onSuccess { _aviso.value = AvisoMapa("Foto de perfil actualizada", true) }
                .onFailure { _aviso.value = AvisoMapa(it.message ?: "No se pudo cambiar la foto", false) }
            _subiendoFoto.value = false
        }
    }

    fun quitarFoto() {
        viewModelScope.launch {
            perfilRepo.quitarFoto()
                .onSuccess { _aviso.value = AvisoMapa("Foto quitada", true) }
                .onFailure { _aviso.value = AvisoMapa(it.message ?: "No se pudo quitar la foto", false) }
        }
    }

    fun cambiarRadio(metros: Int) {
        ajustes.cambiarRadio(metros)
        _aviso.value = AvisoMapa("Cerca ahora es a menos de $metros m", true)
    }

    fun limpiarAviso() {
        _aviso.value = null
    }
}
