package com.tecsup.huellitasvet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.huellitasvet.VetApp
import com.tecsup.huellitasvet.ui.dueno.SplashScreen
import com.tecsup.huellitasvet.ui.veterinario.ServiciosScreen
import com.tecsup.huellitasvet.viewmodel.ServiciosViewModel
import com.tecsup.huellitasvet.viewmodel.VetViewModelFactory
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.theme.VetTheme

@Composable
fun NavVet() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as VetApp
    val factory = VetViewModelFactory(app.repository)

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            SplashScreen(
                // TODO Paso 6: ambos irán a LOGIN
                onComenzar = { nav.navigate(Rutas.SERVICIOS) },
                onYaTengoCuenta = { nav.navigate(Rutas.SERVICIOS) }
            )
        }

        composable(Rutas.SERVICIOS) {
            val vm: ServiciosViewModel = viewModel(factory = factory)
            VetTheme(rol = Rol.ADMIN) {   // pantalla del veterinario → azul
                ServiciosScreen(
                    vm = vm,
                    onEditar = { /* TODO Paso 13 */ },
                    onAtras = { nav.popBackStack() }
                )
            }
        }

        // TODO: resto de destinos en cada paso
    }
}