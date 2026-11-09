package com.tecsup.pokemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.pokemon.ui.AppViewModelProvider
import com.tecsup.pokemon.ui.detalle.DetalleScreen
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.tipos.TiposScreen

object Rutas {
    const val TIPOS = "tipos"
    const val LISTA = "lista/{tipo}"
    const val DETALLE = "detalle/{id}"
    fun lista(tipo: String) = "lista/$tipo"
    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.TIPOS) {

        composable(Rutas.TIPOS) {
            TiposScreen(
                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                onTipoClick = { tipo -> navController.navigate(Rutas.lista(tipo)) }
            )
        }

        composable(
            route = Rutas.LISTA,
            arguments = listOf(navArgument("tipo") { type = NavType.StringType })
        ) {
            ListaScreen(
                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                onItemClick = { id -> navController.navigate(Rutas.detalle(id)) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            DetalleScreen(
                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                onVolver = { navController.popBackStack() }
            )
        }
    }
}