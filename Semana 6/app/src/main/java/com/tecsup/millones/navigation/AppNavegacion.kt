package com.tecsup.millones.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tecsup.millones.components.TiendaTopBar
import com.tecsup.millones.model.productosEjemplo
import com.tecsup.millones.screens.FavoritosScreen
import com.tecsup.millones.screens.InicioScreen
import com.tecsup.millones.screens.PantallaSimple
import kotlinx.coroutines.launch

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var favoritosIds by rememberSaveable { mutableStateOf(setOf<Int>()) }

    fun toggleFavorito(id: Int) {
        val producto = productosEjemplo.find { it.id == id }
        val esFavActualmente = id in favoritosIds
        favoritosIds = if (esFavActualmente) {
            favoritosIds - id
        } else {
            favoritosIds + id
        }
        val mensaje = if (!esFavActualmente) {
            "${producto?.nombre ?: "Producto"} agregado a favoritos"
        } else {
            "${producto?.nombre ?: "Producto"} quitado de favoritos"
        }
        scope.launch {
            snackbarHostState.showSnackbar(mensaje)
        }
    }

    fun mostrarReporteEnviado() {
        scope.launch {
            snackbarHostState.showSnackbar("Reporte enviado")
        }
    }

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
                cantidadFavoritos = favoritosIds.size,
                onDestinoClick = { destino ->
                    scope.launch { drawerState.close() }
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                composable(Destino.Inicio.ruta) {
                    InicioScreen(
                        favoritosIds = favoritosIds,
                        onToggleFavorito = { id -> toggleFavorito(id) },
                        onReportado = { mostrarReporteEnviado() }
                    )
                }
                composable(Destino.Pedidos.ruta) { PantallaSimple("Mis pedidos") }
                composable(Destino.Favoritos.ruta) {
                    FavoritosScreen(
                        favoritosIds = favoritosIds,
                        onToggleFavorito = { id -> toggleFavorito(id) },
                        onReportado = { mostrarReporteEnviado() }
                    )
                }
                composable(Destino.Perfil.ruta) { PantallaSimple("Perfil") }
            }
        }
    }
}
