package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.tecsup.pokemon.util.ImagenPerfil

private val CremaFoto = Color(0xFFFFF4DF)

// Foto redonda del usuario (Base64 de Firestore); sin foto muestra la Pokébola.
// La usan el Perfil y la lista de usuarios del Admin.
@Composable
fun FotoUsuario(
    foto: String?,
    tamano: Dp,
    modifier: Modifier = Modifier
) {
    val imagen = remember(foto) { ImagenPerfil.aImagen(foto) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(CremaFoto)
    ) {
        if (imagen != null) {
            Image(
                bitmap = imagen,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Pokebola(apertura = 0f, brillo = 0f, modifier = Modifier.size(tamano * 0.64f))
        }
    }
}