package com.millones.navlab.navigation

sealed class Screen(val route: String) {

    object Login : Screen(route = "login")

    object Bienvenida : Screen(route = "bienvenida")

    object DirectorioAlumnos : Screen(route = "directorio")

    object ConfiguracionPerfil : Screen(route = "configuracion")

    object ExpedienteAcademico : Screen(route = "expediente/{studentId}") {
        fun createRoute(studentId: String): String = "expediente/$studentId"
    }
}