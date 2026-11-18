package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.AjustesRepository
import com.millones.campusgps.data.ComentariosRepository
import com.millones.campusgps.data.EventosRepository
import com.millones.campusgps.data.LugaresRepository
import com.millones.campusgps.data.PerfilRepository
import com.millones.campusgps.model.AvisoMapa
import com.millones.campusgps.model.Comentario
import com.millones.campusgps.model.Destacado
import com.millones.campusgps.model.Evento
import com.millones.campusgps.model.FiltroOpiniones
import com.millones.campusgps.model.Lugar
import com.millones.campusgps.model.ObjetivoOpinion
import com.millones.campusgps.model.Perfil
import com.millones.campusgps.model.ResumenCalificacion
import com.millones.campusgps.model.TipoObjetivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_TEXTO = 300

// Chat de opiniones: comentarios con calificación sobre lugares y eventos.
// Vive a nivel de la navegación para mostrar la insignia de no leídos en la barra.
class OpinionesViewModel(
    private val comentariosRepo: ComentariosRepository,
    perfilRepo: PerfilRepository,
    lugaresRepo: LugaresRepository,
    eventosRepo: EventosRepository,
    private val ajustes: AjustesRepository
) : ViewModel() {

    val miUid: String? = perfilRepo.miUid()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _aviso = MutableStateFlow<AvisoMapa?>(null)
    val aviso: StateFlow<AvisoMapa?> = _aviso.asStateFlow()

    private val todos: StateFlow<List<Comentario>> = comentariosRepo.comentarios
        .onEach { _cargando.value = false }
        .catch {
            _cargando.value = false
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Fotos y nombres de los autores
    val perfiles: StateFlow<Map<String, Perfil>> = perfilRepo.perfiles
        .catch { emit(emptyMap()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    private val lugares: StateFlow<List<Lugar>> = lugaresRepo.lugares
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val eventos: StateFlow<List<Evento>> = eventosRepo.eventos
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _filtro = MutableStateFlow(FiltroOpiniones.TODAS)
    val filtro: StateFlow<FiltroOpiniones> = _filtro.asStateFlow()

    // Promedio y cantidad de opiniones de cada lugar o evento
    val resumenes: StateFlow<Map<String, ResumenCalificacion>> = todos
        .map { lista ->
            lista.groupBy { it.objetivoId }.mapValues { (_, opiniones) ->
                ResumenCalificacion(
                    promedio = opiniones.map { it.calificacion }.average().toFloat(),
                    total = opiniones.size
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    // Los 6 mejor calificados (carrusel de arriba)
    val destacados: StateFlow<List<Destacado>> = combine(todos, resumenes) { lista, resumen ->
        lista.distinctBy { it.objetivoId }
            .mapNotNull { c ->
                resumen[c.objetivoId]?.let { Destacado(c.objetivoId, c.objetivoNombre, c.tipo, it) }
            }
            .sortedWith(compareByDescending<Destacado> { it.resumen.promedio }.thenByDescending { it.resumen.total })
            .take(6)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Opiniones según el filtro elegido
    val comentarios: StateFlow<List<Comentario>> = combine(todos, _filtro, lugares, eventos) { lista, filtro, l, e ->
        when (filtro) {
            FiltroOpiniones.TODAS -> lista
            FiltroOpiniones.LUGARES -> lista.filter { it.tipo == TipoObjetivo.LUGAR }
            FiltroOpiniones.EVENTOS -> lista.filter { it.tipo == TipoObjetivo.EVENTO }
            FiltroOpiniones.MIAS -> lista.filter { it.esMio }
            FiltroOpiniones.FAVORITOS -> {
                // Lugares favoritos y eventos a los que asistirás
                val ids = l.filter { it.favorito }.map { it.id }.toSet() +
                        e.filter { it.asisto }.map { it.id }.toSet()
                lista.filter { it.objetivoId in ids }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Sobre qué se puede opinar: primero favoritos y eventos a los que asistirás
    val objetivos: StateFlow<List<ObjetivoOpinion>> = combine(lugares, eventos) { l, e ->
        val deLugares = l.map {
            ObjetivoOpinion(it.id, it.nombre, TipoObjetivo.LUGAR, it.categoria, destacado = it.favorito)
        }.sortedByDescending { it.destacado }
        val deEventos = e.map {
            ObjetivoOpinion(it.id, it.titulo, TipoObjetivo.EVENTO, it.lugarNombre, destacado = it.asisto)
        }.sortedByDescending { it.destacado }
        deLugares + deEventos
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Opiniones de otros publicadas después de la última visita (insignia de la barra)
    val noLeidos: StateFlow<Int> = combine(todos, ajustes.ultimaVisitaOpiniones) { lista, visto ->
        lista.count { !it.esMio && it.creadoEn > visto }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Formulario de nueva opinión (objetivoInicial = si se abrió desde un lugar o evento)
    private val _formularioAbierto = MutableStateFlow(false)
    val formularioAbierto: StateFlow<Boolean> = _formularioAbierto.asStateFlow()

    private val _publicando = MutableStateFlow(false)
    val publicando: StateFlow<Boolean> = _publicando.asStateFlow()

    fun elegirFiltro(filtro: FiltroOpiniones) {
        _filtro.value = filtro
    }

    fun marcarVistas() {
        val ultima = todos.value.maxOfOrNull { it.creadoEn } ?: return
        ajustes.marcarOpinionesVistas(ultima)
    }

    fun abrirFormulario() {
        _formularioAbierto.value = true
    }

    fun cerrarFormulario() {
        if (!_publicando.value) _formularioAbierto.value = false
    }

    fun publicar(objetivo: ObjetivoOpinion?, calificacion: Int, texto: String) {
        val limpio = texto.trim()
        val error = when {
            objetivo == null -> "Elige el lugar o evento"
            calificacion !in 1..5 -> "Elige de 1 a 5 estrellas"
            limpio.isBlank() -> "Escribe tu opinión"
            limpio.length > MAX_TEXTO -> "Tu opinión puede tener hasta $MAX_TEXTO letras"
            else -> null
        }
        if (error != null || objetivo == null) {
            _aviso.value = AvisoMapa(error ?: "Revisa los datos", false)
            return
        }
        _publicando.value = true
        viewModelScope.launch {
            comentariosRepo.publicar(objetivo.tipo, objetivo.id, objetivo.nombre, calificacion, limpio)
                .onSuccess {
                    _formularioAbierto.value = false
                    _aviso.value = AvisoMapa("Opinión publicada", true)
                }
                .onFailure { _aviso.value = AvisoMapa(it.message ?: "No se pudo publicar", false) }
            _publicando.value = false
        }
    }

    fun eliminar(comentario: Comentario) {
        if (!comentario.esMio) return
        viewModelScope.launch {
            comentariosRepo.eliminar(comentario.id)
                .onSuccess { _aviso.value = AvisoMapa("Opinión eliminada", true) }
                .onFailure { _aviso.value = AvisoMapa(it.message ?: "No se pudo eliminar", false) }
        }
    }

    fun limpiarAviso() {
        _aviso.value = null
    }
}
