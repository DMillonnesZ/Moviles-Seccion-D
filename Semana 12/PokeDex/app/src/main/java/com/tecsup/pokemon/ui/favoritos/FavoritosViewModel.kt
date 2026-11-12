package com.tecsup.pokemon.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.EvolucionRepository
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.util.Traductor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer

// Opciones del menú Filtrar
enum class OrdenFavoritos(val etiqueta: String) {
    NUM_ASC("Número ↑"),
    NUM_DESC("Número ↓"),
    NOMBRE_ASC("Nombre A → Z"),
    NOMBRE_DESC("Nombre Z → A")
}

@OptIn(FlowPreview::class)
class FavoritosViewModel(
    private val repo: ItemRepository,
    evolucionRepo: EvolucionRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // null = "Todos"
    private val _tipo = MutableStateFlow<String?>(null)
    val tipo: StateFlow<String?> = _tipo.asStateFlow()

    private val _orden = MutableStateFlow(OrdenFavoritos.NUM_ASC)
    val orden: StateFlow<OrdenFavoritos> = _orden.asStateFlow()

    // Último Pokémon quitado (para el aviso de "Deshacer")
    private val _quitado = MutableStateFlow<Item?>(null)
    val quitado: StateFlow<Item?> = _quitado.asStateFlow()

    // Nombre en español e ícono de cada tipo
    val tiposInfo: StateFlow<Map<String, Tipo>> = repo.tipos
        .map { lista -> lista.associateBy { it.nombre } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    // Favoritos con sus tipos (la relación Pokémon - tipo está guardada en Room)
    private val favoritosConTipos: Flow<List<Item>> =
        combine(repo.favoritos, evolucionRepo.tiposPorPokemon) { lista, tipos ->
            lista.map { it.copy(tipos = tipos[it.id].orEmpty()) }
        }

    // Todos los favoritos ordenados por número (para compartir)
    val todos: StateFlow<List<Item>> = favoritosConTipos
        .map { lista -> lista.sortedBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Cantidad total de favoritos (sin filtros)
    val total: StateFlow<Int> = repo.favoritos
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // Cuántos favoritos hay de cada tipo, de más a menos (para el resumen)
    val conteoTipos: StateFlow<List<Pair<String, Int>>> = favoritosConTipos
        .map { lista ->
            lista.flatMap { it.tipos }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedWith(
                    compareByDescending<Pair<String, Int>> { it.second }
                        .thenBy { Traductor.tipoEs(it.first) }
                )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Solo los tipos que aparecen en los favoritos (para los chips)
    val tiposDisponibles: StateFlow<List<String>> = favoritosConTipos
        .map { lista -> lista.flatMap { it.tipos }.distinct().sortedBy { Traductor.tipoEs(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Favoritos filtrados por búsqueda (con debounce), tipo y orden
    val estado: StateFlow<UiState<List<Item>>> = combine(
        favoritosConTipos,
        _busqueda.debounce(300).map { normalizar(it) }.distinctUntilChanged(),
        _tipo,
        _orden
    ) { lista, texto, tipo, orden ->
        val filtrada = lista
            .filter { tipo == null || tipo in it.tipos }
            .filter { texto.isEmpty() || normalizar(it.nombre).contains(texto) || it.id.toString() == texto }
        val ordenada = when (orden) {
            OrdenFavoritos.NUM_ASC -> filtrada.sortedBy { it.id }
            OrdenFavoritos.NUM_DESC -> filtrada.sortedByDescending { it.id }
            OrdenFavoritos.NOMBRE_ASC -> filtrada.sortedBy { it.nombre }
            OrdenFavoritos.NOMBRE_DESC -> filtrada.sortedByDescending { it.nombre }
        }
        val resultado: UiState<List<Item>> = UiState.Exito(ordenada)
        resultado
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarTipo(nuevo: String?) {
        _tipo.value = nuevo
    }

    fun cambiarOrden(nuevo: OrdenFavoritos) {
        _orden.value = nuevo
    }

    // Quita de favoritos y guarda cuál fue, por si se quiere deshacer
    fun quitar(item: Item) {
        viewModelScope.launch {
            repo.alternarFavorito(item.id)
            _quitado.value = item
        }
    }

    // Vuelve a marcar como favorito el último quitado
    fun deshacer() {
        val item = _quitado.value ?: return
        _quitado.value = null
        viewModelScope.launch { repo.alternarFavorito(item.id) }
    }

    fun ocultarAviso() {
        _quitado.value = null
    }

    // Minúsculas y sin tildes
    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
}