package com.tecsup.pokemon.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tecsup.pokemon.PokeDexApp
import com.tecsup.pokemon.ui.admin.AdminViewModel
import com.tecsup.pokemon.ui.detalle.DetalleViewModel
import com.tecsup.pokemon.ui.evolucion.EvolucionDetalleViewModel
import com.tecsup.pokemon.ui.evolucion.EvolucionViewModel
import com.tecsup.pokemon.ui.favoritos.FavoritosViewModel
import com.tecsup.pokemon.ui.lista.ListaViewModel
import com.tecsup.pokemon.ui.login.LoginViewModel
import com.tecsup.pokemon.ui.navigation.BarraViewModel
import com.tecsup.pokemon.ui.navigation.SesionViewModel
import com.tecsup.pokemon.ui.perfil.PerfilViewModel
import com.tecsup.pokemon.ui.registro.RegistroViewModel
import com.tecsup.pokemon.ui.tipos.TiposViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            SesionViewModel(pokeDexApp().container.authRepository)
        }
        initializer {
            BarraViewModel(pokeDexApp().container.itemRepository)
        }
        initializer {
            LoginViewModel(pokeDexApp().container.authRepository)
        }
        initializer {
            RegistroViewModel(pokeDexApp().container.authRepository)
        }
        // Inicio: tipos sin los descontinuados + anuncio del admin
        initializer {
            TiposViewModel(
                repo = pokeDexApp().container.itemRepository,
                catalogoRepo = pokeDexApp().container.catalogoRepository,
                notificador = pokeDexApp().container.notificador
            )
        }
        // Lista: Pokémon sin los descontinuados
        initializer {
            ListaViewModel(
                repo = pokeDexApp().container.itemRepository,
                catalogoRepo = pokeDexApp().container.catalogoRepository,
                savedStateHandle = createSavedStateHandle()
            )
        }
        // Detalle: avisa si el Pokémon fue descontinuado
        initializer {
            DetalleViewModel(
                repo = pokeDexApp().container.itemRepository,
                cartasRepo = pokeDexApp().container.cartasRepository,
                catalogoRepo = pokeDexApp().container.catalogoRepository,
                savedStateHandle = createSavedStateHandle()
            )
        }
        initializer {
            EvolucionViewModel(pokeDexApp().container.evolucionRepository)
        }
        initializer {
            EvolucionDetalleViewModel(
                repo = pokeDexApp().container.evolucionRepository,
                itemRepo = pokeDexApp().container.itemRepository,
                savedStateHandle = createSavedStateHandle()
            )
        }
        // Favoritos: marca los descontinuados
        initializer {
            FavoritosViewModel(
                repo = pokeDexApp().container.itemRepository,
                evolucionRepo = pokeDexApp().container.evolucionRepository,
                catalogoRepo = pokeDexApp().container.catalogoRepository
            )
        }
        // Perfil: datos, foto y contraseña (Firebase)
        initializer {
            PerfilViewModel(pokeDexApp().container.authRepository)
        }
        // Admin: cuentas + catálogo (Firestore) + base local (Room) + tipos de cada Pokémon
        initializer {
            AdminViewModel(
                repo = pokeDexApp().container.itemRepository,
                evolucionRepo = pokeDexApp().container.evolucionRepository,
                usuariosRepo = pokeDexApp().container.usuariosRepository,
                catalogoRepo = pokeDexApp().container.catalogoRepository,
                notificador = pokeDexApp().container.notificador
            )
        }
    }
}

// Obtiene PokeDexApp para acceder al AppContainer
private fun CreationExtras.pokeDexApp(): PokeDexApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PokeDexApp