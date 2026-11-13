package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R

// Error: Pikachu animado, título según el problema, mensaje y botón Reintentar
@Composable
fun ErrorView(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sinConexion = mensaje.contains("conexión", ignoreCase = true) ||
            mensaje.contains("internet", ignoreCase = true)

    TarjetaEstado(modifier = modifier) {
        GifAnimado(
            recurso = R.drawable.pikachu_error,
            descripcion = null,
            modifier = Modifier.size(130.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (sinConexion) "¡Sin conexión!" else "¡Ups! Algo salió mal",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = mensaje,
            color = CafeMadera.copy(alpha = 0.8f),
            fontFamily = FuenteMadera,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        BotonPildora(
            texto = "Reintentar",
            fondo = FondoPildora.Dorado,
            colorTexto = CafeMadera,
            onClick = onReintentar,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}