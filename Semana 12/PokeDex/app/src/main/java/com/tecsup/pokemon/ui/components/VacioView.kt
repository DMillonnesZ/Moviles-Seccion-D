package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DoradoClaroVacio = Color(0xFFFFE7A0)
private val DoradoVacio = Color(0xFFF5C451)

// Vacío: Pokébola en círculo dorado y mensaje, en la misma tarjeta crema
@Composable
fun VacioView(
    mensaje: String = "No hay elementos para mostrar",
    modifier: Modifier = Modifier
) {
    TarjetaEstado(modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(DoradoClaroVacio, DoradoVacio)))
                .border(2.5.dp, Color.White, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Outlined.CatchingPokemon,
                contentDescription = null,
                tint = CafeMadera,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = mensaje,
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}