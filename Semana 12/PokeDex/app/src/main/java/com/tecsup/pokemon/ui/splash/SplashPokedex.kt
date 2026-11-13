package com.tecsup.pokemon.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.ui.components.AnimacionRaw
import com.tecsup.pokemon.ui.components.AparecerSuave
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.CartelTitulo
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.HaloPulsante
import com.tecsup.pokemon.ui.components.Luciernagas
import com.tecsup.pokemon.ui.components.Pokebola
import com.tecsup.pokemon.ui.components.RayosGiratorios
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Mismo azul noche que el splash del sistema (themes.xml), para que no haya salto
private val NocheSplash = Color(0xFF1B1830)

// Luz cálida de los rayos y el halo al abrir la Pokébola
private val LuzDorada = Color(0xFFFFD97A)

// Fondo nocturno del splash
private val FONDO_SPLASH = R.drawable.fondo_splash

// Splash animado: la Pokébola cae, salta de un lado a otro, vuelve al centro,
// se balancea, hace clic, se abre y sale Pikachu; arriba aparece el cartel.
// Espera a que la sesión esté verificada antes de terminar.
@Composable
fun SplashPokedex(
    sesionLista: Boolean,
    onTerminar: () -> Unit
) {
    val sesionListaActual by rememberUpdatedState(sesionLista)
    val onTerminarActual by rememberUpdatedState(onTerminar)

    val fondo = remember { Animatable(0f) }
    val posX = remember { Animatable(0f) }          // dp: negativo izquierda, positivo derecha
    val posY = remember { Animatable(-600f) }       // dp: negativo arriba, 0 en el piso
    val escalaY = remember { Animatable(1f) }       // < 1 aplastada, > 1 estirada
    val giro = remember { Animatable(0f) }
    val brillo = remember { Animatable(0f) }
    val apertura = remember { Animatable(0f) }
    val destello = remember { Animatable(0f) }
    val pokebolaVisible = remember { Animatable(1f) }
    val pikachu = remember { Animatable(0f) }       // 0 oculto, 1 completo
    val cartel = remember { Animatable(0f) }
    var mostrarTexto by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // 1. Del azul noche al fondo: fundido lento con acercamiento suave
        launch { fondo.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
        delay(350)

        // 2. Cae desde arriba, rebota y se aplasta al tocar el piso
        posY.animateTo(0f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessLow))
        aterrizar(escalaY)
        delay(150)

        // 3. Saltos: izquierda, derecha y vuelta al centro girando
        saltar(posX, posY, escalaY, giro, destinoX = -95f, altura = 110f)
        delay(80)
        saltar(posX, posY, escalaY, giro, destinoX = 105f, altura = 150f)
        delay(80)
        saltar(posX, posY, escalaY, giro, destinoX = 0f, altura = 180f, conVuelta = true)
        delay(200)

        // 4. Tres balanceos, como al capturar un Pokémon
        for (angulo in listOf(-16f, 12f, -6f)) {
            giro.animateTo(angulo, tween(150))
            giro.animateTo(0f, tween(150))
            delay(200)
        }

        // 5. "Clic": el botón brilla
        brillo.animateTo(1f, tween(120))
        brillo.animateTo(0f, tween(260))
        delay(100)

        // 6. Se abre con un destello de luz
        launch { apertura.animateTo(1f, tween(320, easing = FastOutSlowInEasing)) }
        destello.animateTo(1f, tween(280))

        // 7. La Pokébola desaparece y del destello sale Pikachu; luego el cartel
        launch { pokebolaVisible.animateTo(0f, tween(250)) }
        launch { pikachu.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow)) }
        launch {
            delay(250)
            cartel.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        }
        destello.animateTo(0f, tween(700))

        // 8. Texto de carga hasta que la sesión esté lista
        mostrarTexto = true
        snapshotFlow { sesionListaActual }.first { it }
        delay(700)

        // 9. Termina
        onTerminarActual()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NocheSplash)
    ) {
        // Fondo nocturno que aparece con fundido y se asienta con un acercamiento lento
        Image(
            painter = painterResource(FONDO_SPLASH),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = fondo.value
                    val zoom = 1.1f - 0.1f * fondo.value   // de 110 % a 100 %
                    scaleX = zoom
                    scaleY = zoom
                }
        )
        // Velo leve: el fondo ya es oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.12f * fondo.value))
        )

        // Luciérnagas volando por toda la pantalla
        Luciernagas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = fondo.value }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // ----- Cartel "PokeDex" arriba -----
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .graphicsLayer {
                        scaleX = cartel.value
                        scaleY = cartel.value
                        alpha = cartel.value.coerceIn(0f, 1f)
                    }
            ) {
                CartelTitulo()
            }

            // ----- Escena: Pokébola que salta y Pikachu que sale -----
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                // Rayos y luz dorada que salen al abrir
                RayosGiratorios(
                    color = LuzDorada,
                    modifier = Modifier
                        .size(320.dp)
                        .graphicsLayer { alpha = destello.value }
                )
                HaloPulsante(
                    color = LuzDorada,
                    modifier = Modifier
                        .size(260.dp)
                        .graphicsLayer { alpha = destello.value }
                )

                // Sombra en el piso: de la Pokébola y luego de Pikachu
                Canvas(
                    modifier = Modifier
                        .offset(x = posX.value.dp, y = 70.dp)
                        .size(width = 120.dp, height = 18.dp)
                        .graphicsLayer {
                            val altura = (-posY.value / 200f).coerceIn(0f, 1f)
                            val sombraPokebola = (1f - altura * 0.7f) * pokebolaVisible.value
                            val tamano = maxOf(1f - altura * 0.5f, pikachu.value)
                            scaleX = tamano
                            scaleY = tamano
                            alpha = maxOf(sombraPokebola, pikachu.value.coerceIn(0f, 1f)) * fondo.value
                        }
                ) {
                    drawOval(Color.Black.copy(alpha = 0.45f))
                }

                // Pokébola: salta, se aplasta, se balancea y se desvanece creciendo un poco
                Pokebola(
                    apertura = apertura.value,
                    brillo = brillo.value,
                    modifier = Modifier
                        .offset(x = posX.value.dp, y = posY.value.dp)
                        .size(140.dp)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 1f)   // se aplasta contra el piso
                            rotationZ = giro.value
                            alpha = pokebolaVisible.value
                            val crecer = 1f + (1f - pokebolaVisible.value) * 0.4f
                            // Al aplastarse se ensancha y al estirarse se adelgaza
                            scaleY = escalaY.value * crecer
                            scaleX = (2f - escalaY.value) * crecer
                        }
                )

                // Pikachu sale del destello, creciendo desde el piso con rebote
                AnimacionRaw(
                    recurso = R.raw.pikachu_splash,
                    descripcion = "Pikachu",
                    modifier = Modifier
                        .offset(y = (-20).dp)   // apoya los pies en el piso de la Pokébola
                        .size(200.dp)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 1f)
                            val escala = 0.3f + 0.7f * pikachu.value
                            scaleX = escala
                            scaleY = escala
                            alpha = pikachu.value.coerceIn(0f, 1f)
                        }
                )

                // Destello cálido al abrirse (encima de todo)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (destello.value > 0f) {
                        val radio = size.minDimension * 0.6f * destello.value + 1f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFF4D6).copy(alpha = destello.value), Color.Transparent),
                                center = center,
                                radius = radio
                            ),
                            radius = radio,
                            center = center
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ----- Texto de carga que titila -----
            Box(modifier = Modifier.height(48.dp)) {
                if (mostrarTexto) {
                    AparecerSuave {
                        TextoCargando()
                    }
                }
            }
        }
    }
}

