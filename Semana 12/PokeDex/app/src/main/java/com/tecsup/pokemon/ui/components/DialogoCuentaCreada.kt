package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R
import kotlinx.coroutines.delay

// Tamaño de la tarjeta en pantalla
private val ANCHO_TARJETA = 370.dp

// Posiciones dentro de la imagen, en fracciones de su alto/ancho (ajustables)
private const val POKEBOLA_X = 0.50f
private const val POKEBOLA_Y = 0.17f
private const val TITULO_Y = 0.34f       // borde superior del área del título
private const val TITULO_ALTO = 0.29f
private const val MENSAJE_Y = 0.61f
private const val MENSAJE_ALTO = 0.16f
private const val MENSAJE_MARGEN = 0.26f // margen lateral para no pisar las hojas
private const val BOTON_Y = 0.76f
private const val BOTON_ALTO = 0.17f

private val DegradadoCafe = Brush.verticalGradient(
    listOf(Color(0xFFAD671C), Color(0xFF693106), Color(0xFF692B04))
)

// Celebración al crear la cuenta: entra con rebote y sale encogiéndose al tocar "¡Perfecto!"
@Composable
fun DialogoCuentaCreada(onPerfecto: () -> Unit) {
    var saliendo by remember { mutableStateOf(false) }

    // Entrada con efecto resorte
    val entrada = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entrada.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
    }

    // Salida: se encoge y desvanece, luego avisa para ir al login
    val salida by animateFloatAsState(
        targetValue = if (saliendo) 0f else 1f,
        animationSpec = tween(350),
        label = "salida"
    )
    LaunchedEffect(saliendo) {
        if (saliendo) {
            delay(380)
            onPerfecto()
        }
    }

    val visibilidad = entrada.value.coerceIn(0f, 1f) * salida

    // Fondo oscurecido que bloquea los toques a la pantalla de atrás
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f * visibilidad))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { }
    ) {
        val imagen = painterResource(R.drawable.celebracion_cuenta)
        val proporcion = imagen.intrinsicSize.width / imagen.intrinsicSize.height

        Box(
            modifier = Modifier
                .width(ANCHO_TARJETA)
                .aspectRatio(proporcion)
                .graphicsLayer {
                    val escala = entrada.value * (0.85f + 0.15f * salida)
                    scaleX = escala
                    scaleY = escala
                    alpha = visibilidad
                }
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val w = maxWidth
                val h = maxHeight

                // Luz y rayos detrás de la Pokébola
                Box(
                    modifier = Modifier
                        .offset(x = w * POKEBOLA_X - 140.dp, y = h * POKEBOLA_Y - 140.dp)
                        .size(280.dp)
                ) {
                    RayosGiratorios(modifier = Modifier.fillMaxSize())
                    HaloPulsante(
                        modifier = Modifier
                            .fillMaxSize(0.75f)
                            .align(Alignment.Center)
                    )
                }

                // Tarjeta con los Pokémon
                Image(
                    painter = imagen,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // "¡Cuenta creada!" en arco, dentro de la tabla
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = h * TITULO_Y)
                        .height(h * TITULO_ALTO)
                ) {
                    val estiloTitulo = TextStyle(
                        brush = DegradadoCafe,
                        fontFamily = FuenteMadera,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextoCurvo(texto = "¡Cuenta", estilo = estiloTitulo, retrasoMs = 350)
                    // Sube la segunda línea para juntarla con la primera
                    Box(modifier = Modifier.offset(y = (-14).dp)) {
                        TextoCurvo(texto = "creada!", estilo = estiloTitulo, retrasoMs = 350 + 7 * 55)
                    }
                }

                // Mensaje en el espacio blanco, sin pisar las hojas de los lados
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = h * MENSAJE_Y)
                        .height(h * MENSAJE_ALTO)
                        .padding(horizontal = w * MENSAJE_MARGEN)
                ) {
                    AparecerSuave(retrasoMs = 1300) {
                        Text(
                            text = "¡Ahora estás listo para explorar el mundo Pokémon!",
                            color = Color(0xFF4A3420),
                            fontFamily = FuenteMadera,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Botón "¡Perfecto!"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = h * BOTON_Y)
                        .height(h * BOTON_ALTO)
                ) {
                    AparecerSuave(retrasoMs = 1600) {
                        BotonVerde(
                            texto = "¡Perfecto!",
                            onClick = { if (!saliendo) saliendo = true },
                            modifier = Modifier
                                .width(w * 0.55f)
                                .height(52.dp)
                        )
                    }
                }

                // Brillos alrededor de la Pokébola y la tarjeta
                Destellos(
                    posiciones = listOf(
                        Offset(0.33f, 0.04f),
                        Offset(0.67f, 0.05f),
                        Offset(0.50f, -0.03f),
                        Offset(0.12f, 0.33f),
                        Offset(0.90f, 0.31f)
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Confeti sobre todo
        Confeti(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = salida }
        )
    }
}

// Botón verde: se achica al presionarlo y la flecha se mueve invitando a tocar
@Composable
private fun BotonVerde(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "escalaBoton"
    )

    val transicion = rememberInfiniteTransition(label = "flecha")
    val desplazamiento by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "desplazamientoFlecha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .clickable(interactionSource = interaccion, indication = null, onClick = onClick)
    ) {
        // El PNG debe estar recortado al borde de la píldora (sin márgenes transparentes)
        Image(
            painter = painterResource(R.drawable.boton_verde),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = texto,
                style = TextStyle(
                    color = Color.White,
                    fontFamily = FuenteMadera,
                    fontSize = 18.sp,
                    shadow = Shadow(Color(0x66000000), Offset(0f, 2f), 3f)
                )
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = desplazamiento.dp)
            )
        }
    }
}