package com.millones.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.murocampus.data.PostRepository
import com.millones.murocampus.model.Comentario
import com.millones.murocampus.model.NuevoPost
import com.millones.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

/** Mensajes de un solo uso: éxito (check con confeti) o error (snackbar). */
sealed interface EventoUi {
    // El id evita que dos mensajes iguales seguidos se confundan
    data class Exito(val mensaje: String, val id: Long = System.nanoTime()) : EventoUi
    data class Error(val mensaje: String, val id: Long = System.nanoTime()) : EventoUi
}

class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    private companion object {
        const val TAMANO_PAGINA = 20
    }

    // ----- Lista: lo que dice el servidor + los cambios locales
    // Guarda SOLO lo que vino del servidor, acumulado página tras página
    private val _remota = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)

    // JSONPlaceholder no guarda nada: lo creado y lo borrado se recuerda aquí
    private val _creados = MutableStateFlow<List<Post>>(emptyList())
    private val _borrados = MutableStateFlow<Set<Int>>(emptySet())
    private var siguienteIdLocal = 101

    // null significa "Todos"
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    // Texto de la barra de búsqueda (filtra por título, sin llamar a la API)
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Paginación
    private val _hayMas = MutableStateFlow(false)
    val hayMas: StateFlow<Boolean> = _hayMas.asStateFlow()

    private val _cargandoMas = MutableStateFlow(false)
    val cargandoMas: StateFlow<Boolean> = _cargandoMas.asStateFlow()

    private var jobLista: Job? = null
    private var jobMas: Job? = null

    val lista: StateFlow<UiState<List<Post>>> =
        combine(_remota, _creados, _borrados, _usuarioFiltro, _busqueda) { remota, creados, borrados, filtro, busqueda ->
            when (remota) {
                UiState.Cargando -> UiState.Cargando
                is UiState.Error -> remota
                is UiState.Exito -> {
                    val texto = busqueda.trim()
                    val propios = creados.filter { filtro == null || it.userId == filtro }
                    val todos = (propios + remota.datos).filter { it.id !in borrados }
                    UiState.Exito(
                        if (texto.isEmpty()) todos
                        else todos.filter { it.title.contains(texto, ignoreCase = true) }
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    /** Primera página (también la usan Reintentar y el cambio de filtro). */
    fun cargar() {
        // Si había un "Cargar más" en curso, se cancela: la lista vuelve a empezar
        jobMas?.cancel()
        _cargandoMas.value = false
        _hayMas.value = false

        jobLista?.cancel() // si eliges otro filtro rápido, cancela la carga anterior
        jobLista = viewModelScope.launch {
            _remota.value = UiState.Cargando
            repo.pagina(_usuarioFiltro.value, 0, TAMANO_PAGINA)
                .onSuccess {
                    _remota.value = UiState.Exito(it)
                    // Si llegó una página completa, probablemente hay más
                    _hayMas.value = it.size == TAMANO_PAGINA
                }
                .onFailure { _remota.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Siguiente página: se agrega a lo que ya está descargado. */
    fun cargarMas() {
        val actual = _remota.value as? UiState.Exito ?: return
        if (_cargandoMas.value || !_hayMas.value) return

        jobMas = viewModelScope.launch {
            _cargandoMas.value = true
            try {
                repo.pagina(_usuarioFiltro.value, actual.datos.size, TAMANO_PAGINA)
                    .onSuccess { nuevos ->
                        _remota.value = UiState.Exito(actual.datos + nuevos)
                        _hayMas.value = nuevos.size == TAMANO_PAGINA
                    }
                    // Si falla, la lista se conserva y se avisa con un snackbar
                    .onFailure { _evento.value = EventoUi.Error(mensajeDe(it)) }
            } finally {
                _cargandoMas.value = false
            }
        }
    }

    fun filtrar(userId: Int?) {
        // Si eliges el mismo usuario y la lista ya está cargada, no repite la descarga
        if (userId == _usuarioFiltro.value && _remota.value is UiState.Exito) return
        _usuarioFiltro.value = userId
        cargar()
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    // ----- Favoritos (en memoria)
    // Se guarda la publicación completa para poder mostrarla aunque no esté en la lista actual
    private val _favoritos = MutableStateFlow<Map<Int, Post>>(emptyMap())
    val favoritos: StateFlow<Map<Int, Post>> = _favoritos.asStateFlow()

    fun alternarFavorito(post: Post) {
        val actual = _favoritos.value
        _favoritos.value = if (post.id in actual) actual - post.id else actual + (post.id to post)
    }

    // ----- Detalle
    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    private var jobDetalle: Job? = null
    private var idAbierto: Int? = null

    fun abrir(id: Int, forzar: Boolean = false) {
        val yaEnCurso = _detalle.value is UiState.Exito || jobDetalle?.isActive == true
        if (!forzar && idAbierto == id && yaEnCurso) return

        idAbierto = id
        jobDetalle?.cancel() // si abres otro rápido, cancela el anterior

        // Una publicación creada aquí no existe en el servidor (daría 404): se muestra desde memoria
        val local = _creados.value.firstOrNull { it.id == id }
        if (local != null) {
            _detalle.value = UiState.Exito(DetalleUi(local, emptyList()))
            return
        }

        jobDetalle = viewModelScope.launch {
            _detalle.value = UiState.Cargando

            // Las dos llamadas salen a la vez
            val post = async { repo.detalle(id) }
            val comentarios = async { repo.comentarios(id) }
            val rPost = post.await()
            val rComentarios = comentarios.await()

            // Si cualquiera falla, todo el detalle muestra el error
            val error = rPost.exceptionOrNull() ?: rComentarios.exceptionOrNull()
            _detalle.value = if (error != null) {
                UiState.Error(mensajeDe(error))
            } else {
                UiState.Exito(DetalleUi(rPost.getOrThrow(), rComentarios.getOrThrow()))
            }
        }
    }

    // ----- Crear
    // null = aún no se envió nada
    private val _crear = MutableStateFlow<UiState<Post>?>(null)
    val crear: StateFlow<UiState<Post>?> = _crear.asStateFlow()

    fun publicar(titulo: String, texto: String) {
        if (_crear.value is UiState.Cargando) return // evita doble envío
        viewModelScope.launch {
            _crear.value = UiState.Cargando
            repo.crear(NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim()))
                .onSuccess { respuesta ->
                    // El servidor siempre responde id 101: la guardamos con un id propio
                    val local = respuesta.copy(id = siguienteIdLocal++)
                    _creados.value = listOf(local) + _creados.value
                    _crear.value = UiState.Exito(respuesta)
                    _evento.value = EventoUi.Exito("Publicado con id ${respuesta.id}")
                }
                .onFailure { _crear.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun limpiarCrear() {
        _crear.value = null
    }

    // ----- Borrar
    fun borrar(id: Int) {
        viewModelScope.launch {
            repo.borrar(id)
                .onSuccess {
                    _creados.value = _creados.value.filterNot { it.id == id }
                    _borrados.value = _borrados.value + id
                    _favoritos.value = _favoritos.value - id // también sale de favoritos
                    _evento.value = EventoUi.Exito("Publicación eliminada")
                }
                .onFailure { _evento.value = EventoUi.Error(mensajeDe(it)) }
        }
    }

    // ----- Eventos (éxito animado / error)
    private val _evento = MutableStateFlow<EventoUi?>(null)
    val evento: StateFlow<EventoUi?> = _evento.asStateFlow()

    fun consumirEvento() {
        _evento.value = null
    }

    init { cargar() }
}