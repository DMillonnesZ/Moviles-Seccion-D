package com.tecsup.pokemon.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

// Dibuja el contenido en blanco y negro (Pokémon descontinuados).
// La capa es un poco más grande para no cortar lo que sobresale de la tarjeta.
fun Modifier.escalaGrises(): Modifier = this.drawWithCache {
    val pintura = Paint().apply {
        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    }
    val zona = Rect(
        left = -size.width * 0.2f,
        top = -size.height * 0.4f,
        right = size.width * 1.2f,
        bottom = size.height * 1.2f
    )
    onDrawWithContent {
        drawIntoCanvas { lienzo ->
            lienzo.saveLayer(zona, pintura)
            drawContent()
            lienzo.restore()
        }
    }
}