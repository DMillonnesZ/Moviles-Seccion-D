package com.millones.campusgps.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import com.millones.campusgps.R

// Guarda dónde está el fondo en pantalla para que la tarjeta de vidrio
// dibuje detrás de ella la misma parte del fondo, pero desenfocada
class EstadoFondoAuth {
    var coordenadas: LayoutCoordinates? by mutableStateOf(null)
}

val LocalFondoAuth = staticCompositionLocalOf<EstadoFondoAuth?> { null }

// Ilustración nocturna del campus (con la ruta ya dibujada) + velo azul:
// más oscuro arriba para que se lea el logo y casi nada en el medio
@Composable
fun CapaFondoAuth(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.login_fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to CampusColores.AzulNoche.copy(alpha = 0.55f),
                        0.35f to CampusColores.AzulNoche.copy(alpha = 0.15f),
                        0.75f to CampusColores.AzulNoche.copy(alpha = 0.10f),
                        1f to CampusColores.AzulNoche.copy(alpha = 0.45f)
                    )
                )
        )
    }
}

// Fondo de las pantallas de acceso
@Composable
fun FondoAuth(content: @Composable () -> Unit) {
    val estado = remember { EstadoFondoAuth() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { estado.coordenadas = it }
    ) {
        CapaFondoAuth(Modifier.fillMaxSize())
        CompositionLocalProvider(LocalFondoAuth provides estado) {
            content()
        }
    }
}
