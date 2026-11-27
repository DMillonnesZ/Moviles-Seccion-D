package com.millones.avisoscampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = Color(0xFF001E32),
    background = FondoPantalla,
    onBackground = Color(0xFF1C1B1F),
    surface = FondoPantalla,
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = TextoSecundario,
    outline = Azul,                 // borde de los botones con contorno (Cancelar, Abrir ajustes)
    error = RojoTexto,
    errorContainer = RojoFondo,
    onErrorContainer = RojoTexto
)

@Composable
fun AvisosCampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}