package com.millones.campusgps.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.acos
import kotlin.math.roundToInt
import android.os.Build
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Constraints

// Cian de los acentos sobre el vidrio (foco de los campos y "¿Olvidaste tu contraseña?")
val CianCampus = Color(0xFF5CE1E6)

// ---------------------------------------------------------------------------
// Componentes de las pantallas de acceso (equivalen a Madera.kt de la PokeDex)
// ---------------------------------------------------------------------------

// Entrada suave: el contenido sube y aparece al abrir la pantalla (AparecerSuave de la PokeDex)
@Composable
fun EntradaSuave(
    retrasoMs: Long = 0L,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val progreso = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(retrasoMs)
        progreso.animateTo(1f, tween(durationMillis = 650, easing = FastOutSlowInEasing))
    }
    Box(
        modifier = modifier.graphicsLayer {
            alpha = progreso.value
            translationY = (1f - progreso.value) * 60f
        }
    ) {
        content()
    }
}

// Nombre de la app (CartelTitulo de la PokeDex): ícono de GPS dibujado y el nombre con
// degradado suave, resplandor difuminado y un brillo que lo recorre; la frase va debajo
// compacto = true: más pequeño y sin la frase (pantallas con muchos campos, como el registro)
@Composable
fun LogoCampus(compacto: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // El nombre queda centrado (alineado con la frase de abajo) y el pin se cuelga
        // a su izquierda sin ocupar ancho, así no empuja el nombre hacia un lado
        val separacion = if (compacto) 4.dp else 6.dp
        Box(contentAlignment = Alignment.Center) {
            NombreCampus(fontSize = if (compacto) 32.sp else 40.sp)
            LogoGps(
                tamano = if (compacto) 48.dp else 60.dp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .layout { medible, restricciones ->
                        val pin = medible.measure(restricciones.copy(minWidth = 0, minHeight = 0))
                        // Ancho 0: el Box solo mide el nombre
                        layout(0, pin.height) {
                            pin.place(-pin.width - separacion.roundToPx(), 0)
                        }
                    }
            )
        }
        if (!compacto) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Encuentra tu camino en el campus",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = FuenteCampus,
                fontSize = 14.sp,
                letterSpacing = 0.2.sp
            )
        }
    }
}

// "CampusGPS" con degradado suave, resplandor difuminado y brillo lento (login y splash)
@Composable
fun NombreCampus(fontSize: TextUnit) {
    Text(
        text = buildAnnotatedString {
            // "Campus" blanco limpio y "GPS" con degradado menta -> turquesa (como el diseño)
            withStyle(SpanStyle(color = Color.White)) { append("Campus") }
            withStyle(
                SpanStyle(brush = Brush.horizontalGradient(listOf(Color(0xFF7BF1C8), Color(0xFF34D3C4))))
            ) { append("GPS") }
        },
        style = TextStyle(
            fontFamily = FuenteCampus,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            // Resplandor celeste difuminado detrás de las letras
            shadow = Shadow(color = CampusColores.AzulCielo.copy(alpha = 0.25f), offset = Offset.Zero, blurRadius = 14f)
        ),
        // Brillo suave y lento
        modifier = Modifier.brilloRecorrido(intensidad = 0.2f, duracionMs = 5000)
    )
}

