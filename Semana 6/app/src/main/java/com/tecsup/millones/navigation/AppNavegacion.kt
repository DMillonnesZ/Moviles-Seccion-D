package com.tecsup.millones.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tecsup.millones.components.TiendaTopBar
import com.tecsup.millones.screens.InicioScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavegacion() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Sección marcada como activa (en el Commit 5 vendrá del NavController)
    var rutaActual by remember { mutableStateOf(Destino.Inicio.ruta) }

    // ModalNavigationDrawer envuelve al Scaffold de la pantalla
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                rutaActual = rutaActual,
                onDestinoClick = { destino ->
                    rutaActual = destino.ruta
                    scope.launch { drawerState.close() }
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
                    subtitulo = "Más vendidos",
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { padding ->
            InicioScreen(modifier = Modifier.padding(padding))
        }
    }
}