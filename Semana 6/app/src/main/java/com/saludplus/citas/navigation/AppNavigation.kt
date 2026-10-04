package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saludplus.citas.ui.screens.auth.LoginScreen
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen

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
                onLoginExitoso = { /* se conecta con Inicio en el siguiente commit */ },
                onIrRegistro = {
                    navController.navigate(Rutas.REGISTRO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onAtras = { navController.popBackStack() }
            )
        }
    }
}