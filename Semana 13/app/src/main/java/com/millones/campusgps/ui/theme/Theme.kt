package com.millones.campusgps.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.millones.campusgps.ui.componentes.CampusColores

// Esquema fijo con la paleta de CampusGPS.
// Sin color dinámico: en Android 12+ el color del fondo de pantalla cambiaría toda la app.
private val EsquemaCampus = lightColorScheme(
    primary = CampusColores.AzulPrimario,
    onPrimary = CampusColores.Blanco,
    secondary = CampusColores.AzulCielo,
    onSecondary = CampusColores.Blanco,
    tertiary = CampusColores.VerdeMenta,
    onTertiary = CampusColores.TextoOscuro,
    background = CampusColores.Superficie,
    onBackground = CampusColores.TextoOscuro,
    surface = CampusColores.Superficie,
    onSurface = CampusColores.TextoOscuro,
    surfaceVariant = CampusColores.CelesteHielo,
    onSurfaceVariant = CampusColores.TextoSecundario,
    outline = CampusColores.Borde,
    error = CampusColores.Error,
    onError = CampusColores.Blanco
)

@Composable
fun CampusGPSTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaCampus,
        typography = Typography,
        content = content
    )
}
