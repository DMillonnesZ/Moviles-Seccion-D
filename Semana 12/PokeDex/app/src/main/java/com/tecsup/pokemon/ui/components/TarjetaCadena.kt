package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.CadenaEvolutiva
import com.tecsup.pokemon.domain.model.EtapaEvolucion
import com.tecsup.pokemon.domain.model.MetodoEvolucion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val CelesteNombre = Color(0xFFE4F2FC)
private val CelesteBorde = Color(0xFFBCDCF2)
private val GrisNumeroCadena = Color(0xFF7A6A5A)
private val DoradoClaroCadena = Color(0xFFFFE7A0)
private val DoradoCadena = Color(0xFFF5C451)
private val DoradoBordeCadena = Color(0xFFD9A43A)
private val MoradoPiedra = Color(0xFF9C6ADE)
private val RosaAmistad = Color(0xFFE85D8A)

// Grosor del marco de las tarjetas (más grande = borde y hojas más gruesos)
const val GROSOR_MARCO_CADENA = 1.4f

// Medidas de la fila de la cadena
private val ANCHO_FLECHA = 40.dp            // flecha entre etapas
private val EXTRA_MEDALLON = 14.dp          // ancho extra de la columna sobre el círculo (para el nombre)
private val DESPLAZAMIENTO_PILA = 16.dp     // separación entre círculos apilados
private val TAMANO_MINIMO = 44.dp           // nunca más chico que esto

// Ícono de cada método (null = "Todos")
fun iconoMetodo(metodo: MetodoEvolucion?): ImageVector = when (metodo) {
    null -> Icons.Rounded.AccountTree
    MetodoEvolucion.NIVEL -> Icons.Rounded.ArrowUpward
    MetodoEvolucion.PIEDRA -> Icons.Rounded.Diamond
    MetodoEvolucion.INTERCAMBIO -> Icons.Rounded.SyncAlt
    MetodoEvolucion.AMISTAD -> Icons.Rounded.Favorite
    MetodoEvolucion.OTROS -> Icons.Rounded.MoreHoriz
}

// Color del ícono de cada método
fun colorMetodo(metodo: MetodoEvolucion?): Color = when (metodo) {
    MetodoEvolucion.PIEDRA -> MoradoPiedra
    MetodoEvolucion.AMISTAD -> RosaAmistad
    else -> CafeMadera
}

// Tipo que da el color: evita "normal" y "volador" si tiene otro (Charizard -> fuego)
fun tipoParaColor(tipos: List<String>?): String? =
    tipos?.firstOrNull { it != "normal" && it != "flying" } ?: tipos?.firstOrNull()

// Pestaña dorada pequeña con ícono (método de la tarjeta, títulos de sección)
@Composable
fun PestanaMetodo(
    texto: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    colorIcono: Color = CafeMadera
) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(2.dp, forma)
            .clip(forma)
            .background(Brush.verticalGradient(listOf(DoradoClaroCadena, DoradoCadena)))
            .border(1.dp, DoradoBordeCadena, forma)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            text = texto,
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Círculo del Pokémon: degradado del color de su tipo, burbujas y borde blanco
@Composable
fun CirculoPokemon(
    imagenUrl: String,
    nombre: String,
    color: Color,
    tamano: Dp,
    modifier: Modifier = Modifier,
    resaltado: Boolean = false
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano)
            // Brillo dorado detrás si está resaltado
            .drawBehind {
                if (resaltado) {
                    val radio = size.minDimension * 0.68f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(DoradoCadena.copy(alpha = 0.7f), Color.Transparent),
                            center = center,
                            radius = radio
                        ),
                        radius = radio
                    )
                }
            }
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(lerp(color, Color.White, 0.72f), lerp(color, Color.White, 0.18f))
                )
            )
            .border(
                width = if (resaltado) 3.dp else 2.5.dp,
                color = if (resaltado) DoradoCadena else Color.White,
                shape = CircleShape
            )
    ) {
        // Burbujas decorativas
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            listOf(
                Triple(0.22f, 0.30f, 0.06f),
                Triple(0.32f, 0.17f, 0.035f),
                Triple(0.80f, 0.72f, 0.05f),
                Triple(0.74f, 0.24f, 0.03f)
            ).forEach { (x, y, r) ->
                drawCircle(Color.White.copy(alpha = 0.55f), radius = w * r, center = Offset(w * x, w * y))
            }
        }
        AsyncImage(
            model = imagenUrl,
            contentDescription = nombre,
            modifier = Modifier.fillMaxSize(0.84f)
        )
    }
}

