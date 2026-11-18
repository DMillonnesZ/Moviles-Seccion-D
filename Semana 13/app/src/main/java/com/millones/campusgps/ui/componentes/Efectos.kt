package com.millones.campusgps.ui.componentes

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

// Desvanece el contenido en los bordes de arriba y/o abajo en lugar de cortarlo en seco.
// Se aplica al contenedor que tiene el scroll (LazyColumn, Column con verticalScroll).
fun Modifier.desvanecerBordes(arriba: Dp = 24.dp, abajo: Dp = 0.dp): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val altoArriba = arriba.toPx()
        val altoAbajo = abajo.toPx()
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

// Oculta la barra de estado y la de navegación del sistema mientras se muestre.
// Deslizando desde el borde aparecen un momento y se vuelven a ocultar.
@Composable
fun PantallaCompleta() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = view.context.buscarActivity()?.window
        val controlador = window?.let { WindowCompat.getInsetsController(it, view) }
        controlador?.apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            controlador?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

private fun Context.buscarActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.buscarActivity()
    else -> null
}