// Ícono de GPS dibujado con código: pin blanco que flota (sin fondo),
// con ondas de señal menta que salen hacia arriba y su sombra en el suelo
// Cuando la ruta del fondo llega, el pin salta y suelta una onda menta.
@Composable
fun LogoGps(
    tamano: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    // Llegadas de la ruta (0 si no hay ruta viva alrededor)
    val llegadas = LocalEstadoRuta.current?.llegadas ?: 0
    val salto = remember { Animatable(0f) }
    val ondaLlegada = remember { Animatable(1f) }   // 1 = sin onda
    LaunchedEffect(llegadas) {
        if (llegadas > 0) {
            launch {
                ondaLlegada.snapTo(0f)
                ondaLlegada.animateTo(1f, tween(800))
            }
            salto.animateTo(1f, tween(140))
            salto.animateTo(0f, spring(dampingRatio = 0.35f))
        }
    }

    val transicion = rememberInfiniteTransition(label = "logoGps")
    val senal by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "senal"
    )
    val flote by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flote"
    )

    Box(modifier = modifier.size(tamano)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val s = size.minDimension
            val cx = size.width / 2f
            val subida = -flote * s * 0.035f - salto.value * s * 0.14f
            val radioPin = s * 0.17f
            val centroPin = Offset(cx, size.height * 0.43f + subida)
            val punta = Offset(cx, size.height * 0.75f + subida)

            // Sombra en el suelo: se achica cuando el pin sube
            val anchoSombra = s * (0.26f - 0.06f * flote)
            drawOval(
                color = CampusColores.AzulCielo.copy(alpha = 0.45f),
                topLeft = Offset(cx - anchoSombra / 2f, size.height * 0.79f),
                size = Size(anchoSombra, s * 0.06f)
            )

            // Dos ondas de señal que salen por arriba del pin
            listOf(0f, 0.5f).forEach { desfase ->
                val t = (senal + desfase) % 1f
                val radio = radioPin * (1.3f + 0.9f * t)
                drawArc(
                    color = CampusColores.VerdeMenta.copy(alpha = (1f - t) * 0.9f),
                    startAngle = -135f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(centroPin.x - radio, centroPin.y - radio),
                    size = Size(radio * 2f, radio * 2f),
                    style = Stroke(width = s * 0.045f, cap = StrokeCap.Round)
                )
            }

            // Pin en forma de gota: dos líneas desde la punta, tangentes al círculo de arriba
            val distancia = punta.y - centroPin.y
            val angulo = Math.toDegrees(acos((radioPin / distancia).toDouble())).toFloat()
            val pin = Path().apply {
                moveTo(punta.x, punta.y)
                arcTo(
                    rect = Rect(center = centroPin, radius = radioPin),
                    startAngleDegrees = 90f - angulo,
                    sweepAngleDegrees = -(360f - 2f * angulo),
                    forceMoveTo = false
                )
                close()
            }
            // Pin blanco con un toque celeste abajo
            drawPath(
                path = pin,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFD3EBFF)),
                    startY = centroPin.y - radioPin,
                    endY = punta.y
                )
            )
            // Agujero del pin
            drawCircle(CampusColores.AzulProfundo, radius = radioPin * 0.42f, center = centroPin)

            // Onda menta al llegar la ruta
            if (ondaLlegada.value < 1f) {
                val v = ondaLlegada.value
                drawCircle(
                    color = CampusColores.VerdeMenta.copy(alpha = (1f - v) * 0.8f),
                    radius = radioPin * (1.2f + 2.4f * v),
                    center = centroPin,
                    style = Stroke(width = s * 0.03f)
                )
            }
        }
    }
}

// Tarjeta de vidrio esmerilado donde va el formulario: detrás dibuja la misma parte del fondo
// pero desenfocada (Android 12+), con un tinte azul translúcido y borde blanco fino.
// En Android 11 o menos no hay desenfoque: el tinte se vuelve más oscuro para que se lea.
// compacta = true: menos espacio entre elementos y menos relleno (registro)
@Composable
fun TarjetaAuth(
    modifier: Modifier = Modifier,
    compacta: Boolean = false,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(30.dp)
    val fondo = LocalFondoAuth.current
    val conDesenfoque = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    // Posición de la tarjeta dentro del fondo (para recortar la misma zona)
    var desfase by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { tarjeta ->
                val base = fondo?.coordenadas
                if (base != null && base.isAttached && tarjeta.isAttached) {
                    desfase = base.localPositionOf(tarjeta, Offset.Zero)
                }
            }
            .clip(forma)
    ) {
        val tamanoFondo = fondo?.coordenadas?.size
        if (conDesenfoque && tamanoFondo != null) {
            Box(modifier = Modifier.matchParentSize()) {
                CapaFondoAuth(
                    Modifier
                        .layout { medible, restricciones ->
                            val capa = medible.measure(
                                Constraints.fixed(tamanoFondo.width, tamanoFondo.height)
                            )
                            layout(restricciones.maxWidth, restricciones.maxHeight) {
                                capa.place(-desfase.x.roundToInt(), -desfase.y.roundToInt())
                            }
                        }
                        .blur(26.dp)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (compacta) 10.dp else 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF14295A).copy(alpha = if (conDesenfoque) 0.30f else 0.62f))
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.05f))
                    )
                )
                .border(
                    1.2.dp,
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.45f), Color.White.copy(alpha = 0.12f))
                    ),
                    forma
                )
                .padding(horizontal = 24.dp, vertical = if (compacta) 20.dp else 28.dp),
            content = contenido
        )
    }
}

