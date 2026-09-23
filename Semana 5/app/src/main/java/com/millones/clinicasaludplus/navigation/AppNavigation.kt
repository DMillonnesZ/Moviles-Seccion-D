package com.millones.clinicasaludplus.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Inicio.route
    ) {

        composable(Screen.Inicio.route) {
            Text("Inicio")
        }

        composable(Screen.PerfilMedico.route) {
            Text("Perfil del médico")
        }

        composable(Screen.AgendarCita.route) {
            Text("Agendar cita")
        }

        composable(Screen.Confirmacion.route) {
            Text("Confirmación")
        }

        composable(Screen.MisCitas.route) {
            Text("Mis citas")
        }

        composable(Screen.HistorialMedico.route) {
            Text("Historial médico")
        }
    }
}