package com.millones.navlab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.millones.navlab.ui.theme.LavandaClaro
import com.millones.navlab.ui.theme.MoradoOscuro

enum class GradientDirection {
    VERTICAL,
    HORIZONTAL
}

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(MoradoOscuro, LavandaClaro),
    direction: GradientDirection = GradientDirection.VERTICAL,
    content: @Composable BoxScope.() -> Unit
) {
    val brush = when (direction) {
        GradientDirection.VERTICAL -> Brush.verticalGradient(colors)
        GradientDirection.HORIZONTAL -> Brush.horizontalGradient(colors)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush),
        content = content
    )
}