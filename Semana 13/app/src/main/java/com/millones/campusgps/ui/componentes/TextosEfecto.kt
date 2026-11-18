package com.millones.campusgps.ui.componentes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit

// Espacio seguro de todas las pantallas: barras del sistema + cámara (display cutout).
// Con la barra de estado oculta, statusBarsPadding vale 0 y el contenido quedaría bajo la cámara;
// el cutout sigue midiendo ese espacio.
val InsetsCampus: WindowInsets
    @Composable get() = WindowInsets.systemBars.union(WindowInsets.displayCutout)

// Degradado suave de arriba hacia abajo en las letras: la parte de arriba queda del primer
// color y desde la mitad pasa poco a poco al segundo (sin corte brusco)
fun brushBicolor(arriba: Color, abajo: Color): Brush =
    Brush.verticalGradient(
        0f to arriba,
        0.45f to arriba,
        1f to abajo
    )

// Brillo que recorre las letras de izquierda a derecha sin parar.
// SrcAtop pinta el brillo solo donde hay letras.
@Composable
fun Modifier.brilloRecorrido(
    color: Color = Color.White,
    duracionMs: Int = 3600,     // más lento: se nota menos
    intensidad: Float = 0.3f   // 0 = sin brillo, 1 = brillo fuerte
): Modifier {
    val transicion = rememberInfiniteTransition(label = "brillo")
    val avance by transicion.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(duracionMs, easing = LinearEasing)),
        label = "avanceBrillo"
    )
    return this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val x = size.width * avance
            val ancho = size.width * 0.22f
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, color.copy(alpha = intensidad), Color.Transparent),
                    start = Offset(x - ancho, 0f),
                    end = Offset(x + ancho, size.height)
                ),
                blendMode = BlendMode.SrcAtop
            )
        }
}

// Título con color partido, resplandor difuminado detrás y brillo opcional
@Composable
fun TextoBicolor(
    texto: String,
    arriba: Color,
    abajo: Color,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    resplandor: Color? = null,
    brillo: Boolean = false
) {
    Text(
        text = texto,
        style = TextStyle(
            brush = brushBicolor(arriba, abajo),
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            shadow = resplandor?.let { Shadow(color = it, offset = Offset.Zero, blurRadius = 24f) }
        ),
        modifier = if (brillo) modifier.brilloRecorrido() else modifier
    )
}
