package com.tecsup.millones.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destino(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    object Inicio : Destino("inicio", "Inicio", Icons.Default.Home)
    object Pedidos : Destino("pedidos", "Mis pedidos", Icons.Default.ShoppingCart)
    object Favoritos : Destino("favoritos", "Favoritos", Icons.Default.Favorite)
    object Perfil : Destino("perfil", "Perfil", Icons.Default.Person)
}

val destinosDrawer = listOf(
    Destino.Inicio,
    Destino.Pedidos,
    Destino.Favoritos,
    Destino.Perfil
)