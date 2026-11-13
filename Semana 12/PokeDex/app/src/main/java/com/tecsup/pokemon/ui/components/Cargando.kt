package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R

private val CremaCarga = Color(0xFFFFF4DF)

// Pikachu pixel animado sobre la Pokébola + mensaje en píldora crema
@Composable
fun Cargando(
    mensaje: String = "Cargando...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GifAnimado(
            recurso = R.drawable.pikachu_carga,
            pixelado = true,
            descripcion = "Cargando",
            modifier = Modifier
                .height(120.dp)
                .aspectRatio(100f / 146f)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = mensaje,
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .shadow(3.dp, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(CremaCarga)
                .border(1.dp, CafeMadera.copy(alpha = 0.25f), RoundedCornerShape(50))
                .padding(horizontal = 18.dp, vertical = 6.dp)
        )
    }
}