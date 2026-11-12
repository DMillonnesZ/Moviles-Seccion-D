package com.tecsup.pokemon.ui.evolucion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.CadenaEvolutiva
import com.tecsup.pokemon.domain.model.EtapaEvolucion
import com.tecsup.pokemon.domain.model.MetodoEvolucion
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.components.AnimacionEvolucion
import com.tecsup.pokemon.ui.components.BotonVolver
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.CirculoPokemon
import com.tecsup.pokemon.ui.components.FlechaCadena
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.GROSOR_MARCO_CADENA
import com.tecsup.pokemon.ui.components.LetreroSeccion
import com.tecsup.pokemon.ui.components.LocalRellenoBarra
import com.tecsup.pokemon.ui.components.PanelMadera
import com.tecsup.pokemon.ui.components.PestanaMetodo
import com.tecsup.pokemon.ui.components.PilaRamas
import com.tecsup.pokemon.ui.components.colorDeTipo
import com.tecsup.pokemon.ui.components.colorMetodo
import com.tecsup.pokemon.ui.components.desvanecerBordes
import com.tecsup.pokemon.ui.components.iconoMetodo
import com.tecsup.pokemon.ui.components.tipoParaColor
import com.tecsup.pokemon.util.Traductor
import kotlinx.coroutines.delay

private val GrisDatoEvo = Color(0xFF7A6A5A)
private val CelesteCaja = Color(0xFFE8F3FC)
private val CelesteBordeCaja = Color(0xFFBCDCF2)
private val CelesteNombreEvo = Color(0xFFE4F2FC)
private val AzulInfo = Color(0xFF3D7BD9)
private val DoradoEvo = Color(0xFFF5C451)
private val DoradoClaroEvo = Color(0xFFFFE7A0)
private val DoradoBordeEvo = Color(0xFFD9A43A)
private val CremaEvo = Color(0xFFFFF4DF)

// Parte de arriba y abajo del marco que no se estira (las hojas son altas)
private const val CORTE_MARCO_CADENA = 0.3f

// Medidas de la cadena seleccionable
private val ANCHO_FLECHA_DETALLE = 40.dp
private val EXTRA_COLUMNA = 14.dp
private val DESPLAZAMIENTO_PILA_DETALLE = 16.dp

// Detalle de una cadena evolutiva: cadena para elegir una etapa, datos de la etapa,
// requisitos, proceso escrito y botón para ver la animación de evolución
@Composable
fun EvolucionDetalleScreen(
    viewModel: EvolucionDetalleViewModel,
    onVolver: () -> Unit
) {
    val cadena by viewModel.cadena.collectAsStateWithLifecycle()
    val tipos by viewModel.tiposPorPokemon.collectAsStateWithLifecycle()
    val tiposInfo by viewModel.tiposInfo.collectAsStateWithLifecycle()

    FondoInicio {
        Column(modifier = Modifier.fillMaxSize()) {
            // ----- Cabecera: volver + letrero centrado -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 36.dp, bottom = 4.dp)
            ) {
                BotonVolver(onClick = onVolver)
                Spacer(Modifier.width(8.dp))
                LetreroSeccion(
                    texto = "Evolución",
                    alto = 62.dp,
                    tamanoTexto = 20.sp,
                    icono = {
                        Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(22.dp))
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                // Mismo ancho que el botón, para que el letrero quede centrado
                Spacer(Modifier.width(40.dp))
            }

            // ----- Contenido: termina encima de la barra y se desvanece en los bordes -----
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = LocalRellenoBarra.current)
                    .desvanecerBordes(arriba = 20.dp, abajo = 20.dp)
            ) {
                val actual = cadena
                if (actual == null) {
                    Cargando("Cargando evolución...")
                } else {
                    ContenidoEvolucion(cadena = actual, tipos = tipos, tiposInfo = tiposInfo)
                }
            }
        }
    }
}

