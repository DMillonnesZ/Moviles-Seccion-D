package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DoradoClaroBoton = Color(0xFFFFE7A0)
private val DoradoBoton = Color(0xFFF5C451)

// Botón redondo dorado reutilizable: volver, avanzar, favorito...
// - icono: por defecto la flecha de volver (para avanzar: Icons.AutoMirrored.Rounded.ArrowForward)
// - tinte: color del ícono
// - latido: late cuando cambia a true (por ejemplo, al marcar favorito)
// Se hunde un poco al presionarlo.
@Composable
fun BotonVolver(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
    tinte: Color = CafeMadera,
    descripcion: String = "Volver",
    tamano: Dp = 40.dp,
    latido: Boolean = false
) {
    val escala = remember { Animatable(1f) }
    var primeraVez by remember { mutableStateOf(true) }
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val hundir by animateFloatAsState(
        targetValue = if (presionado) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "hundirBoton"
    )

    // Latido al cambiar a true
    LaunchedEffect(latido) {
        if (primeraVez) {
            primeraVez = false
        } else if (latido) {
            escala.animateTo(1.25f, tween(110))
            escala.animateTo(1f, spring(dampingRatio = 0.35f))
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano)
            .graphicsLayer {
                val e = escala.value * hundir
                scaleX = e
                scaleY = e
            }
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(DoradoClaroBoton, DoradoBoton)))
            .border(1.5.dp, CafeMadera.copy(alpha = 0.6f), CircleShape)
            .clickable(interactionSource = interaccion, indication = null, onClick = onClick)
    ) {
        // Brillo superior
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOval(
                color = Color.White.copy(alpha = 0.55f),
                topLeft = Offset(size.width * 0.25f, size.height * 0.1f),
                size = Size(size.width * 0.5f, size.height * 0.2f)
            )
        }
        Icon(
            imageVector = icono,
            contentDescription = descripcion,
            tint = tinte,
            modifier = Modifier.size(tamano * 0.55f)
        )
    }
}