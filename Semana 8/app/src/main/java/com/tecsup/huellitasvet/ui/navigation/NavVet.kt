package com.tecsup.huellitasvet.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.huellitasvet.VetApp
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.components.VetButton
import com.tecsup.huellitasvet.ui.dueno.LoginScreen
import com.tecsup.huellitasvet.ui.dueno.SplashScreen
import com.tecsup.huellitasvet.ui.theme.VetTheme
import com.tecsup.huellitasvet.ui.veterinario.ServiciosScreen
import com.tecsup.huellitasvet.viewmodel.ServiciosViewModel
import com.tecsup.huellitasvet.viewmodel.SesionViewModel
import com.tecsup.huellitasvet.viewmodel.VetViewModelFactory

@Composable
fun NavVet() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as VetApp
    val factory = remember { VetViewModelFactory(app.repository, app.prefs) }

    // Sesión compartida por toda la app
    val sesionVm: SesionViewModel = viewModel(factory = factory)
    val rolActivo by sesionVm.rolActivo.collectAsState()
    val oscuro by sesionVm.modoOscuro.collectAsState()

    val irAPrincipal: (Rol) -> Unit = { rol ->
        nav.navigate(if (rol == Rol.CLIENTE) Rutas.INICIO else Rutas.PANEL) {
            popUpTo(nav.graph.id) { inclusive = true }   // limpia la pila
        }
    }
    val cerrarSesion: () -> Unit = {
        sesionVm.cerrarSesion {
            nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
        }
    }

    // Tema según el rol activo: verde (Dueño) / azul (Veterinario)
    VetTheme(rol = rolActivo ?: Rol.CLIENTE, oscuro = oscuro) {
        NavHost(navController = nav, startDestination = Rutas.SPLASH) {

            composable(Rutas.SPLASH) {
                // Si ya hay perfil guardado → salta el login
                LaunchedEffect(Unit) {
                    sesionVm.restaurarSesion()?.let(irAPrincipal)
                }
                SplashScreen(
                    onComenzar = { nav.navigate(Rutas.LOGIN) },
                    onYaTengoCuenta = { nav.navigate(Rutas.LOGIN) }
                )
            }

            composable(Rutas.LOGIN) {
                LoginScreen(vm = sesionVm, onIngresar = irAPrincipal)
            }

            // TODO Paso 7: reemplazar por InicioScreen
            composable(Rutas.INICIO) {
                val usuario by sesionVm.usuarioActual.collectAsState()
                PantallaTemporal("Inicio del dueño", usuario?.nombre, cerrarSesion)
            }

            // TODO Paso 11: reemplazar por PanelScreen
            composable(Rutas.PANEL) {
                val usuario by sesionVm.usuarioActual.collectAsState()
                PantallaTemporal("Panel del veterinario", usuario?.nombre, cerrarSesion) {
                    nav.navigate(Rutas.SERVICIOS)
                }
            }

            composable(Rutas.SERVICIOS) {
                val vm: ServiciosViewModel = viewModel(factory = factory)
                ServiciosScreen(
                    vm = vm,
                    onEditar = { /* TODO Paso 13 */ },
                    onAtras = { nav.popBackStack() }
                )
            }
        }
    }
}

/** Pantalla provisional para probar el login (se reemplaza en los siguientes pasos) */
@Composable
private fun PantallaTemporal(
    titulo: String,
    nombre: String?,
    onCerrarSesion: () -> Unit,
    onServicios: (() -> Unit)? = null
) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        Text("Hola, ${nombre ?: ""}", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        if (onServicios != null) {
            VetButton("Ver servicios", onServicios)
            Spacer(Modifier.height(12.dp))
        }
        OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
    }
}