@Composable
private fun ContenidoEvolucion(
    cadena: CadenaEvolutiva,
    tipos: Map<Int, List<String>>,
    tiposInfo: Map<String, Tipo>
) {
    // Etapa elegida: al inicio la básica
    var seleccionadoId by rememberSaveable(cadena.id) { mutableIntStateOf(cadena.etapas.first().id) }
    // Animación en curso: (desde, hacia)
    var animando by remember { mutableStateOf<Pair<EtapaEvolucion, EtapaEvolucion>?>(null) }

    val seleccionado = cadena.etapas.firstOrNull { it.id == seleccionadoId } ?: cadena.etapas.first()
    val hijos = remember(cadena, seleccionado) { hijosDe(cadena.etapas, seleccionado) }
    val colorDe: (Int) -> Color = { id -> colorDeTipo(tipoParaColor(tipos[id]) ?: "") }
    val tiposSeleccionado = tipos[seleccionado.id].orEmpty()
    val tipoPrincipal = tipoParaColor(tiposSeleccionado)
    val color = colorDe(seleccionado.id)

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 10.dp, end = 10.dp, top = 16.dp, bottom = 24.dp)
    ) {
        // ----- Panel 1: cadena completa para elegir una etapa -----
        PanelMadera(
            marco = R.drawable.marco_cadena,
            margenLateral = 0.dp,
            respiro = 6.dp,
            grosorMarco = GROSOR_MARCO_CADENA,
            corteVertical = CORTE_MARCO_CADENA
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 28.dp, end = 28.dp, top = 4.dp)
                ) {
                    PestanaMetodo(texto = "Cadena", icono = iconoMetodo(null))
                    Spacer(Modifier.weight(1f))
                    if (tipoPrincipal != null) {
                        ChipTipoEvolucion(tipoPrincipal, tiposInfo[tipoPrincipal], prefijo = "Tipo: ")
                    }
                }
                Spacer(Modifier.height(10.dp))
                CadenaSeleccionable(
                    cadena = cadena,
                    seleccionadoId = seleccionado.id,
                    colorDe = colorDe,
                    onSeleccionar = { seleccionadoId = it.id }
                )
            }
        }

        // ----- Panel 2: datos de la etapa elegida -----
        PanelMadera(
            marco = R.drawable.marco_cadena,
            margenLateral = 0.dp,
            respiro = 8.dp,
            grosorMarco = GROSOR_MARCO_CADENA,
            corteVertical = CORTE_MARCO_CADENA
        ) {
            // Margen arriba para no chocar con las hojas de las esquinas
            Column(modifier = Modifier.padding(top = 18.dp)) {
                Row {
                    TarjetaPokemonGrande(
                        etapa = seleccionado,
                        color = color,
                        tipos = tiposSeleccionado,
                        tiposInfo = tiposInfo,
                        modifier = Modifier.weight(0.42f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(0.58f)
                    ) {
                        PestanaMetodo(texto = "Evoluciona a", icono = Icons.Rounded.ArrowUpward)
                        if (hijos.isEmpty()) {
                            FilaEtapaFinal()
                        } else {
                            hijos.take(3).forEach { hijo ->
                                FilaSiguiente(
                                    etapa = hijo,
                                    color = colorDe(hijo.id),
                                    onClick = { seleccionadoId = hijo.id }
                                )
                            }
                            if (hijos.size > 3) {
                                Text(
                                    text = "+${hijos.size - 3} más en la cadena",
                                    color = GrisDatoEvo,
                                    fontFamily = FuenteMadera,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            PestanaMetodo(texto = "Requisitos", icono = Icons.Rounded.Checklist)
                            hijos.take(3).forEach { hijo ->
                                FilaRequisito(etapa = hijo, mostrarNombre = hijos.size > 1)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                PestanaMetodo(texto = "Proceso de evolución", icono = Icons.AutoMirrored.Rounded.MenuBook)
                Spacer(Modifier.height(6.dp))
                CajaProceso(texto = textoProceso(cadena))

                Spacer(Modifier.height(16.dp))
                BotonEvolucionar(
                    habilitado = hijos.isNotEmpty(),
                    // Con varias ramas, evoluciona a una al azar (como sorpresa)
                    onClick = { animando = seleccionado to hijos.random() }
                )
                Spacer(Modifier.height(6.dp))
            }
        }
    }

    // Animación encima de todo; al terminar queda elegida la evolución
    animando?.let { (desde, hacia) ->
        AnimacionEvolucion(
            desde = desde,
            hacia = hacia,
            color = colorDe(hacia.id),
            onTerminar = {
                seleccionadoId = hacia.id
                animando = null
            }
        )
    }
}

// Cadena con medallones que se pueden tocar.
// Cada columna usa el ancho que le toca, así los nombres se leen completos.
// Las ramas se apilan; al tocar la pila se abre una ventana con todas para elegir.
@Composable
private fun CadenaSeleccionable(
    cadena: CadenaEvolutiva,
    seleccionadoId: Int,
    colorDe: (Int) -> Color,
    onSeleccionar: (EtapaEvolucion) -> Unit
) {
    val fases = cadena.etapas.groupBy { it.fase }.toSortedMap().values.toList()
    // Ramas abiertas en la ventana (null = cerrada)
    var ramasAbiertas by remember { mutableStateOf<List<EtapaEvolucion>?>(null) }

    BoxWithConstraints(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        val cantidad = fases.size.coerceAtLeast(1)
        val extraPilas = fases
            .filter { it.size > 1 }
            .fold(0.dp) { total, grupo -> total + DESPLAZAMIENTO_PILA_DETALLE * (minOf(grupo.size, 4) - 1) }
        // Ancho de cada columna: el espacio se reparte entre las etapas
        val anchoColumna = ((maxWidth - ANCHO_FLECHA_DETALLE * (cantidad - 1) - extraPilas) / cantidad)
            .coerceIn(64.dp, 130.dp)
        val tamano = (anchoColumna - EXTRA_COLUMNA).coerceIn(44.dp, 58.dp)

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            fases.forEachIndexed { indice, grupo ->
                if (indice > 0) {
                    // La flecha queda a la altura del centro de los círculos
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.height(tamano)) {
                        FlechaCadena(
                            condicion = if (grupo.size == 1) grupo.first().condicion else "",
                            ancho = ANCHO_FLECHA_DETALLE
                        )
                    }
                }
                if (grupo.size == 1) {
                    MedallonElegible(
                        etapa = grupo.first(),
                        color = colorDe(grupo.first().id),
                        tamano = tamano,
                        ancho = anchoColumna,
                        elegido = grupo.first().id == seleccionadoId,
                        onClick = { onSeleccionar(grupo.first()) }
                    )
                } else {
                    // La elegida (si está en el grupo) va arriba de la pila
                    val ordenadas = grupo.sortedBy { if (it.id == seleccionadoId) 0 else 1 }
                    val elegidaEnGrupo = grupo.any { it.id == seleccionadoId }
                    Box(
                        modifier = Modifier
                            .drawBehind {
                                if (elegidaEnGrupo) {
                                    val radio = size.minDimension * 0.6f
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(DoradoEvo.copy(alpha = 0.6f), Color.Transparent),
                                            center = Offset(size.width * 0.35f, size.height * 0.35f),
                                            radius = radio
                                        ),
                                        radius = radio,
                                        center = Offset(size.width * 0.35f, size.height * 0.35f)
                                    )
                                }
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { ramasAbiertas = ordenadas }
                    ) {
                        PilaRamas(
                            etapas = ordenadas,
                            colorDe = colorDe,
                            tamano = tamano,
                            compacta = cantidad >= 3
                        )
                    }
                }
            }
        }
    }

    ramasAbiertas?.let { ramas ->
        SelectorRamas(
            etapas = ramas,
            seleccionadoId = seleccionadoId,
            colorDe = colorDe,
            onElegir = {
                onSeleccionar(it)
                ramasAbiertas = null
            },
            onCerrar = { ramasAbiertas = null }
        )
    }
}

// Medallón que se puede tocar; el elegido crece un poco y se resalta en dorado
@Composable
private fun MedallonElegible(
    etapa: EtapaEvolucion,
    color: Color,
    tamano: Dp,
    ancho: Dp,
    elegido: Boolean,
    onClick: () -> Unit
) {
    val escala by animateFloatAsState(if (elegido) 1.08f else 1f, label = "escalaElegido")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(ancho)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        CirculoPokemon(etapa.imagenUrl, etapa.nombre, color, tamano, resaltado = elegido)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(y = (-8).dp)
                .clip(RoundedCornerShape(50))
                .background(CelesteNombreEvo)
                .border(1.dp, CelesteBordeCaja, RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 1.dp)
        ) {
            Text(
                text = etapa.nombre.replaceFirstChar { it.uppercase() },
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "#%03d".format(etapa.id),
                color = GrisDatoEvo,
                fontFamily = FuenteMadera,
                fontSize = 10.sp,
                lineHeight = 11.sp
            )
        }
    }
}