// Medallón con la pastilla celeste del nombre y número debajo
@Composable
fun MedallonCadena(
    etapa: EtapaEvolucion,
    color: Color,
    modifier: Modifier = Modifier,
    tamano: Dp = 60.dp,
    resaltado: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(tamano + EXTRA_MEDALLON)
    ) {
        CirculoPokemon(etapa.imagenUrl, etapa.nombre, color, tamano, resaltado = resaltado)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(y = (-8).dp)
                .clip(RoundedCornerShape(50))
                .background(CelesteNombre)
                .border(1.dp, CelesteBorde, RoundedCornerShape(50))
                .padding(horizontal = 6.dp, vertical = 1.dp)
        ) {
            Text(
                text = etapa.nombre.replaceFirstChar { it.uppercase() },
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "#%03d".format(etapa.id),
                color = GrisNumeroCadena,
                fontFamily = FuenteMadera,
                fontSize = 10.sp,
                lineHeight = 11.sp
            )
        }
    }
}

// Flecha punteada café con el requisito encima
@Composable
fun FlechaCadena(condicion: String, modifier: Modifier = Modifier, ancho: Dp = ANCHO_FLECHA) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(ancho)
    ) {
        if (condicion.isNotBlank()) {
            Text(
                text = condicion,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        ) {
            val y = size.height / 2f
            val fin = size.width - 6.dp.toPx()
            val tramo = 4.dp.toPx()
            drawLine(
                color = CafeMadera,
                start = Offset(2.dp.toPx(), y),
                end = Offset(fin, y),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(tramo, tramo))
            )
            val punta = Path().apply {
                moveTo(size.width, y)
                lineTo(fin - 1.dp.toPx(), y - 4.dp.toPx())
                lineTo(fin - 1.dp.toPx(), y + 4.dp.toPx())
                close()
            }
            drawPath(punta, CafeMadera)
        }
    }
}