// Encabezado de la tarjeta: título de color partido y descripción
@Composable
fun EncabezadoTarjeta(titulo: String, descripcion: String, compacto: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compacto) 2.dp else 6.dp)
    ) {
        Text(
            text = titulo,
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (compacto) 23.sp else 26.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = descripcion,
            color = Color.White.copy(alpha = 0.80f),
            fontFamily = FuenteCampus,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Subtítulo en píldora con rayitas a los lados (TablaSubtitulo de la PokeDex)
@Composable
fun SubtituloCampus(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Rayitas(izquierda = true)
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            color = Color.White,
            fontFamily = FuenteCampus,
            fontSize = 16.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
                .padding(horizontal = 18.dp, vertical = 7.dp)
        )
        Spacer(Modifier.width(6.dp))
        Rayitas(izquierda = false)
    }
}

@Composable
private fun Rayitas(izquierda: Boolean) {
    Canvas(modifier = Modifier.size(width = 16.dp, height = 18.dp)) {
        val grosor = 2.5.dp.toPx()
        val w = size.width
        val h = size.height
        val color = CampusColores.VerdeMenta
        if (izquierda) {
            drawLine(color, Offset(0f, h * 0.2f), Offset(w, h * 0.4f), grosor, StrokeCap.Round)
            drawLine(color, Offset(0f, h * 0.8f), Offset(w, h * 0.6f), grosor, StrokeCap.Round)
        } else {
            drawLine(color, Offset(w, h * 0.2f), Offset(0f, h * 0.4f), grosor, StrokeCap.Round)
            drawLine(color, Offset(w, h * 0.8f), Offset(0f, h * 0.6f), grosor, StrokeCap.Round)
        }
    }
}

// Desvanece el texto solo en el lado donde queda texto escondido
private fun Modifier.bordesDifuminados(izquierda: Boolean, derecha: Boolean, ancho: Float = 0.10f): Modifier {
    if (!izquierda && !derecha) return this
    return this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.horizontalGradient(
                    0f to if (izquierda) Color.Transparent else Color.Black,
                    ancho to Color.Black,
                    1f - ancho to Color.Black,
                    1f to if (derecha) Color.Transparent else Color.Black
                ),
                blendMode = BlendMode.DstIn
            )
        }
}

// Muestra la contraseña como puntos sin cambiar el texto real
private object OcultarClave : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        replace(0, length, "•".repeat(length))
    }
}

