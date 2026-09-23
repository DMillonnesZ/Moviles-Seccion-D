package com.millones.navlab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.millones.navlab.screens.BienvenidaScreen
import com.millones.navlab.screens.ConfiguracionPerfilScreen
import com.millones.navlab.screens.DirectorioAlumnosScreen
import com.millones.navlab.screens.ExpedienteAcademicoScreen
import com.millones.navlab.screens.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Bienvenida.route) {
            BienvenidaScreen(navController)
        }
        composable(Screen.DirectorioAlumnos.route) {
            DirectorioAlumnosScreen(navController)
        }
        composable(Screen.ConfiguracionPerfil.route) {
            ConfiguracionPerfilScreen(navController)
        }
        composable(
            route = Screen.ExpedienteAcademico.route,
            arguments = listOf(
                navArgument(name = "studentId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            ExpedienteAcademicoScreen(navController, studentId)
        }
    }
}