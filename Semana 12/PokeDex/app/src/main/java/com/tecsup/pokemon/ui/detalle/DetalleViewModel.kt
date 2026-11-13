package com.tecsup.pokemon.ui.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.catalogo.CatalogoRepository
import com.tecsup.pokemon.data.repository.CartasRepository
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.EstadoCartas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetalleViewModel(
    private val repo: ItemRepository,
    private val cartasRepo: CartasRepository,
    private val catalogoRepo: CatalogoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Llegan desde la navegación: detalle/{id}?tipo={tipo}
    val id: Int = checkNotNull(savedStateHandle["id"])

    // Tipo de la lista desde la que se entró (null si se entró desde Favoritos)
    val tipoOrigen: String? = savedStateHandle["tipo"]

    private val _estado = MutableStateFlow<UiState<ItemDetalle>>(UiState.Cargando)
    val estado: StateFlow<UiState<ItemDetalle>> = _estado.asStateFlow()

    // Cartas: se cargan aparte, así el resto del Detalle no espera a TCGdex
    private val _cartas = MutableStateFlow<EstadoCartas>(EstadoCartas.Cargando)
    val cartas: StateFlow<EstadoCartas> = _cartas.asStateFlow()

    // Si este Pokémon está en favoritos
    val esFavorito: StateFlow<Boolean> = repo.favoritos
        .map { lista -> lista.any { it.id == id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // Tipos guardados en Room (para los íconos)
    val tiposInfo: StateFlow<Map<String, Tipo>> = repo.tipos
        .map { lista -> lista.associateBy { it.nombre } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando

            // Se puede llegar a un descontinuado desde una cadena de evolución
            val fuera = catalogoRepo.leerDeshabilitados()
            if (id in fuera.pokemon) {
                _estado.value = UiState.Error("Este Pokémon fue descontinuado por el administrador")
                return@launch
            }

            repo.obtenerDetalle(id)
                .onSuccess { detalle ->
                    // Si alguno de sus tipos fue descontinuado, el Pokémon tampoco se muestra
                    if (detalle.tipos.any { it in fuera.tipos }) {
                        _estado.value = UiState.Error("Este Pokémon pertenece a un tipo descontinuado por el administrador")
                        return@onSuccess
                    }
                    _estado.value = UiState.Exito(detalle)
                    cargarCartas(detalle.nombre)
                }
                .onFailure { _estado.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun cargarCartas(nombre: String) {
        viewModelScope.launch {
            _cartas.value = EstadoCartas.Cargando
            cartasRepo.cartasDe(nombre)
                .onSuccess { lista ->
                    _cartas.value = if (lista.isEmpty()) EstadoCartas.Vacio else EstadoCartas.Lista(lista)
                }
                .onFailure { _cartas.value = EstadoCartas.Error(mensajeDe(it)) }
        }
    }

    fun alternarFavorito() {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }

    private fun mensajeDe(e: Throwable): String = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}