// Campo de la tarjeta (CampoMadera de la PokeDex). Una línea que se desliza a lo ancho,
// con los bordes difuminados y borde celeste al enfocarse.
// La etiqueta sube y se achica al escribir; valido = true muestra un check verde.
@Composable
fun CampoCampus(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    icono: ImageVector?,          // null = sin ícono (como la contraseña del login)
    esClave: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    habilitado: Boolean = true,
    valido: Boolean = false,
    alto: Dp = 60.dp
) {
    var verClave by rememberSaveable { mutableStateOf(false) }
    val estadoTexto = rememberTextFieldState(initialText = valor)
    val desplazamiento = rememberScrollState()
    val onValorChangeActual by rememberUpdatedState(onValorChange)
    val interaccion = remember { MutableInteractionSource() }
    val enfocado by interaccion.collectIsFocusedAsState()

    // Campo -> ViewModel
    LaunchedEffect(estadoTexto) {
        snapshotFlow { estadoTexto.text.toString() }
            .drop(1)
            .collect { onValorChangeActual(it) }
    }
    // ViewModel -> campo (por ejemplo, si lo limpia)
    LaunchedEffect(valor) {
        if (estadoTexto.text.toString() != valor) {
            estadoTexto.setTextAndPlaceCursorAtEnd(valor)
        }
    }

    val colorBorde by animateColorAsState(
        targetValue = if (enfocado) CianCampus else Color.White.copy(alpha = 0.22f),
        animationSpec = tween(250),
        label = "bordeCampo"
    )
    val colorIcono by animateColorAsState(
        targetValue = if (enfocado) CianCampus else Color.White.copy(alpha = 0.85f),
        animationSpec = tween(250),
        label = "iconoCampo"
    )
    val colorRelleno by animateColorAsState(
        targetValue = if (enfocado) CianCampus.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.07f),
        animationSpec = tween(250),
        label = "rellenoCampo"
    )
    // 0 = etiqueta dentro como texto guía, 1 = etiqueta arriba y pequeña
    val etiquetaArriba by animateFloatAsState(
        targetValue = if (enfocado || estadoTexto.text.isNotEmpty()) 1f else 0f,
        animationSpec = tween(200),
        label = "etiquetaArriba"
    )
    val forma = RoundedCornerShape(18.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(alto)
            // Halo cian alrededor del campo enfocado
            .drawBehind {
                if (enfocado) {
                    val halo = 3.dp.toPx()
                    drawRoundRect(
                        color = CianCampus.copy(alpha = 0.28f),
                        topLeft = Offset(-halo, -halo),
                        size = Size(size.width + halo * 2, size.height + halo * 2),
                        cornerRadius = CornerRadius(21.dp.toPx()),
                        style = Stroke(width = halo * 1.4f)
                    )
                }
            }
            .clip(forma)
            .background(colorRelleno)
            .border(1.5.dp, colorBorde, forma)
            .padding(start = 18.dp, end = 16.dp)
    ) {
        // Ícono suelto, sin círculo detrás
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
        }
        BasicTextField(
            state = estadoTexto,
            enabled = habilitado,
            lineLimits = TextFieldLineLimits.SingleLine,
            scrollState = desplazamiento,
            interactionSource = interaccion,
            textStyle = TextStyle(
                color = Color.White,
                fontFamily = FuenteCampus,
                fontSize = 16.sp
            ),
            cursorBrush = SolidColor(CianCampus),
            outputTransformation = if (esClave && !verClave) OcultarClave else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (esClave) KeyboardType.Password else tipoTeclado
            ),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .bordesDifuminados(
                    izquierda = desplazamiento.value > 0,
                    derecha = desplazamiento.value < desplazamiento.maxValue
                ),
            decorator = { campo ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Etiqueta flotante: sube y se achica cuando hay foco o texto
                    Text(
                        text = placeholder,
                        color = if (etiquetaArriba > 0.5f) colorIcono else Color.White.copy(alpha = 0.85f),
                        fontFamily = FuenteCampus,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer {
                            transformOrigin = TransformOrigin(0f, 0.5f)
                            val escala = 1f - 0.25f * etiquetaArriba
                            scaleX = escala
                            scaleY = escala
                            translationY = -12.dp.toPx() * etiquetaArriba
                        }
                    )
                    // El texto escrito queda debajo de la etiqueta
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { translationY = 8.dp.toPx() * etiquetaArriba }
                    ) {
                        campo()
                    }
                }
            }
        )
        // Check verde cuando el dato es válido
        AnimatedVisibility(
            visible = valido,
            enter = scaleIn(spring(dampingRatio = 0.45f)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Dato válido",
                tint = CampusColores.VerdeMenta,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }
        if (esClave) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = if (verClave) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                contentDescription = if (verClave) "Ocultar contraseña" else "Mostrar contraseña",
                tint = colorIcono,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { verClave = !verClave }
            )
        }
    }
}