// Estrella dorada con la cantidad (ej. "×8")
@Composable
fun EstrellaCantidad(texto: String, modifier: Modifier = Modifier, tamano: Dp = 34.dp) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(tamano)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val puntas = 12
            val radioExterior = size.minDimension / 2f
            val radioInterior = radioExterior * 0.82f
            val estrella = Path().apply {
                for (i in 0 until puntas * 2) {
                    val radio = if (i % 2 == 0) radioExterior else radioInterior
                    val angulo = PI * i / puntas - PI / 2
                    val x = center.x + (radio * cos(angulo)).toFloat()
                    val y = center.y + (radio * sin(angulo)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(estrella, Brush.verticalGradient(listOf(Color(0xFFFFE066), Color(0xFFF5B800))))
            drawPath(estrella, DoradoBordeCadena, style = Stroke(width = 1.5.dp.toPx()))
        }
        Text(
            text = texto,
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

// Varias evoluciones en la misma etapa: círculos superpuestos como cartas + estrella con la cantidad.
// compacta = texto en dos líneas (cuando la cadena tiene 3 etapas y hay poco espacio)
@Composable
fun PilaRamas(
    etapas: List<EtapaEvolucion>,
    colorDe: (Int) -> Color,
    modifier: Modifier = Modifier,
    tamano: Dp = 60.dp,
    compacta: Boolean = false
) {
    val visibles = etapas.take(4)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(
            modifier = Modifier
                .height(tamano)
                .width(tamano + DESPLAZAMIENTO_PILA * (visibles.size - 1) + EXTRA_MEDALLON)
        ) {
            visibles.forEachIndexed { i, etapa ->
                CirculoPokemon(
                    imagenUrl = etapa.imagenUrl,
                    nombre = etapa.nombre,
                    color = colorDe(etapa.id),
                    tamano = tamano,
                    modifier = Modifier
                        .offset(x = DESPLAZAMIENTO_PILA * i)
                        .zIndex(-i.toFloat())
                        .graphicsLayer { rotationZ = i * 6f }
                )
            }
            EstrellaCantidad(
                texto = "×${etapas.size}",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = (-6).dp)
                    .zIndex(10f)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(y = (-6).dp)
                .clip(RoundedCornerShape(50))
                .background(CelesteNombre)
                .border(1.dp, CelesteBorde, RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            if (compacta) {
                Text(
                    text = "${etapas.size} evoluciones",
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 12.sp
                )
                Text(
                    text = "Toca para ver",
                    color = GrisNumeroCadena,
                    fontFamily = FuenteMadera,
                    fontSize = 10.sp,
                    lineHeight = 11.sp
                )
            } else {
                Text(
                    text = "${etapas.size} evoluciones · Toca para ver",
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// Fila con toda la cadena: medallones unidos por flechas; las ramas se apilan.
// El tamaño de los medallones se ajusta para que la cadena entre completa en el ancho disponible.
@Composable
fun FilaCadena(
    cadena: CadenaEvolutiva,
    colorDe: (Int) -> Color,
    modifier: Modifier = Modifier,
    tamanoMaximo: Dp = 60.dp
) {
    val fases = cadena.etapas.groupBy { it.fase }.toSortedMap().values.toList()

    BoxWithConstraints(contentAlignment = Alignment.Center, modifier = modifier.fillMaxWidth()) {
        val cantidad = fases.size.coerceAtLeast(1)
        // Ancho extra que ocupan las pilas por sus círculos superpuestos
        val extraPilas = fases
            .filter { it.size > 1 }
            .fold(0.dp) { total, grupo -> total + DESPLAZAMIENTO_PILA * (minOf(grupo.size, 4) - 1) }
        // Tamaño que hace entrar todo: (ancho - flechas - pilas) / etapas - extra de la columna
        val disponible = (maxWidth - ANCHO_FLECHA * (cantidad - 1) - extraPilas) / cantidad - EXTRA_MEDALLON
        val tamano = disponible.coerceIn(TAMANO_MINIMO, tamanoMaximo)
        val compacta = cantidad >= 3

        Row(
            verticalAlignment = Alignment.Top,
            // Respaldo: si aun así no entra (cadenas muy largas), se desliza de lado
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            fases.forEachIndexed { indice, grupo ->
                if (indice > 0) {
                    // La flecha queda a la altura del centro de los círculos
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.height(tamano)) {
                        FlechaCadena(condicion = if (grupo.size == 1) grupo.first().condicion else "")
                    }
                }
                if (grupo.size == 1) {
                    MedallonCadena(grupo.first(), colorDe(grupo.first().id), tamano = tamano)
                } else {
                    PilaRamas(grupo, colorDe, tamano = tamano, compacta = compacta)
                }
            }
        }
    }
}

// Tarjeta de una cadena: marco de madera, pestaña con el método y la cadena completa.
// Se hunde un poco al presionarla.
@Composable
fun TarjetaCadena(
    cadena: CadenaEvolutiva,
    colorDe: (Int) -> Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionada by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionada) 0.97f else 1f, label = "escalaCadena")

    val primerMetodo = cadena.metodos.firstOrNull()
    val etiqueta = if (cadena.metodos.isEmpty()) "Sin evolución"
    else cadena.metodos.joinToString(" / ") { it.etiqueta }

    PanelMadera(
        marco = R.drawable.marco_cadena,
        margenLateral = 0.dp,
        respiro = 4.dp,
        grosorMarco = GROSOR_MARCO_CADENA,
        modifier = modifier
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .clickable(interactionSource = interaccion, indication = null, onClick = onClick)
    ) {
        Column {
            // Corrida a la derecha y abajo para no chocar con las hojas de la esquina
            PestanaMetodo(
                texto = etiqueta,
                icono = if (cadena.metodos.isEmpty()) Icons.Rounded.Eco else iconoMetodo(primerMetodo),
                colorIcono = colorMetodo(primerMetodo),
                modifier = Modifier.padding(start = 28.dp, top = 4.dp)
            )
            Spacer(Modifier.height(6.dp))
            FilaCadena(cadena = cadena, colorDe = colorDe)
        }
    }
}