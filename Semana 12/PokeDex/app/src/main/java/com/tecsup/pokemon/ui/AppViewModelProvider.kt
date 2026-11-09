package com.tecsup.pokemon.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tecsup.pokemon.PokeDexApp
import com.tecsup.pokemon.ui.detalle.DetalleViewModel
import com.tecsup.pokemon.ui.lista.ListaViewModel
import com.tecsup.pokemon.ui.tipos.TiposViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TiposViewModel(pokeDexApp().container.itemRepository)
        }
        initializer {
            ListaViewModel(
                repo = pokeDexApp().container.itemRepository,
                savedStateHandle = createSavedStateHandle()
            )
        }
        initializer {
            DetalleViewModel(
                repo = pokeDexApp().container.itemRepository,
                savedStateHandle = createSavedStateHandle()
            )
        }
    }
}

// Obtiene PokeDexApp para acceder al AppContainer
private fun CreationExtras.pokeDexApp(): PokeDexApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PokeDexApp