package com.saludplus.citas.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.ColoresSemanticos
import com.saludplus.citas.ui.theme.Degradados
import com.saludplus.citas.ui.theme.Espaciado
import com.saludplus.citas.ui.theme.Formas
import com.saludplus.citas.ui.theme.GrisTexto
import kotlin.random.Random

// 1. EstadoVacio
@Composable
fun EstadoVacio(
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    icono: ImageVector = Icons.Default.Info,
    accionTexto: String? = null,
    onAccion: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Espaciado.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AzulClaro),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = AzulPrimario,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(Espaciado.md))
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = AzulOscuro,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Espaciado.xs))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTexto,
            textAlign = TextAlign.Center
        )
        if (accionTexto != null && onAccion != null) {
            Spacer(modifier = Modifier.height(Espaciado.lg))
            Button(
                onClick = onAccion,
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                shape = Formas.mediana
            ) {
                Text(text = accionTexto, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// 2. ChipFiltro
@Composable
fun ChipFiltro(
    seleccionado: Boolean,
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = if (icono != null) {
            {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null,
        modifier = modifier,
        shape = Formas.pildora,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AzulPrimario,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White,
            containerColor = AzulClaro,
            labelColor = AzulOscuro,
            iconColor = AzulPrimario
        )
    )
}

// 3. Insignia
@Composable
fun Insignia(
    numero: Int,
    modifier: Modifier = Modifier
) {
    if (numero <= 0) return
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = ColoresSemanticos.Error
    ) {
        Text(
            text = if (numero > 99) "99+" else numero.toString(),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// 4. Esqueleto (Shimmer)
@Composable
fun Esqueleto(
    modifier: Modifier = Modifier,
    forma: Shape = Formas.mediana
) {
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.7f),
        Color.LightGray.copy(alpha = 0.3f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(forma)
            .background(brush)
    )
}

// 5. EncabezadoDegradado
@Composable
fun EncabezadoDegradado(
    titulo: String,
    subtitulo: String? = null,
    onAtras: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    contenido: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Degradados.Principal)
            .padding(horizontal = Espaciado.xl, vertical = Espaciado.lg)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onAtras != null) {
                    IconButton(
                        onClick = onAtras,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(Espaciado.sm))
                }
                Column {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    subtitulo?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
            if (contenido != null) {
                Spacer(modifier = Modifier.height(Espaciado.md))
                contenido()
            }
        }
    }
}

// 6. Modifier.efectoPresion
fun Modifier.efectoPresion(
    habilitado: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    var presionado by remember { mutableStateOf(false) }
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "EfectoPresion"
    )

    this
        .graphicsLayer {
            scaleX = escala
            scaleY = escala
        }
        .pointerInput(habilitado) {
            if (!habilitado) return@pointerInput
            detectTapGestures(
                onPress = {
                    presionado = true
                    try {
                        awaitRelease()
                    } finally {
                        presionado = false
                    }
                },
                onTap = { onClick?.invoke() }
            )
        }
}

// 7. ContadorAnimado
@Composable
fun ContadorAnimado(
    valor: Int,
    modifier: Modifier = Modifier,
    duracionMs: Int = 800
) {
    val animado by animateIntAsState(
        targetValue = valor,
        animationSpec = tween(durationMillis = duracionMs, easing = FastOutSlowInEasing),
        label = "ContadorAnimado"
    )

    Text(
        text = animado.toString(),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = AzulOscuro,
        modifier = modifier
    )
}

// 8. ConfeteAnimado (Canvas)
private data class ParticulaConfeti(
    val xRatio: Float,
    var yRatio: Float,
    val velocidad: Float,
    val tamano: Float,
    val color: Color
)

@Composable
fun ConfeteAnimado(
    activo: Boolean,
    modifier: Modifier = Modifier,
    duracionMs: Long = 2000L
) {
    if (!activo) return

    val colores = listOf(
        AzulPrimario, Color(0xFF0D9488), Color(0xFFF59E0B),
        Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF10B981)
    )

    val particulas = remember {
        List(40) {
            ParticulaConfeti(
                xRatio = Random.nextFloat(),
                yRatio = -0.1f - Random.nextFloat() * 0.3f,
                velocidad = 0.3f + Random.nextFloat() * 0.5f,
                tamano = 8f + Random.nextFloat() * 12f,
                color = colores.random()
            )
        }
    }

    val progresoAnim = remember { Animatable(0f) }

    LaunchedEffect(key1 = activo) {
        progresoAnim.snapTo(0f)
        progresoAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = duracionMs.toInt(), easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier) {
        val p = progresoAnim.value
        particulas.forEach { particula ->
            val y = (particula.yRatio + p * particula.velocidad * 2f) * size.height
            val x = particula.xRatio * size.width
            if (y in 0f..size.height) {
                drawCircle(
                    color = particula.color.copy(alpha = (1f - p).coerceIn(0f, 1f)),
                    radius = particula.tamano,
                    center = Offset(x, y)
                )
            }
        }
    }
}

// 9. CheckAnimado
@Composable
fun CheckAnimado(
    activo: Boolean,
    modifier: Modifier = Modifier,
    tamano: Dp = 64.dp
) {
    val progreso = remember { Animatable(0f) }

    LaunchedEffect(key1 = activo) {
        if (activo) {
            progreso.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        } else {
            progreso.snapTo(0f)
        }
    }

    val colorExito = ColoresSemanticos.Exito

    Canvas(modifier = modifier.size(tamano)) {
        val strokeWidth = 5.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // Dibujar círculo de fondo
        drawCircle(
            color = ColoresSemanticos.ExitoFondo,
            radius = radius,
            center = center
        )

        // Dibujar borde animado
        drawCircle(
            color = colorExito,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Dibujar check animado
        if (progreso.value > 0f) {
            val path = Path().apply {
                moveTo(size.width * 0.28f, size.height * 0.52f)
                lineTo(size.width * 0.45f, size.height * 0.68f)
                lineTo(size.width * 0.72f, size.height * 0.35f)
            }
            val measure = PathMeasure()
            measure.setPath(path, false)

            val animatedPath = Path()
            measure.getSegment(0f, measure.length * progreso.value, animatedPath, true)

            drawPath(
                path = animatedPath,
                color = colorExito,
                style = Stroke(width = strokeWidth + 1.dp.toPx())
            )
        }
    }
}
