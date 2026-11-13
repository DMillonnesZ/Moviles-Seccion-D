package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.EtapaEvolucion
import kotlinx.coroutines.delay

private val GrisNumeroEvo = Color(0xFF5E4E3E)
private val DoradoInsignia = Color(0xFFF5C451)
private val DoradoClaroEvo = Color(0xFFFFE7A0)
private val DoradoBordeEvo = Color(0xFFD9A43A)
private val CremaEvo = Color(0xFFFFF4DF)
private val ColoresAnillo = listOf(
    Color(0xFFFFD54F), Color(0xFFFF6FB5), Color(0xFF7C4DFF),
    Color(0xFF40C4FF), Color(0xFF69F0AE), Color(0xFFFFD54F)
)

// Medidas de la cadena (pensadas para que 3 etapas entren en una pantalla de ~360 dp)
private val ANCHO_MEDALLON = 80.dp
private val ANCHO_CONECTOR = 44.dp
private val ANCHO_PILA = 96.dp
private val TAMANO_ACTUAL = 68.dp
private val TAMANO_NORMAL = 54.dp
private val TAMANO_PILA = 58.dp

// Halo blanco detrás del texto para que se lea sobre el paisaje
private val HaloTexto = Shadow(color = Color.White, offset = Offset.Zero, blurRadius = 10f)

// Cadena evolutiva sin marco: medallones flotando sobre el fondo, unidos por líneas punteadas.
// El Pokémon actual se distingue por su anillo que gira y su tamaño.
// Si una etapa tiene varias evoluciones (ej. Gloom → Vileplume / Bellossom), se apilan como cartas
// y al tocarlas se abre una ventana para elegir cuál ver.
@Composable
fun CadenaEvolucion(
    etapas: List<EtapaEvolucion>,
    idActual: Int,
    color: Color,
    onEtapaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val fases = etapas.groupBy { it.fase }.toSortedMap().values.toList()

    // Animaciones compartidas: anillo que gira, brillo que late y puntos que fluyen
    val transicion = rememberInfiniteTransition(label = "evolucion")
    val giro by transicion.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "giroAnillo"
    )
    val pulso by transicion.animateFloat(
        initialValue = 0.35f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "pulsoEtapa"
    )
    val flujo by transicion.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "flujoLinea"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            fases.forEachIndexed { indice, grupo ->
                if (indice > 0) {
                    Conector(
                        condicion = if (grupo.size > 1) "" else grupo.first().condicion,
                        flujo = flujo
                    )
                }
                if (grupo.size == 1) {
                    val etapa = grupo.first()
                    Medallon(
                        etapa = etapa,
                        esActual = etapa.id == idActual,
                        color = color,
                        giro = giro,
                        pulso = pulso,
                        onClick = { if (etapa.id != idActual) onEtapaClick(etapa.id) }
                    )
                } else {
                    GrupoRamas(
                        etapas = grupo,
                        idActual = idActual,
                        color = color,
                        giro = giro,
                        pulso = pulso,
                        onEtapaClick = onEtapaClick
                    )
                }
            }
        }
    }
}

