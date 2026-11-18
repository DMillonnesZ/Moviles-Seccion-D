package com.millones.campusgps.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.campusgps.R
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.brilloRecorrido
import com.millones.campusgps.ui.componentes.brushBicolor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Duración total del splash antes de pasar al login o al mapa
private const val DURACION_SPLASH_MS = 2700L

// Colores del texto sobre el fondo claro del splash
private val AzulTinta = Color(0xFF0B1F4D)
private val AzulTintaSuave = Color(0xFF1C3A7A)
private val AzulBrillante = Color(0xFF2F80FF)
private val FondoSplash = Color(0xFFEAF2FD)

// Splash: la imagen de fondo aparece alejándose suavemente; luego "CampusGPS" entra
// letra por letra con rebote, las líneas se abren desde la frase y la frase aparece.
// La sesión de Firebase se conoce al instante (currentUser), así que solo se espera la animación.
@Composable
fun PantallaSplash(onTerminar: () -> Unit) {
    val fondo = remember { Animatable(0f) }
    val lineas = remember { Animatable(0f) }
    val onTerminarActual by rememberUpdatedState(onTerminar)

    LaunchedEffect(Unit) {
        launch { fondo.animateTo(1f, tween(1200, easing = FastOutSlowInEasing)) }
        launch {
            delay(1250)
            lineas.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        }
        delay(DURACION_SPLASH_MS)
        onTerminarActual()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoSplash)
    ) {
        // Fondo: aparece y se aleja un poco (de 108 % a 100 %)
        Image(
            painter = painterResource(R.drawable.splash_fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = fondo.value
                    val escala = 1.08f - 0.08f * fondo.value
                    scaleX = escala
                    scaleY = escala
                }
        )

        // Texto en la franja libre del medio de la imagen
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 20.dp)
        ) {
            TituloLetraPorLetra(retrasoMs = 450L)

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LineaLateral(progreso = lineas.value)
                Spacer(Modifier.width(10.dp))
                EntradaSuave(retrasoMs = 1350L) {
                    Text(
                        text = "Encuentra tu camino en el campus",
                        color = AzulTinta,
                        fontFamily = FuenteCampus,
                        fontSize = 15.sp,
                        letterSpacing = 0.2.sp
                    )
                }
                Spacer(Modifier.width(10.dp))
                LineaLateral(progreso = lineas.value)
            }
        }
    }
}

// "CampusGPS": cada letra sube, crece y aparece con rebote, una detrás de otra.
// "Campus" en azul tinta y "GPS" en azul brillante, con un brillo suave que recorre todo.
@Composable
private fun TituloLetraPorLetra(retrasoMs: Long) {
    val texto = "CampusGPS"
    val largoCampus = "Campus".length
    val animaciones = remember { List(texto.length) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        delay(retrasoMs)
        animaciones.forEachIndexed { i, animacion ->
            launch {
                delay(i * 65L)
                animacion.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)
                )
            }
        }
    }

    val estiloCampus = TextStyle(
        brush = brushBicolor(AzulTinta, AzulTintaSuave),
        fontFamily = FuenteCampus,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 50.sp
    )
    val estiloGps = TextStyle(
        brush = brushBicolor(AzulBrillante, CampusColores.AzulPrimario),
        fontFamily = FuenteCampus,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 50.sp
    )

    Row(modifier = Modifier.brilloRecorrido(intensidad = 0.45f, duracionMs = 3000)) {
        texto.forEachIndexed { i, letra ->
            Text(
                text = letra.toString(),
                style = if (i < largoCampus) estiloCampus else estiloGps,
                modifier = Modifier.graphicsLayer {
                    val v = animaciones[i].value
                    alpha = v.coerceIn(0f, 1f)
                    translationY = (1f - v) * 24.dp.toPx()
                    val escala = 0.6f + 0.4f * v
                    scaleX = escala
                    scaleY = escala
                }
            )
        }
    }
}

// Línea fina a cada lado de la frase: se abre desde 0 hasta 36 dp
@Composable
private fun LineaLateral(progreso: Float) {
    Box(
        modifier = Modifier
            .width(36.dp * progreso)
            .height(1.5.dp)
            .background(AzulTinta.copy(alpha = 0.7f))
    )
}
