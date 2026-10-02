package com.tecsup.millones.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tecsup.millones.components.TiendaTopBar
import com.tecsup.millones.screens.InicioScreen
import com.tecsup.millones.screens.PantallaSimple
import kotlinx.coroutines.launch

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // La ruta activa se obtiene de la pila de navegación,
    // así el drawer siempre resalta la pantalla en la que estamos
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: Destino.Inicio.ruta
    val tituloActual = if (rutaActual == Destino.Inicio.ruta) {
        "Más vendidos"
    } else {
        destinosDrawer.find { it.ruta == rutaActual }?.titulo ?: "Más vendidos"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                rutaActual = rutaActual,
                onDestinoClick = { destino ->
                    // 1. Cerrar el menú lateral
                    scope.launch { drawerState.close() }
                    // 2. Abrir la pantalla elegida
                    navController.navigate(destino.ruta) {
                        popUpTo(Destino.Inicio.ruta) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onCerrarSesion = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TiendaTopBar(
                    subtitulo = tituloActual,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Destino.Inicio.ruta,
                modifier = Modifier.padding(padding)
            ) {
                composable(Destino.Inicio.ruta) { InicioScreen() }
                composable(Destino.Pedidos.ruta) { PantallaSimple("Mis pedidos") }
                composable(Destino.Favoritos.ruta) { PantallaSimple("Favoritos") }
                composable(Destino.Perfil.ruta) { PantallaSimple("Perfil") }
            }
        }
    }
}