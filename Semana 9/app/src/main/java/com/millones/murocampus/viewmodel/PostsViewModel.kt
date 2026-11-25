package com.millones.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.murocampus.data.PostRepository
import com.millones.murocampus.model.Comentario
import com.millones.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    // ----- Lista y filtro (Pasos 6 y 8)
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    // null significa "Todos"
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    private var jobLista: Job? = null

    init { cargar() }

    fun cargar() {
        jobLista?.cancel() // si eliges otro filtro rápido, cancela la carga anterior
        jobLista = viewModelScope.launch {
            _lista.value = UiState.Cargando
            val filtro = _usuarioFiltro.value
            val resultado = if (filtro == null) repo.listar() else repo.porUsuario(filtro)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun filtrar(userId: Int?) {
        // Si tocas el mismo chip y la lista ya está cargada, no repite la descarga
        if (userId == _usuarioFiltro.value && _lista.value is UiState.Exito) return
        _usuarioFiltro.value = userId
        cargar()
    }

    // ----- Detalle (Paso 7)
    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    private var jobDetalle: Job? = null
    private var idAbierto: Int? = null

    fun abrir(id: Int, forzar: Boolean = false) {
        // Si ya está cargado (o cargando) este mismo id, no se repite la descarga
        val yaEnCurso = _detalle.value is UiState.Exito || jobDetalle?.isActive == true
        if (!forzar && idAbierto == id && yaEnCurso) return

        idAbierto = id
        jobDetalle?.cancel() // si abres otro rápido, cancela el anterior
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
}