// Ventana con todas las ramas: las tarjetas saltan hacia arriba una tras otra
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectorRamas(
    etapas: List<EtapaEvolucion>,
    seleccionadoId: Int,
    colorDe: (Int) -> Color,
    onElegir: (EtapaEvolucion) -> Unit,
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
                Spacer(Modifier.height(24.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    maxItemsInEachRow = 2
                ) {
                    etapas.forEachIndexed { indice, etapa ->
                        TarjetaElegir(
                            etapa = etapa,
                            color = colorDe(etapa.id),
                            elegida = etapa.id == seleccionadoId,
                            retrasoMs = indice * 80L,
                            onClick = { onElegir(etapa) }
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

// Tarjeta de una rama: salta desde abajo con rebote; la elegida tiene borde dorado grueso
@Composable
private fun TarjetaElegir(
    etapa: EtapaEvolucion,
    color: Color,
    elegida: Boolean,
    retrasoMs: Long,
    onClick: () -> Unit
) {
    val salto = remember { Animatable(1f) }       // 1 = abajo, 0 = en su lugar
    LaunchedEffect(Unit) {
        delay(retrasoMs)
        salto.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
    }
    val forma = RoundedCornerShape(20.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(150.dp)
            .graphicsLayer {
                translationY = salto.value * 260.dp.toPx()
                alpha = (1f - salto.value).coerceIn(0f, 1f)
            }
            .shadow(
                elevation = if (elegida) 18.dp else 6.dp,
                shape = forma,
                ambientColor = if (elegida) color else Color.Black,
                spotColor = if (elegida) color else Color.Black
            )
            .clip(forma)
            .background(Brush.verticalGradient(listOf(Color.White, CremaEvo)))
            .border(
                width = if (elegida) 3.dp else 2.dp,
                brush = Brush.verticalGradient(listOf(DoradoClaroEvo, DoradoBordeEvo)),
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        CirculoPokemon(etapa.imagenUrl, etapa.nombre, color, 100.dp, resaltado = elegida)
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
            color = GrisDatoEvo,
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
                    .background(Brush.verticalGradient(listOf(DoradoClaroEvo, DoradoEvo)))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

// Tarjeta grande del Pokémon elegido: degradado del tipo, burbujas, nombre y tipos
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TarjetaPokemonGrande(
    etapa: EtapaEvolucion,
    color: Color,
    tipos: List<String>,
    tiposInfo: Map<String, Tipo>,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(20.dp)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .shadow(6.dp, forma, ambientColor = color, spotColor = color)
                .clip(forma)
                .background(
                    Brush.verticalGradient(
                        listOf(lerp(color, Color.White, 0.55f), lerp(color, Color.White, 0.1f))
                    )
                )
                .border(3.dp, Color.White.copy(alpha = 0.9f), forma)
        ) {
            // Burbujas
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                listOf(
                    Triple(0.18f, 0.16f, 0.05f), Triple(0.28f, 0.08f, 0.03f),
                    Triple(0.86f, 0.30f, 0.04f), Triple(0.78f, 0.12f, 0.025f),
                    Triple(0.12f, 0.55f, 0.03f)
                ).forEach { (x, y, r) ->
                    drawCircle(Color.White.copy(alpha = 0.6f), radius = w * r, center = Offset(w * x, size.height * y))
                }
            }
            // La imagen cambia con fundido al elegir otra etapa
            AnimatedContent(
                targetState = etapa,
                transitionSpec = { (fadeIn() + scaleIn(initialScale = 0.85f)) togetherWith fadeOut() },
                label = "pokemonGrande",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
                    .fillMaxWidth(0.88f)
                    .aspectRatio(1f)
            ) { e ->
                AsyncImage(
                    model = e.imagenUrl,
                    contentDescription = e.nombre,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Nombre y número
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 6.dp, end = 6.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Text(
                    text = etapa.nombre.replaceFirstChar { it.uppercase() },
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "#%03d".format(etapa.id),
                    color = GrisDatoEvo,
                    fontFamily = FuenteMadera,
                    fontSize = 11.sp,
                    lineHeight = 12.sp
                )
            }
        }
        if (tipos.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tipos.forEach { tipo -> ChipTipoEvolucion(tipo, tiposInfo[tipo]) }
            }
        }
    }
}

// Chip de tipo claro con el ícono de la API ("Tipo: Agua")
@Composable
private fun ChipTipoEvolucion(tipo: String, info: Tipo?, prefijo: String = "") {
    val color = colorDeTipo(tipo)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.92f))
            .border(1.dp, lerp(color, Color.White, 0.3f), RoundedCornerShape(50))
            .padding(start = 3.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
    ) {
        if (info != null) {
            AsyncImage(
                model = info.iconoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = prefijo + (info?.nombreEs ?: Traductor.tipoEs(tipo)),
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

// Siguiente evolución: mini medallón, nombre y número (tocarla la elige)
@Composable
private fun FilaSiguiente(etapa: EtapaEvolucion, color: Color, onClick: () -> Unit) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, CafeMadera.copy(alpha = 0.12f), forma)
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        CirculoPokemon(etapa.imagenUrl, etapa.nombre, color, 38.dp)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = etapa.nombre.replaceFirstChar { it.uppercase() },
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "#%03d".format(etapa.id),
                color = GrisDatoEvo,
                fontFamily = FuenteMadera,
                fontSize = 11.sp
            )
        }
    }
}

// Mensaje cuando la etapa elegida es la última de su cadena
@Composable
private fun FilaEtapaFinal() {
    val forma = RoundedCornerShape(14.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, CafeMadera.copy(alpha = 0.12f), forma)
            .padding(10.dp)
    ) {
        Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFE0A526), modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = "Es la última etapa de su cadena",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 13.sp
        )
    }
}