// Varias evoluciones en la misma etapa: apiladas como cartas; al tocarlas se abre la ventana
@Composable
private fun GrupoRamas(
    etapas: List<EtapaEvolucion>,
    idActual: Int,
    color: Color,
    giro: Float,
    pulso: Float,
    onEtapaClick: (Int) -> Unit
) {
    var ventanaAbierta by remember { mutableStateOf(false) }
    // La etapa actual (si está en el grupo) va arriba de la pila
    val ordenadas = etapas.sortedBy { if (it.id == idActual) 0 else 1 }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(ANCHO_PILA)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { ventanaAbierta = true }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(width = ANCHO_PILA, height = 84.dp)
        ) {
            // De atrás hacia adelante: cada una un poco desplazada e inclinada
            val visibles = ordenadas.take(3).reversed()
            visibles.forEachIndexed { i, etapa ->
                val posicion = visibles.size - 1 - i   // 0 = la de adelante
                CirculoEtapa(
                    etapa = etapa,
                    tamano = TAMANO_PILA,
                    color = color,
                    esActual = etapa.id == idActual && posicion == 0,
                    giro = giro,
                    pulso = pulso,
                    modifier = Modifier
                        .zIndex(-posicion.toFloat())
                        .offset(x = (posicion * 12).dp - 6.dp, y = (-posicion * 4).dp)
                        .graphicsLayer { rotationZ = posicion * 8f }
                )
            }
            // Insignia con la cantidad
            Text(
                text = "×${etapas.size}",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 2.dp)
                    .zIndex(5f)
                    .clip(CircleShape)
                    .background(DoradoInsignia)
                    .border(1.dp, CafeMadera.copy(alpha = 0.4f), CircleShape)
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            )
        }
        Text(
            text = "${etapas.size} evoluciones",
            style = TextStyle(shadow = HaloTexto),
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1
        )
        Text(
            text = "Toca para ver",
            style = TextStyle(shadow = HaloTexto),
            color = GrisNumeroEvo,
            fontFamily = FuenteMadera,
            fontSize = 10.sp
        )
    }

    if (ventanaAbierta) {
        VentanaRamas(
            etapas = ordenadas,
            idActual = idActual,
            color = color,
            onElegir = { id ->
                ventanaAbierta = false
                onEtapaClick(id)
            },
            onCerrar = { ventanaAbierta = false }
        )
    }
}

// Ventana que tapa la pantalla con una tarjeta grande por cada evolución, para elegir cuál ver
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VentanaRamas(
    etapas: List<EtapaEvolucion>,
    idActual: Int,
    color: Color,
    onElegir: (Int) -> Unit,
    onCerrar: () -> Unit
) {
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCerrar
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "Elige una evolución",
                    color = Color.White,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                // Espacio extra arriba para la tarjeta alzada
                Spacer(Modifier.height(30.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    maxItemsInEachRow = 2
                ) {
                    etapas.forEachIndexed { indice, etapa ->
                        TarjetaRama(
                            etapa = etapa,
                            esActual = etapa.id == idActual,
                            color = color,
                            retrasoMs = indice * 90L,
                            onClick = {
                                if (etapa.id == idActual) onCerrar() else onElegir(etapa.id)
                            }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Toca fuera para cerrar",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FuenteMadera,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Tarjeta grande de una evolución: entra girando como una carta que se da vuelta.
// La del Pokémon actual sobresale: alzada, más grande, con sombra del color del tipo y flotando.
@Composable
private fun TarjetaRama(
    etapa: EtapaEvolucion,
    esActual: Boolean,
    color: Color,
    retrasoMs: Long,
    onClick: () -> Unit
) {
    val entrada = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(retrasoMs)
        entrada.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow))
    }

    // Flotación suave solo para la actual
    val transicion = rememberInfiniteTransition(label = "flotarRama")
    val flotar by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "flotar"
    )

    val forma = RoundedCornerShape(20.dp)
    val alzado = if (esActual) 14.dp + 4.dp * flotar else 0.dp
    val escalaBase = if (esActual) 1.06f else 1f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(150.dp)
            .offset(y = -alzado)
            .graphicsLayer {
                rotationY = (1f - entrada.value) * 90f
                val escala = (0.7f + 0.3f * entrada.value) * escalaBase
                scaleX = escala
                scaleY = escala
                alpha = entrada.value.coerceIn(0f, 1f)
                cameraDistance = 12f * density
            }
            .shadow(
                elevation = if (esActual) 22.dp else 6.dp,
                shape = forma,
                ambientColor = if (esActual) color else Color.Black,
                spotColor = if (esActual) color else Color.Black
            )
            .clip(forma)
            .background(Brush.verticalGradient(listOf(Color.White, CremaEvo)))
            .border(
                width = if (esActual) 3.dp else 2.dp,
                brush = Brush.verticalGradient(listOf(DoradoClaroEvo, DoradoBordeEvo)),
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(lerp(color, Color.White, 0.65f), lerp(color, Color.White, 0.2f))
                    )
                )
                .border(3.dp, Color.White, CircleShape)
        ) {
            AsyncImage(
                model = etapa.imagenUrl,
                contentDescription = etapa.nombre,
                modifier = Modifier.size(92.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = etapa.nombre.replaceFirstChar { it.uppercase() },
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "#%03d".format(etapa.id),
            color = GrisNumeroEvo,
            fontFamily = FuenteMadera,
            fontSize = 12.sp
        )
        if (etapa.condicion.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = etapa.condicion,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brush.verticalGradient(listOf(DoradoClaroEvo, DoradoInsignia)))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

// Línea punteada que fluye hacia la siguiente etapa, con punta de flecha y la condición encima
@Composable
private fun Conector(condicion: String, flujo: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(ANCHO_CONECTOR)
            .padding(bottom = 34.dp)   // alinea la línea con el centro de los medallones
    ) {
        if (condicion.isNotBlank()) {
            Text(
                text = condicion,
                style = TextStyle(shadow = HaloTexto),
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(Modifier.height(3.dp))
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        ) {
            val y = size.height / 2f
            val tramo = 5.dp.toPx()
            val fin = size.width - 7.dp.toPx()

            // Sombra clara para que se vea sobre el paisaje
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(0f, y), end = Offset(fin, y),
                strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round
            )
            // Puntos que avanzan
            drawLine(
                color = CafeMadera,
                start = Offset(0f, y), end = Offset(fin, y),
                strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(tramo, tramo), -flujo * tramo * 2)
            )
            // Punta de flecha
            val punta = Path().apply {
                moveTo(size.width, y)
                lineTo(fin - 2.dp.toPx(), y - 5.dp.toPx())
                lineTo(fin - 2.dp.toPx(), y + 5.dp.toPx())
                close()
            }
            drawPath(punta, Color.White, style = Stroke(width = 3.dp.toPx()))
            drawPath(punta, CafeMadera)
        }
    }
}

