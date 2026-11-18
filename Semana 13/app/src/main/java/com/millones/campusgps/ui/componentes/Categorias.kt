package com.millones.campusgps.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Ícono y color de cada categoría de Lugar (solo presentación, por eso vive en la UI)
data class EstiloCategoria(val icono: ImageVector, val color: Color)

fun estiloCategoria(categoria: String): EstiloCategoria = when (categoria.lowercase()) {
    "estudio" -> EstiloCategoria(Icons.AutoMirrored.Rounded.MenuBook, CampusColores.AzulPrimario)
    "comida" -> EstiloCategoria(Icons.Rounded.Restaurant, CampusColores.Ambar)
    "eventos" -> EstiloCategoria(Icons.Rounded.Celebration, CampusColores.Violeta)
    "servicios" -> EstiloCategoria(Icons.Rounded.Handyman, CampusColores.Exito)
    else -> EstiloCategoria(Icons.Rounded.Place, CampusColores.TextoSecundario)
}
