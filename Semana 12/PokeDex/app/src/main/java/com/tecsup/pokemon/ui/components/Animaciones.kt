package com.tecsup.pokemon.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// Muestra el contenido con fundido y subiendo un poco, después de un retraso
@Composable
fun AparecerSuave(
    retrasoMs: Int = 0,
    duracionMs: Int = 600,
    contenido: @Composable () -> Unit
) {
    // Empieza oculto y se pide mostrarlo una sola vez (no se repite al recomponer)
    val visible = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    AnimatedVisibility(
        visibleState = visible,
        enter = fadeIn(tween(durationMillis = duracionMs, delayMillis = retrasoMs)) +
                slideInVertically(
                    animationSpec = tween(
                        durationMillis = duracionMs,
                        delayMillis = retrasoMs,
                        easing = FastOutSlowInEasing
                    ),
                    initialOffsetY = { altura -> altura / 3 }   // sube desde un tercio de su alto
                )
    ) {
        contenido()
    }
}