// Círculo de una etapa; la actual con anillo holográfico que gira y brillo que late
@Composable
private fun CirculoEtapa(
    etapa: EtapaEvolucion,
    tamano: Dp,
    color: Color,
    esActual: Boolean,
    giro: Float,
    pulso: Float,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano + 10.dp)
            .drawBehind {
                if (esActual) {
                    val radio = size.minDimension * 0.72f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color.copy(alpha = pulso), Color.Transparent),
                            center = center,
                            radius = radio
                        ),
                        radius = radio
                    )
                    rotate(giro) {
                        drawCircle(
                            brush = Brush.sweepGradient(ColoresAnillo, center),
                            radius = size.minDimension / 2f - 2.dp.toPx(),
                            style = Stroke(width = 3.5.dp.toPx())
                        )
                    }
                }
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(tamano)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(lerp(color, Color.White, 0.65f), lerp(color, Color.White, 0.2f))
                    )
                )
                .border(2.5.dp, Color.White, CircleShape)
        ) {
            AsyncImage(
                model = etapa.imagenUrl,
                contentDescription = etapa.nombre,
                modifier = Modifier.size(tamano * 0.82f)
            )
        }
    }
}

// Medallón con su nombre y número (el actual se distingue por su anillo y tamaño)
@Composable
private fun Medallon(
    etapa: EtapaEvolucion,
    esActual: Boolean,
    color: Color,
    giro: Float,
    pulso: Float,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(ANCHO_MEDALLON)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        CirculoEtapa(
            etapa = etapa,
            tamano = if (esActual) TAMANO_ACTUAL else TAMANO_NORMAL,
            color = color,
            esActual = esActual,
            giro = giro,
            pulso = pulso
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = etapa.nombre.replaceFirstChar { it.uppercase() },
            style = TextStyle(shadow = HaloTexto),
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "#%03d".format(etapa.id),
            style = TextStyle(shadow = HaloTexto),
            color = GrisNumeroEvo,
            fontFamily = FuenteMadera,
            fontSize = 11.sp
        )
    }
}