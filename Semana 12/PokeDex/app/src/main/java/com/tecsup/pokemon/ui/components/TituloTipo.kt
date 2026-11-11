package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tecsup.pokemon.R

private val DegradadoTituloTipo = Brush.verticalGradient(
    listOf(Color(0xFFAD671C), Color(0xFF693106), Color(0xFF692B04))
)

// Letrero con el ícono del tipo (de la API) y su nombre en español, centrado dentro de la madera
@Composable
fun TituloTipo(
    nombre: String,
    iconoUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .widthIn(max = 240.dp)
            .fillMaxWidth()
            .height(92.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_titulo_tipo),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 36.dp)
        ) {
            if (iconoUrl != null) {
                AsyncImage(
                    model = iconoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(7.dp))
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = nombre,
                style = TextStyle(
                    brush = DegradadoTituloTipo,
                    fontFamily = FuenteMadera,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}