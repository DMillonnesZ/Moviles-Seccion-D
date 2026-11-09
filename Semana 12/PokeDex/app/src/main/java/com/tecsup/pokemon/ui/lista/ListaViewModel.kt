package com.tecsup.pokemon.ui.lista

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListaViewModel(
    private val repo: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Llega desde la navegación: lista/{tipo}
    private val tipo: String = checkNotNull(savedStateHandle["tipo"])

    // Título en español del tipo
    val titulo: StateFlow<String> = repo.tipos
        .map { lista -> lista.firstOrNull { it.nombre == tipo }?.nombreEs ?: tipo }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), tipo)

    // Pokémon de este tipo
    val estado: StateFlow<UiState<List<Item>>> = repo.itemsPorTipo(tipo)
        .map<List<Item>, UiState<List<Item>>> { UiState.Exito(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    fun alternarFavorito(id: Int) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }
}