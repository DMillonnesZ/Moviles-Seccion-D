package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FondoBarra = Color(0xFFECE7E1)

// Fila de estadística: ícono en círculo, nombre, valor, barra gruesa animada y línea divisoria
@Composable
fun BarraEstadistica(
    icono: ImageVector,
    nombre: String,
    valor: Int,
    color: Color,
    animar: Boolean,
    retrasoMs: Int = 0,
    mostrarDivisor: Boolean = true,
    modifier: Modifier = Modifier
) {
    // 255 es el valor máximo de una estadística base
    val progreso by animateFloatAsState(
        targetValue = if (animar) (valor / 255f).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 900, delayMillis = retrasoMs, easing = FastOutSlowInEasing),
        label = "progresoEstadistica"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            // Ícono en círculo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(CafeMadera.copy(alpha = 0.08f))
            ) {
                Icon(icono, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = nombre,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 14.sp,
                modifier = Modifier.width(78.dp)
            )
            Text(
                text = "$valor",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.width(34.dp)
            )
            Spacer(Modifier.width(12.dp))
            // Barra gruesa
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(FondoBarra)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progreso)
                        .clip(RoundedCornerShape(50))
                        // Color del tipo vivo, aclarándose hacia la punta (sin negro)
                        .background(
                            Brush.horizontalGradient(
                                listOf(color, lerp(color, Color.White, 0.3f))
                            )
                        )
                        // Brillo arriba
                        .drawWithContent {
                            drawContent()
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.4f),
                                topLeft = Offset(size.height / 2f, size.height * 0.18f),
                                size = Size((size.width - size.height).coerceAtLeast(0f), size.height * 0.22f),
                                cornerRadius = CornerRadius(size.height)
                            )
                        }
                )
            }
        }
        if (mostrarDivisor) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CafeMadera.copy(alpha = 0.08f))
            )
        }
    }
}