package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material.icons.rounded.CatchingPokemon
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Tipo

private val CafeInsignia = Color(0xFF4A2C14)
private val CremaInsignia = Color(0xFFFFF3DC)

// Tarjeta de madera de un tipo: al presionarla se levanta y brilla con el color del tipo
@Composable
fun TarjetaTipo(
    tipo: Tipo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = colorDeTipo(tipo.nombre)
    val interaccion = remember { MutableInteractionSource() }
    val presionada by interaccion.collectIsPressedAsState()
    val levantar by animateFloatAsState(
        targetValue = if (presionada) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "levantarTarjeta"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            // Brillo suave del color del tipo al presionar
            .drawBehind {
                if (levantar > 0f) {
                    val radio = size.maxDimension * 0.7f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color.copy(alpha = 0.55f * levantar), Color.Transparent),
                            center = center,
                            radius = radio
                        ),
                        radius = radio,
                        center = center
                    )
                }
            }
            // Se levanta: crece un poco y sube
            .graphicsLayer {
                val escala = 1f + 0.06f * levantar
                scaleX = escala
                scaleY = escala
                translationY = -8.dp.toPx() * levantar
            }
            .clickable(interactionSource = interaccion, indication = null, onClick = onClick)
    ) {
        // Marco de madera
        Image(
            painter = painterResource(R.drawable.tarjeta_tipo),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp)
        ) {
            // Fondo de reemplazo (color del tipo + Pokébola) y encima el ícono de la API.
            // Sin internet el ícono no carga y queda el fondo, como en las tarjetas de Pokémon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.verticalGradient(listOf(lerp(color, Color.White, 0.35f), color)))
                    .border(2.dp, lerp(color, Color.Black, 0.35f), RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = Icons.Outlined.CatchingPokemon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.size(40.dp)
                )
                AsyncImage(
                    model = tipo.iconoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(Modifier.height(6.dp))

            // Insignia con la cantidad de Pokémon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(CafeInsignia.copy(alpha = 0.88f))
                    .border(1.dp, color.copy(alpha = 0.8f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CatchingPokemon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${tipo.cantidad}",
                    color = CremaInsignia,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Tablita con el nombre
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.9f)
                .height(34.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.tabla_nombre_tipo),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
            Text(
                text = tipo.nombreEs,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}