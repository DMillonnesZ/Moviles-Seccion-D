package com.tecsup.huellitasvet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.huellitasvet.model.Rol

private val DuenoClaro = lightColorScheme(
    primary = DuenoPrimario,
    onPrimary = Color.White,
    primaryContainer = DuenoContenedor,
    onPrimaryContainer = Color(0xFF064E3B),
    background = DuenoFondo,
    surface = Color.White
)

private val DuenoOscuro = darkColorScheme(
    primary = DuenoPrimarioOscuro,
    onPrimary = Color(0xFF022C22),
    primaryContainer = DuenoContenedorOscuro,
    onPrimaryContainer = DuenoContenedor,
    background = FondoOscuro,
    surface = SuperficieOscura
)

private val VetClaro = lightColorScheme(
    primary = VetPrimario,
    onPrimary = Color.White,
    primaryContainer = VetContenedor,
    onPrimaryContainer = Color(0xFF1E3A8A),
    background = VetFondo,
    surface = Color.White
)

private val VetOscuro = darkColorScheme(
    primary = VetPrimarioOscuro,
    onPrimary = Color(0xFF0B1A3D),
    primaryContainer = VetContenedorOscuro,
    onPrimaryContainer = VetContenedor,
    background = FondoOscuro,
    surface = SuperficieOscura
)

@Composable
fun VetTheme(
    rol: Rol = Rol.CLIENTE,
    oscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquema = when (rol) {
        Rol.CLIENTE -> if (oscuro) DuenoOscuro else DuenoClaro
        Rol.ADMIN -> if (oscuro) VetOscuro else VetClaro
    }
    MaterialTheme(colorScheme = esquema, typography = Typography, content = content)
}