package com.tecsup.pokemon.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

// Oculta la barra de estado y la de navegación mientras la pantalla esté visible
@Composable
fun PantallaCompleta() {
    val view = LocalView.current

    DisposableEffect(Unit) {
        val window = view.context.buscarActivity()?.window
        val controlador = window?.let { WindowCompat.getInsetsController(it, view) }

        controlador?.apply {
            // Deslizando desde el borde se muestran un momento y se vuelven a ocultar
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        // Si este composable deja de mostrarse, las barras vuelven a aparecer
        onDispose {
            controlador?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

// Obtiene la Activity desde el Context de Compose
private fun Context.buscarActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.buscarActivity()
    else -> null
}