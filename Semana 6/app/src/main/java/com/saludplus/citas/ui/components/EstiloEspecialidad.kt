package com.saludplus.citas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PregnantWoman
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class EstiloEspecialidad(
    val icono: ImageVector,
    val color: Color,
    val fondo: Color
)

// Ícono y colores de cada especialidad, según su id
fun estiloEspecialidad(id: Int): EstiloEspecialidad = when (id) {
    1 -> EstiloEspecialidad(Icons.Default.MedicalServices, Color(0xFF2563EB), Color(0xFFE3EDFF))
    2 -> EstiloEspecialidad(Icons.Default.ChildCare, Color(0xFFF28C28), Color(0xFFFFEBD6))
    3 -> EstiloEspecialidad(Icons.Default.PregnantWoman, Color(0xFFE5484D), Color(0xFFFDE4E4))
    4 -> EstiloEspecialidad(Icons.Default.Favorite, Color(0xFFDC2626), Color(0xFFFDE4E4))
    5 -> EstiloEspecialidad(Icons.Default.Face, Color(0xFF7C4DFF), Color(0xFFEBE3FF))
    6 -> EstiloEspecialidad(Icons.Default.SentimentSatisfied, Color(0xFF0EA5A4), Color(0xFFDDF5F4))
    7 -> EstiloEspecialidad(Icons.Default.Visibility, Color(0xFF2563EB), Color(0xFFE3EDFF))
    8 -> EstiloEspecialidad(Icons.Default.Accessibility, Color(0xFF22A05B), Color(0xFFDDF3E6))
    else -> EstiloEspecialidad(Icons.Default.MedicalServices, Color(0xFF2563EB), Color(0xFFE3EDFF))
}