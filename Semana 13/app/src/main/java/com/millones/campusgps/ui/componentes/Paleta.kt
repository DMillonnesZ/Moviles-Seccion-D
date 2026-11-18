package com.millones.campusgps.ui.componentes

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// Sistema de color único: azules profundos, celeste y menta para acentos
object CampusColores {
    val AzulProfundo = Color(0xFF0A1F44)
    val AzulNoche = Color(0xFF06142E)
    val AzulPrimario = Color(0xFF1565C0)
    val AzulCielo = Color(0xFF3DA5F4)
    val CelesteHielo = Color(0xFFE8F2FD)
    val VerdeMenta = Color(0xFF6EE7B7)
    val Blanco = Color(0xFFFFFFFF)
    val Superficie = Color(0xFFF3F6FB)
    val TextoOscuro = Color(0xFF0F1E3D)
    val TextoSecundario = Color(0xFF6B7A90)
    val Borde = Color(0xFFDCE3EE)
    val Error = Color(0xFFE5484D)
    val Exito = Color(0xFF1FA971)
    val Ambar = Color(0xFFF5A524)
    val Violeta = Color(0xFF8B5CF6)
}

// Una sola fuente para toda la app (en la PokeDex era FuenteMadera).
// Para usar una propia: copia los .ttf a res/font y cambia esta línea, por ejemplo:
// FontFamily(Font(R.font.nunito_regular), Font(R.font.nunito_bold, FontWeight.Bold))
val FuenteCampus: FontFamily = FontFamily.Default

// Degradados reutilizables (equivalen a FondoPildora y a los fondos crema de la PokeDex)
object DegradadosCampus {
    val Primario = Brush.horizontalGradient(listOf(CampusColores.AzulPrimario, CampusColores.AzulCielo))
    val Noche = Brush.verticalGradient(listOf(CampusColores.AzulProfundo, CampusColores.AzulNoche))
    val Tarjeta = Brush.verticalGradient(listOf(Color.White, CampusColores.CelesteHielo))
    val Suave = Brush.verticalGradient(listOf(Color.White, CampusColores.Superficie))
    val BordeTarjeta = Brush.verticalGradient(
        listOf(CampusColores.AzulCielo.copy(alpha = 0.55f), CampusColores.AzulPrimario.copy(alpha = 0.25f))
    )
    val Menta = Brush.verticalGradient(listOf(Color(0xFFA7F3D0), CampusColores.VerdeMenta))
    val Peligro = Brush.verticalGradient(listOf(Color(0xFFFF8A8E), CampusColores.Error))
}
