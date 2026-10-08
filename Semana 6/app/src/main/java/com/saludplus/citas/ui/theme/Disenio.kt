package com.saludplus.citas.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object Espaciado {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
}

object Formas {
    val pequena = RoundedCornerShape(12.dp)
    val mediana = RoundedCornerShape(16.dp)
    val grande = RoundedCornerShape(20.dp)
    val extraGrande = RoundedCornerShape(24.dp)
    val pildora = RoundedCornerShape(50)
}

object Elevaciones {
    val ninguna = 0.dp
    val baja = 2.dp
    val media = 4.dp
    val alta = 8.dp
}

object Duraciones {
    const val corta = 150
    const val media = 300
    const val larga = 500
}

object ColoresSemanticos {
    val Exito = Color(0xFF16A34A)
    val ExitoFondo = Color(0xFFDCFCE7)

    val Advertencia = Color(0xFFD97706)
    val AdvertenciaFondo = Color(0xFFFEF3C7)

    val Error = Color(0xFFDC2626)
    val ErrorFondo = Color(0xFFFEE2E2)

    val Info = Color(0xFF2563EB)
    val InfoFondo = Color(0xFFE8F0FE)

    val Turquesa = Color(0xFF0D9488)
    val TurquesaClaro = Color(0xFFCCFBF1)
}

object Degradados {
    val Principal = Brush.horizontalGradient(
        colors = listOf(AzulPrimario, Color(0xFF0D9488))
    )
    val AzulTurquesaVertical = Brush.verticalGradient(
        colors = listOf(AzulPrimario, Color(0xFF0284C7))
    )
    val Suave = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F2FE), Color(0xFFF3F7FC))
    )
    val Tarjeta = Brush.linearGradient(
        colors = listOf(Color.White, Color(0xFFF8FAFC))
    )
}
