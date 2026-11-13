package com.tecsup.pokemon.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Desvanece suavemente el contenido en los bordes de arriba y/o abajo,
// en lugar de cortarlo en seco (por ejemplo, al pasar por debajo de una cabecera).
// Se aplica al contenedor que tiene el scroll.
fun Modifier.desvanecerBordes(arriba: Dp = 28.dp, abajo: Dp = 0.dp): Modifier = this
    // Capa aparte: necesaria para que el degradado "borre" solo este contenido
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val altoArriba = arriba.toPx()
        val altoAbajo = abajo.toPx()

        // Arriba: de transparente a visible
        if (altoArriba > 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = altoArriba
                ),
                size = Size(size.width, altoArriba),
                blendMode = BlendMode.DstIn
            )
        }
        // Abajo: de visible a transparente
        if (altoAbajo > 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = size.height - altoAbajo,
                    endY = size.height
                ),
                topLeft = Offset(0f, size.height - altoAbajo),
                size = Size(size.width, altoAbajo),
                blendMode = BlendMode.DstIn
            )
        }
    }