// Un salto en arco: se aplasta, despega estirada, vuela hasta destinoX y aterriza aplastándose
private suspend fun saltar(
    posX: Animatable<Float, AnimationVector1D>,
    posY: Animatable<Float, AnimationVector1D>,
    escalaY: Animatable<Float, AnimationVector1D>,
    giro: Animatable<Float, AnimationVector1D>,
    destinoX: Float,
    altura: Float,
    conVuelta: Boolean = false
) = coroutineScope {
    // Toma impulso
    escalaY.animateTo(0.82f, tween(80))

    // Despega estirada y avanza en horizontal durante todo el salto
    launch { escalaY.animateTo(1.12f, tween(120)) }
    launch { posX.animateTo(destinoX, tween(480, easing = FastOutSlowInEasing)) }
    if (conVuelta) {
        launch {
            giro.animateTo(360f, tween(480, easing = FastOutSlowInEasing))
            giro.snapTo(0f)
        }
    }

    // Sube y baja (arco)
    posY.animateTo(-altura, tween(240, easing = LinearOutSlowInEasing))
    launch { escalaY.animateTo(1f, tween(120)) }
    posY.animateTo(0f, tween(240, easing = FastOutLinearInEasing))

    // Aterriza
    aterrizar(escalaY)
}

// Se aplasta al tocar el piso y recupera su forma con rebote
private suspend fun aterrizar(escalaY: Animatable<Float, AnimationVector1D>) {
    escalaY.animateTo(0.78f, tween(70))
    escalaY.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMedium))
}

@Composable
private fun TextoCargando() {
    val transicion = rememberInfiniteTransition(label = "cargando")
    val opacidad by transicion.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "opacidadCargando"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(260.dp)
            .height(48.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_enlace),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Text(
            text = "Cargando tu Pokédex...",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 17.sp,
            modifier = Modifier.graphicsLayer { alpha = opacidad }
        )
    }
}