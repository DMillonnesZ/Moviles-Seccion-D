package com.tecsup.pokemon.ui.lista

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.OrdenPokemon
import com.tecsup.pokemon.domain.model.Tipo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Pokémon por página
const val POKEMON_POR_PAGINA = 6

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ListaViewModel(
    private val repo: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Llega desde la navegación: lista/{tipo}
    val tipo: String = checkNotNull(savedStateHandle["tipo"])

    // Todos los tipos (para el título y los íconos con nombre en español)
    val tiposInfo: StateFlow<Map<String, Tipo>> = repo.tipos
        .map { lista -> lista.associateBy { it.nombre } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    // Texto del buscador: se muestra al instante, pero se filtra con debounce
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _orden = MutableStateFlow(OrdenPokemon.NUM_ASC)
    val orden: StateFlow<OrdenPokemon> = _orden.asStateFlow()

    private val _soloFavoritos = MutableStateFlow(false)
    val soloFavoritos: StateFlow<Boolean> = _soloFavoritos.asStateFlow()

    // Página que se está viendo (empieza en 0)
    private val _pagina = MutableStateFlow(0)
    val pagina: StateFlow<Int> = _pagina.asStateFlow()

    // Pokémon con Paging 3: se vuelve a consultar cuando cambia la búsqueda, el orden o el filtro
    val pokemon: Flow<PagingData<Item>> = combine(
        _busqueda.debounce(300).map { limpiarBusqueda(it) }.distinctUntilChanged(),
        _orden,
        _soloFavoritos
    ) { texto, orden, soloFavoritos -> Triple(texto, orden, soloFavoritos) }
        .flatMapLatest { (texto, orden, soloFavoritos) ->
            repo.paginarPorTipo(tipo, texto, soloFavoritos, orden)
        }
        .cachedIn(viewModelScope)

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
        _pagina.value = 0
    }

    fun cambiarOrden(nuevo: OrdenPokemon) {
        _orden.value = nuevo
        _pagina.value = 0
    }

    fun alternarSoloFavoritos() {
        _soloFavoritos.value = !_soloFavoritos.value
        _pagina.value = 0
    }

    fun irAPagina(nueva: Int) {
        _pagina.value = nueva.coerceAtLeast(0)
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }

    // "Char " -> "char"; "#025" -> "25" (para buscar por número)
    private fun limpiarBusqueda(texto: String): String {
        val limpio = texto.trim().lowercase().removePrefix("#")
        return if (limpio.isNotEmpty() && limpio.all { it.isDigit() }) {
            limpio.trimStart('0').ifEmpty { "0" }
        } else {
            limpio
        }
    }
}