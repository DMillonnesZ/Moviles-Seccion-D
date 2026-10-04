package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saludplus.citas.ui.screens.agendamiento.EspecialidadesScreen
import com.saludplus.citas.ui.screens.agendamiento.MedicosScreen
import com.saludplus.citas.ui.screens.auth.LoginScreen
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen
import com.saludplus.citas.ui.screens.citas.MisCitasScreen
import com.saludplus.citas.ui.screens.home.HomeScreen
import com.saludplus.citas.ui.screens.perfil.PerfilScreen
import com.saludplus.citas.ui.screens.resultados.ResultadosScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Cambio de pestaña en la barra inferior: no apila pantallas repetidas
    val irA: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {
        composable(Rutas.SPLASH) {
            SplashScreen(
                onComenzar = { navController.navigate(Rutas.REGISTRO) },
                onYaTengoCuenta = { navController.navigate(Rutas.LOGIN) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                },
                onIrLogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                },
                onTerminos = { /* se conecta con Términos en el reto extra */ }
            )
        }
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                },
                onIrRegistro = {
                    navController.navigate(Rutas.REGISTRO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onAtras = { navController.popBackStack() }
            )
        }
        composable(Rutas.HOME) {
            HomeScreen(
                onNotificaciones = { /* se conecta con Notificaciones en el reto extra */ },
                onAgendar = { navController.navigate(Rutas.ESPECIALIDADES) },
                onMisCitas = { irA(Rutas.MIS_CITAS) },
                onMisDatos = { irA(Rutas.PERFIL) },
                onResultados = { irA(Rutas.RESULTADOS) },
                onEspecialidad = { id -> navController.navigate(Rutas.medicos(id)) },
                onVerEspecialidades = { navController.navigate(Rutas.ESPECIALIDADES) },
                onNavegar = irA
            )
        }
        composable(Rutas.ESPECIALIDADES) {
            EspecialidadesScreen(
                onAtras = { navController.popBackStack() },
                onEspecialidad = { id -> navController.navigate(Rutas.medicos(id)) }
            )
        }
        composable(
            route = Rutas.MEDICOS,
            arguments = listOf(navArgument("especialidadId") { type = NavType.IntType })
        ) { entrada ->
            val especialidadId = entrada.arguments?.getInt("especialidadId") ?: 0
            MedicosScreen(
                especialidadId = especialidadId,
                onAtras = { navController.popBackStack() },
                onMedico = { /* se conecta con Fecha y hora en el siguiente commit */ }
            )
        }
        composable(Rutas.MIS_CITAS) { MisCitasScreen(onNavegar = irA) }
        composable(Rutas.RESULTADOS) { ResultadosScreen(onNavegar = irA) }
        composable(Rutas.PERFIL) { PerfilScreen(onNavegar = irA) }
    }
}