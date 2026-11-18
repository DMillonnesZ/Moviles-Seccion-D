package com.millones.campusgps.ui.componentes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Colores del vidrio oscuro que flota sobre el mapa (cabecera, botones, panel, menú)
object VidrioMapa {
    val Fondo = Color(0xFF0D1F45).copy(alpha = 0.86f)
    val FondoSolido = Color(0xFF0B1B3D)
    val Cian = Color(0xFF4FC3F7)
    val Estrella = Color(0xFFFFC94D)
    val Borde = Brush.verticalGradient(
        listOf(Color(0xFF5CC8FF).copy(alpha = 0.45f), Color.White.copy(alpha = 0.08f))
    )
}

// Vidrio oscuro con borde celeste fino y sombra
fun Modifier.vidrioMapa(forma: Shape, fondo: Color = VidrioMapa.Fondo): Modifier = this
    .shadow(12.dp, forma, ambientColor = Color.Black, spotColor = Color.Black)
    .clip(forma)
    .background(fondo)
    .border(1.dp, VidrioMapa.Borde, forma)

// Botón cuadrado redondeado de vidrio (menú, ir al más cercano). Con fondo = degradado
// se vuelve el botón azul (ubicarme). Se hunde al presionarlo.
@Composable
fun BotonVidrio(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tamano: Dp = 52.dp,
    forma: Shape = RoundedCornerShape(18.dp),
    fondo: Brush? = null,
    tinte: Color = Color.White,
    habilitado: Boolean = true,
    cargando: Boolean = false
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "escalaBotonVidrio"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .alpha(if (habilitado) 1f else 0.5f)
            .then(
                if (fondo != null) {
                    Modifier
                        .shadow(10.dp, forma, ambientColor = CampusColores.AzulCielo, spotColor = CampusColores.AzulCielo)
                        .clip(forma)
                        .background(fondo)
                        .border(1.dp, Color.White.copy(alpha = 0.35f), forma)
                } else {
                    Modifier.vidrioMapa(forma)
                }
            )
            .clickable(
                interactionSource = interaccion,
                indication = null,
                enabled = habilitado && !cargando,
                onClick = onClick
            )
    ) {
        if (cargando) {
            CircularProgressIndicator(color = tinte, strokeWidth = 2.dp, modifier = Modifier.size(tamano * 0.42f))
        } else {
            Icon(icono, contentDescription = descripcion, tint = tinte, modifier = Modifier.size(tamano * 0.46f))
        }
    }
}
