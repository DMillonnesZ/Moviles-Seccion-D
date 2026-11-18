package com.millones.campusgps.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.campusgps.ui.componentes.DegradadosCampus
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.LogoGps
import com.millones.campusgps.ui.componentes.NombreCampus
import com.millones.campusgps.ui.componentes.ConRutaViva
import com.millones.campusgps.ui.componentes.marcarDestino
import com.millones.campusgps.ui.componentes.marcarZonaLibre
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Duración total del splash antes de pasar al login o al mapa
private const val DURACION_SPLASH_MS = 2300L

// Splash animado (SplashPokedex de la PokeDex): el pin cae desde arriba y rebota,
// después aparecen "CampusGPS" y la frase, con la ruta viva de fondo.
// La sesión de Firebase se conoce al instante (currentUser), así que solo se espera la animación.
@Composable
fun PantallaSplash(onTerminar: () -> Unit) {
    val caida = remember { Animatable(-1f) }   // -1 = fuera de la pantalla arriba, 0 = en su lugar
    val onTerminarActual by rememberUpdatedState(onTerminar)

    LaunchedEffect(Unit) {
        launch {
            delay(150)
            caida.animateTo(0f, spring(dampingRatio = 0.42f, stiffness = Spring.StiffnessLow))
        }
        delay(DURACION_SPLASH_MS)
        onTerminarActual()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DegradadosCampus.Noche)
    ) {
        // Ruta rápida: llega al pin justo antes de pasar al login
        ConRutaViva(duracionMs = 1500) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            // Zona libre: la ruta rodea el logo y el nombre
            modifier = Modifier.marcarZonaLibre()
        ) {
            // El destino se marca en la caja quieta; el pin cae dentro de ella
            Box(modifier = Modifier.marcarDestino()) {
                Box(
                    modifier = Modifier.graphicsLayer {
                        translationY = caida.value * 420.dp.toPx()
                    }
                ) {
                    LogoGps(tamano = 130.dp)
                }
            }

            Spacer(Modifier.height(10.dp))

            EntradaSuave(retrasoMs = 700L) {
                NombreCampus(fontSize = 44.sp)
            }

            Spacer(Modifier.height(6.dp))

            EntradaSuave(retrasoMs = 900L) {
                Text(
                    text = "Encuentra tu camino en el campus",
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = FuenteCampus,
                    fontSize = 15.sp,
                    letterSpacing = 0.2.sp
                )
            }
        }
        }
        }
    }
}
