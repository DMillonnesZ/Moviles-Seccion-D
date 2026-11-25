package com.millones.murocampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaOscuro = darkColorScheme(
    primary = AzulBoton1,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = Color.White,
    secondary = Menta,
    onSecondary = Color(0xFF00382A),
    background = FondoMedio,
    onBackground = Color.White,
    surface = Superficie,
    onSurface = Color.White,
    surfaceVariant = SuperficieCampo,
    onSurfaceVariant = TextoSecundario,
    outline = BordeCampo,
    outlineVariant = BordeTarjeta,
    error = RojoTexto,
    onError = Color.White,
    errorContainer = RojoFondo,
    onErrorContainer = Color(0xFFFFB4BC)
)

@Composable
fun MuroCampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaOscuro,
        typography = Typography,
        content = content
    )
}