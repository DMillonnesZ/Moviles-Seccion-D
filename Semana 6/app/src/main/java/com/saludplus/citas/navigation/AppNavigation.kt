package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {
        composable(Rutas.SPLASH) {
            SplashScreen(
                onComenzar = { navController.navigate(Rutas.REGISTRO) },
                onYaTengoCuenta = { /* se conecta con Login en el siguiente commit */ }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = { /* se conecta con Login en el siguiente commit */ },
                onIrLogin = { /* se conecta con Login en el siguiente commit */ },
                onTerminos = { /* se conecta con Términos en el reto extra */ }
            )
        }
    }
}