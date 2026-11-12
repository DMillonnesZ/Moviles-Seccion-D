package com.tecsup.pokemon.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.ItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Datos que muestra la barra de navegación (por ahora, la cantidad de favoritos)
class BarraViewModel(repo: ItemRepository) : ViewModel() {

    val cantidadFavoritos: StateFlow<Int> = repo.favoritos
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}