// Requisito de una evolución, con ícono según el método (sin filas vacías)
@Composable
private fun FilaRequisito(etapa: EtapaEvolucion, mostrarNombre: Boolean) {
    val metodo = metodoDeCondicion(etapa.condicion)
    val (etiqueta, valor) = requisitoDe(metodo, etapa.condicion)
    val nombre = etapa.nombre.replaceFirstChar { it.uppercase() }
    val forma = RoundedCornerShape(12.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, CafeMadera.copy(alpha = 0.12f), forma)
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        IconoRequisito(metodo)
        Spacer(Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (mostrarNombre) "$nombre · $etiqueta" else etiqueta,
                color = GrisDatoEvo,
                fontFamily = FuenteMadera,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = valor,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
        }
    }
}

// Círculo con el ícono del método ("Nv." para nivel, como en el mockup)
@Composable
private fun IconoRequisito(metodo: MetodoEvolucion) {
    if (metodo == MetodoEvolucion.NIVEL) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CafeMadera)
        ) {
            Text("Nv.", color = Color.White, fontFamily = FuenteMadera, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    } else {
        val color = colorMetodo(metodo)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(1.dp, color.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(iconoMetodo(metodo), contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
    }
}

// Caja celeste con el proceso escrito
@Composable
private fun CajaProceso(texto: AnnotatedString) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(CelesteCaja)
            .border(1.dp, CelesteBordeCaja, forma)
            .padding(10.dp)
    ) {
        Icon(Icons.Rounded.Info, contentDescription = null, tint = AzulInfo, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = texto,
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

// Botón verde "Evolucionar" con brillo; gris "Etapa final" si no hay evolución
@Composable
private fun BotonEvolucionar(habilitado: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionado) 0.96f else 1f, label = "escalaEvolucionar")

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .shadow(if (habilitado) 6.dp else 1.dp, forma)
            .clip(forma)
            .background(
                if (habilitado) Brush.verticalGradient(listOf(Color(0xFF7BD857), Color(0xFF35A82C), Color(0xFF268A20)))
                else Brush.verticalGradient(listOf(Color(0xFFD8D2C8), Color(0xFFB9B1A4)))
            )
            .border(2.dp, if (habilitado) Color(0xFF1E6E1A) else Color(0xFF9A9084), forma)
            .clickable(enabled = habilitado, interactionSource = interaccion, indication = null, onClick = onClick)
    ) {
        // Brillo superior
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(size.height / 2f, size.height * 0.12f),
                size = Size(size.width - size.height, size.height * 0.28f),
                cornerRadius = CornerRadius(size.height)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (habilitado) "Evolucionar" else "Etapa final",
                color = Color.White,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ----- Ayudas -----

// Evoluciones directas de una etapa: las que siguen en la lista con la fase siguiente,
// hasta volver a su fase o menos (la lista viene en el orden del árbol)
private fun hijosDe(etapas: List<EtapaEvolucion>, padre: EtapaEvolucion): List<EtapaEvolucion> {
    val inicio = etapas.indexOf(padre)
    val hijos = mutableListOf<EtapaEvolucion>()
    for (j in inicio + 1 until etapas.size) {
        val e = etapas[j]
        if (e.fase <= padre.fase) break
        if (e.fase == padre.fase + 1) hijos += e
    }
    return hijos
}

// Etapa anterior: la más cercana hacia atrás con una fase menos
private fun padreDe(etapas: List<EtapaEvolucion>, hijo: EtapaEvolucion): EtapaEvolucion? {
    val inicio = etapas.indexOf(hijo)
    for (j in inicio - 1 downTo 0) {
        if (etapas[j].fase == hijo.fase - 1) return etapas[j]
    }
    return null
}

// Método a partir de la condición ya traducida ("Nv. 16", "Piedra Trueno"...)
private fun metodoDeCondicion(condicion: String): MetodoEvolucion = when {
    condicion.startsWith("Nv.") -> MetodoEvolucion.NIVEL
    condicion.startsWith("Piedra") -> MetodoEvolucion.PIEDRA
    condicion.startsWith("Intercambio") -> MetodoEvolucion.INTERCAMBIO
    condicion.startsWith("Amistad") -> MetodoEvolucion.AMISTAD
    else -> MetodoEvolucion.OTROS
}

// (etiqueta, valor) de cada requisito
private fun requisitoDe(metodo: MetodoEvolucion, condicion: String): Pair<String, String> = when (metodo) {
    MetodoEvolucion.NIVEL -> "Nivel mínimo" to condicion
    MetodoEvolucion.PIEDRA -> "Objeto" to condicion
    MetodoEvolucion.INTERCAMBIO ->
        "Intercambio" to if (condicion == "Intercambio") "Sí" else condicion.removePrefix("Intercambio con ")
    MetodoEvolucion.AMISTAD -> "Amistad" to condicion.replace("Amistad", "Alta").trim()
    MetodoEvolucion.OTROS -> "Condición" to condicion.ifBlank { "Especial" }
}

// "al alcanzar el nivel 16", "con la Piedra Trueno"...
private fun fraseDe(condicion: String): String = when (metodoDeCondicion(condicion)) {
    MetodoEvolucion.NIVEL -> "al alcanzar el nivel ${condicion.removePrefix("Nv. ")}"
    MetodoEvolucion.PIEDRA -> "con la $condicion"
    MetodoEvolucion.INTERCAMBIO ->
        if (condicion == "Intercambio") "al ser intercambiado"
        else "al ser intercambiado con ${condicion.removePrefix("Intercambio con ")}"
    MetodoEvolucion.AMISTAD -> "con amistad alta" + condicion.removePrefix("Amistad")
    MetodoEvolucion.OTROS ->
        if (condicion.isBlank()) "de una forma especial" else "con esta condición: $condicion"
}

// Proceso completo con los nombres en negrita
private fun textoProceso(cadena: CadenaEvolutiva): AnnotatedString = buildAnnotatedString {
    val etapas = cadena.etapas
    if (etapas.size <= 1) {
        append("Este Pokémon no evoluciona.")
        return@buildAnnotatedString
    }
    val negrita = SpanStyle(fontWeight = FontWeight.Bold)
    etapas.drop(1).forEachIndexed { i, hijo ->
        val padre = padreDe(etapas, hijo) ?: return@forEachIndexed
        if (i > 0) append(" ")
        withStyle(negrita) { append(padre.nombre.replaceFirstChar { it.uppercase() }) }
        append(" evoluciona a ")
        withStyle(negrita) { append(hijo.nombre.replaceFirstChar { it.uppercase() }) }
        append(" ${fraseDe(hijo.condicion)}.")
    }
}