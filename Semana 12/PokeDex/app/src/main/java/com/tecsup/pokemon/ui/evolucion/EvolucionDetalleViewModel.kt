package com.tecsup.pokemon.ui.evolucion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.EvolucionRepository
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.CadenaEvolutiva
import com.tecsup.pokemon.domain.model.Tipo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EvolucionDetalleViewModel(
    repo: EvolucionRepository,
    itemRepo: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Llega desde la navegación: evolucion/{cadenaId}
    private val cadenaId: Int = checkNotNull(savedStateHandle["cadenaId"])

    // La cadena desde Room (null hasta que Room responda)
    val cadena: StateFlow<CadenaEvolutiva?> = repo.cadenas
        .map { lista -> lista.firstOrNull { it.id == cadenaId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Tipos de cada Pokémon (para colores y chips)
    val tiposPorPokemon: StateFlow<Map<Int, List<String>>> = repo.tiposPorPokemon
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    // Nombre en español e ícono de cada tipo
    val tiposInfo: StateFlow<Map<String, Tipo>> = itemRepo.tipos
        .map { lista -> lista.associateBy { it.nombre } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}