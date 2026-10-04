package com.saludplus.citas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage

// Iniciales del médico, sin contar "Dr." ni "Dra."
fun iniciales(nombre: String): String {
    return nombre.split(" ")
        .filter { it != "Dr." && it != "Dra." }
        .take(2)
        .joinToString("") { it.first().toString() }
}

// Foto circular del médico descargada de internet con Coil.
// Mientras carga, o si no hay internet, muestra las iniciales.
@Composable
fun AvatarMedico(
    nombre: String,
    foto: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 64.dp
) {
    SubcomposeAsyncImage(
        model = foto,
        contentDescription = nombre,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(Color(0xFFE3EDFF)),
        loading = { InicialesAvatar(nombre) },
        error = { InicialesAvatar(nombre) }
    )
}

@Composable
private fun InicialesAvatar(nombre: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales(nombre),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB)
        )
    }
}