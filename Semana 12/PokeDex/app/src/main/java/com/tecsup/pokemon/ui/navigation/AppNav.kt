package com.tecsup.pokemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.pokemon.ui.AppViewModelProvider
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.tipos.TiposScreen

object Rutas {
    const val TIPOS = "tipos"
    const val LISTA = "lista/{tipo}"
    fun lista(tipo: String) = "lista/$tipo"
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
                onItemClick = { },   // el Detalle se conecta en el H7
                onVolver = { navController.popBackStack() }
            )
        }
    }
}