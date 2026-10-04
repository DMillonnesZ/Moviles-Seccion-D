package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saludplus.citas.ui.screens.auth.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {
        composable(Rutas.SPLASH) {
            SplashScreen(
                onComenzar = { /* se conecta con Registro en el siguiente commit */ },
                onYaTengoCuenta = { /* se conecta con Login más adelante */ }
            )
        }
    }
}