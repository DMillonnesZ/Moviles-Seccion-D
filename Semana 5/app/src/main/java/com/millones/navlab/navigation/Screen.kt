package com.millones.navlab.navigation

sealed class Screen(val route: String) {

    // Pantalla de inicio — punto de entrada de la app
    object Home : Screen(route = "home")

    // Pantalla que muestra la lista de elementos
    object List : Screen(route = "list")

    // Pantalla del perfil del usuario
    object Profile : Screen(route = "profile")

    object Detail : Screen(route = "detail/{itemId}") {

        fun createRoute(itemId: Int): String = "detail/$itemId"
    }
}