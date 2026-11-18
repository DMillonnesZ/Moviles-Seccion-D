package com.millones.campusgps.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

// Avatar redondo: la foto de perfil (Base64) o, si no hay, la inicial sobre el degradado azul
@Composable
fun AvatarUsuario(
    nombre: String,
    fotoBase64: String?,
    tamano: Dp,
    modifier: Modifier = Modifier,
    grosorBorde: Dp = 2.dp,
    colorBorde: Color = Color.White.copy(alpha = 0.8f)
) {
    val imagen = rememberImagenBase64(fotoBase64)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(DegradadosCampus.Primario)
            .border(grosorBorde, colorBorde, CircleShape)
    ) {
        if (imagen != null) {
            Image(
                bitmap = imagen,
                contentDescription = "Foto de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.ExtraBold,
                fontSize = (tamano.value * 0.42f).sp
            )
        }
    }
}