// Barra de fuerza de la contraseña: 0 oculta, 1 débil, 2 media, 3 fuerte
@Composable
fun BarraFuerzaClave(nivel: Int) {
    val (etiqueta, colorObjetivo) = when (nivel) {
        1 -> "Débil" to CampusColores.Error
        2 -> "Media" to CampusColores.Ambar
        3 -> "Fuerte" to CampusColores.Exito
        else -> "" to Color.White
    }
    val color by animateColorAsState(colorObjetivo, tween(300), label = "colorFuerza")

    AnimatedVisibility(
        visible = nivel > 0,
        enter = fadeIn(tween(250)) + expandVertically(tween(250)),
        exit = fadeOut(tween(250)) + shrinkVertically(tween(250))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp, top = 8.dp)
        ) {
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (i < nivel) color else Color.White.copy(alpha = 0.18f))
                )
                if (i < 2) Spacer(Modifier.width(4.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = etiqueta,
                color = color,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.20f))
                    .padding(horizontal = 8.dp, vertical = 1.dp)
            )
        }
    }
}

// Flecha dibujada a mano (chevron)
@Composable
fun FlechaDibujada(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val ancho = size.width
        val alto = size.height
        val trazo = Path().apply {
            moveTo(ancho * 0.28f, alto * 0.14f)
            lineTo(ancho * 0.74f, alto * 0.50f)
            lineTo(ancho * 0.28f, alto * 0.86f)
        }
        drawPath(
            path = trazo,
            color = color,
            style = Stroke(width = ancho * 0.18f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

// Botón principal (BotonMadera de la PokeDex): degradado y se hunde al presionar.
// Mientras carga se encoge hasta ser un círculo con la ruedita girando.
@Composable
fun BotonCampus(
    texto: String,
    onClick: () -> Unit,
    cargando: Boolean = false,
    alto: Dp = 60.dp
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val encima by interaccion.collectIsHoveredAsState()   // mouse encima (emulador / tablet)
    val activo = presionado || encima
    val escala by animateFloatAsState(
        targetValue = when {
            presionado -> 0.96f
            encima -> 1.02f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.5f),
        label = "escalaBoton"
    )
    val resplandor by animateDpAsState(
        targetValue = if (activo) 26.dp else 16.dp,
        animationSpec = tween(250),
        label = "resplandorBoton"
    )

    // Brillo diagonal que cruza el botón: al pasar por encima / tocarlo y cada pocos segundos
    val brillo = remember { Animatable(1.3f) }   // fuera del botón = sin brillo
    LaunchedEffect(activo) {
        if (activo) {
            brillo.snapTo(-0.3f)
            brillo.animateTo(1.3f, tween(650, easing = FastOutSlowInEasing))
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            if (!brillo.isRunning) {
                brillo.snapTo(-0.3f)
                brillo.animateTo(1.3f, tween(1000, easing = FastOutSlowInEasing))
            }
        }
    }

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        val ancho by animateDpAsState(
            targetValue = if (cargando) alto else maxWidth,
            animationSpec = tween(380, easing = FastOutSlowInEasing),
            label = "anchoBoton"
        )
        val esquina by animateDpAsState(
            targetValue = if (cargando) alto / 2 else 16.dp,
            animationSpec = tween(380, easing = FastOutSlowInEasing),
            label = "esquinaBoton"
        )
        val forma = RoundedCornerShape(esquina)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(ancho)
                .height(alto)
                .graphicsLayer {
                    scaleX = escala
                    scaleY = escala
                }
                // Resplandor azul alrededor del botón (crece al pasar por encima)
                .shadow(resplandor, forma, ambientColor = Color(0xFF3B82F6), spotColor = Color(0xFF3B82F6))
                .clip(forma)
                .background(Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF3B8BFF))))
                // Luz suave arriba, como el vidrio del diseño
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.18f), Color.Transparent)
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.30f), forma)
                .drawWithContent {
                    drawContent()
                    if (!cargando) {
                        val x = size.width * brillo.value
                        val banda = size.width * 0.18f
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.45f),
                                    Color.Transparent
                                ),
                                start = Offset(x - banda, 0f),
                                end = Offset(x + banda, size.height)
                            )
                        )
                    }
                    // Se aclara un poco mientras está encima o presionado
                    if (activo) drawRect(Color.White.copy(alpha = 0.08f))
                }
                .hoverable(interaccion)
                .clickable(interactionSource = interaccion, indication = null, enabled = !cargando, onClick = onClick)
        ) {
            if (cargando) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(26.dp))
            } else {
                Text(
                    text = texto,
                    color = Color.White,
                    fontFamily = FuenteCampus,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

// Pregunta + enlace (EnlaceMadera de la PokeDex).
// sinFondo = true: solo texto, para ir dentro de la tarjeta de vidrio (login)
@Composable
fun EnlaceCampus(
    pregunta: String,
    accion: String,
    onClick: () -> Unit,
    sinFondo: Boolean = false
) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = if (sinFondo) {
            Modifier.padding(vertical = 4.dp)
        } else {
            Modifier
                .clip(forma)
                .background(Color.White.copy(alpha = 0.10f))
                .border(1.dp, Color.White.copy(alpha = 0.22f), forma)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        }
    ) {
        Text(
            text = "$pregunta ",
            color = Color.White.copy(alpha = 0.75f),
            fontFamily = FuenteCampus,
            fontSize = 15.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Text(
                text = accion,
                color = CampusColores.VerdeMenta,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = CampusColores.VerdeMenta,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Aviso en píldora blanca con ícono: rojo si es error, verde si es éxito
@Composable
fun MensajeAviso(texto: String, exito: Boolean = false) {
    // Tonos claros para que se lean sobre el vidrio oscuro
    val color = if (exito) CampusColores.VerdeMenta else Color(0xFFFF8A8A)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.40f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 11.dp)
    ) {
        Icon(
            imageVector = if (exito) Icons.Rounded.CheckCircle else Icons.Rounded.ErrorOutline,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = texto,
            color = color,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
}

// Aviso que aparece con fundido, dura unos segundos y se oculta solo (ErrorTemporal de la PokeDex)
@Composable
fun AvisoTemporal(
    mensaje: String?,
    onOcultar: () -> Unit,
    exito: Boolean = false,
    duracionMs: Long = 2500
) {
    // Guarda el último texto para que se vea mientras se desvanece
    var ultimoMensaje by remember { mutableStateOf("") }

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            ultimoMensaje = mensaje
            delay(duracionMs)
            onOcultar()
        }
    }

    AnimatedVisibility(
        visible = mensaje != null,
        enter = fadeIn(tween(300)) + expandVertically(tween(300)),
        exit = fadeOut(tween(600)) + shrinkVertically(tween(600))
    ) {
        MensajeAviso(ultimoMensaje, exito)
    }
}

// Sacudida de lado a lado con vibración corta cada vez que aparece un error
// (se aplica a la tarjeta del formulario)
@Composable
fun Modifier.sacudida(error: String?): Modifier {
    val desplazamiento = remember { Animatable(0f) }
    val haptico = LocalHapticFeedback.current

    LaunchedEffect(error) {
        if (error != null) {
            haptico.performHapticFeedback(HapticFeedbackType.Reject)
            listOf(-14f, 12f, -9f, 6f, -3f, 0f).forEach { x ->
                desplazamiento.animateTo(x, tween(50))
            }
        }
    }
    return this.graphicsLayer { translationX = desplazamiento.value.dp.toPx() }
}
