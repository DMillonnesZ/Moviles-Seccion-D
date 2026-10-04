package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saludplus.citas.ui.screens.auth.LoginScreen
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen
import com.saludplus.citas.ui.screens.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

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
                onAgendar = { /* se conecta con Especialidades en su commit */ },
                onMisCitas = { /* se conecta con la barra de navegación en el siguiente commit */ },
                onMisDatos = { /* se conecta con la barra de navegación en el siguiente commit */ },
                onResultados = { /* se conecta con la barra de navegación en el siguiente commit */ },
                onEspecialidad = { /* se conecta con Médicos en su commit */ },
                onVerEspecialidades = { /* se conecta con Especialidades en su commit */ }
            )
        }
    }
}