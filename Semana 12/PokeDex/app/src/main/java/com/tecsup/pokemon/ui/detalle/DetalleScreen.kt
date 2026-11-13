package com.tecsup.pokemon.ui.detalle

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Carta
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.BarraEstadistica
import com.tecsup.pokemon.ui.components.BotonVolver
import com.tecsup.pokemon.ui.components.CadenaEvolucion
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.CarruselCartas
import com.tecsup.pokemon.ui.components.DialogoQuitarFavorito
import com.tecsup.pokemon.ui.components.ErrorView
import com.tecsup.pokemon.ui.components.EstadoCartas
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LetreroSeccion
import com.tecsup.pokemon.ui.components.LluviaCorazones
import com.tecsup.pokemon.ui.components.PanelMadera
import com.tecsup.pokemon.ui.components.Pokebola
import com.tecsup.pokemon.ui.components.VisorCarta
import com.tecsup.pokemon.ui.components.colorDeTipo
import com.tecsup.pokemon.ui.components.desvanecerBordes
import com.tecsup.pokemon.util.Traductor

private val GrisDato = Color(0xFF8A7A6A)
private val CremaPanel = Color(0xFFFFF4DF)
private val DoradoPildora = Color(0xFFF5C451)
private val DoradoClaro = Color(0xFFFFE7A0)
private val DoradoBorde = Color(0xFFD9A43A)
private val DoradoCita = Color(0xFFE0A526)
private val RojoCorazon = Color(0xFFE53935)
private val AzulMacho = Color(0xFF3D7BD9)
private val RosaHembra = Color(0xFFE0569B)

// Grosor de los marcos de madera (más chico = más fino y nítido)
private const val GROSOR_MARCO = 0.65f

// Ancho de la columna del Pokémon en el panel principal (la línea divisoria va en su borde)
private const val PESO_IZQUIERDA = 0.46f

// Ícono de cada estadística
private fun iconoEstadistica(clave: String): ImageVector = when (clave) {
    "hp" -> Icons.Rounded.Favorite
    "attack" -> Icons.Rounded.FitnessCenter
    "defense" -> Icons.Rounded.Shield
    "special-attack" -> Icons.Rounded.AutoAwesome
    "special-defense" -> Icons.Rounded.VerifiedUser
    "speed" -> Icons.Rounded.Air
    else -> Icons.Rounded.BarChart
}

// Tipo principal: el de la lista desde la que se entró, si el Pokémon lo tiene;
// si no (por ejemplo, desde Favoritos), su primer tipo
private fun tipoPrincipalDe(detalle: ItemDetalle, tipoOrigen: String?): String =
    tipoOrigen?.takeIf { it in detalle.tipos } ?: detalle.tipos.firstOrNull() ?: "normal"

@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    onVolver: () -> Unit,
    onEtapaClick: (Int) -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val esFavorito by viewModel.esFavorito.collectAsStateWithLifecycle()
    val cartas by viewModel.cartas.collectAsStateWithLifecycle()
    val tiposInfo by viewModel.tiposInfo.collectAsStateWithLifecycle()

    var disparoCorazones by remember { mutableIntStateOf(0) }
    var cartaSeleccionada by remember { mutableStateOf<Carta?>(null) }
    var confirmarQuitar by remember { mutableStateOf(false) }

    FondoInicio {
        Column(modifier = Modifier.fillMaxSize()) {
            // ----- Cabecera: volver, letrero que llena el espacio y favorito -----
            // (en pantalla completa la barra de estado no ocupa espacio, por eso el margen fijo)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 36.dp, bottom = 4.dp)
            ) {
                BotonVolver(onClick = onVolver)
                Spacer(Modifier.width(8.dp))
                LetreroSeccion(
                    texto = "Detalle del Pokémon",
                    alto = 62.dp,
                    tamanoTexto = 17.sp,
                    icono = { Pokebola(apertura = 0f, brillo = 0f, modifier = Modifier.size(22.dp)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                BotonVolver(
                    icono = if (esFavorito) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    descripcion = "Favorito",
                    tinte = if (esFavorito) RojoCorazon else CafeMadera,
                    latido = esFavorito,
                    onClick = {
                        if (esFavorito) {
                            // Quitar: primero se pregunta
                            confirmarQuitar = true
                        } else {
                            // Marcar: cae la lluvia de corazones
                            disparoCorazones++
                            viewModel.alternarFavorito()
                        }
                    }
                )
            }

            // Contenido: se desvanece suavemente al pasar por debajo de la cabecera
            Box(
                modifier = Modifier
                    .weight(1f)
                    .desvanecerBordes()
            ) {
                when (val e = estado) {
                    is UiState.Cargando -> Cargando("Cargando detalle...")
                    is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::cargar)
                    is UiState.Exito -> ContenidoDetalle(
                        detalle = e.datos,
                        tipoOrigen = viewModel.tipoOrigen,
                        tiposInfo = tiposInfo,
                        cartas = cartas,
                        onEtapaClick = onEtapaClick,
                        onCartaClick = { cartaSeleccionada = it },
                        onReintentarCartas = { viewModel.cargarCartas(e.datos.nombre) }
                    )
                }
            }
        }

        // Lluvia de corazones encima de todo (no bloquea los toques)
        LluviaCorazones(disparo = disparoCorazones)

        // Carta en grande
        VisorCarta(carta = cartaSeleccionada, onCerrar = { cartaSeleccionada = null })

        // Confirmación para quitar de favoritos
        val detalleActual = (estado as? UiState.Exito)?.datos
        if (confirmarQuitar && detalleActual != null) {
            DialogoQuitarFavorito(
                nombre = detalleActual.nombre,
                imagenUrl = detalleActual.imagenUrl,
                color = colorDeTipo(tipoPrincipalDe(detalleActual, viewModel.tipoOrigen)),
                onConfirmar = {
                    confirmarQuitar = false
                    viewModel.alternarFavorito()
                },
                onCancelar = { confirmarQuitar = false }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContenidoDetalle(
    detalle: ItemDetalle,
    tipoOrigen: String?,
    tiposInfo: Map<String, Tipo>,
    cartas: EstadoCartas,
    onEtapaClick: (Int) -> Unit,
    onCartaClick: (Carta) -> Unit,
    onReintentarCartas: () -> Unit
) {
    val tipoPrincipal = tipoPrincipalDe(detalle, tipoOrigen)
    val tiposOrdenados = detalle.tipos.sortedBy { if (it == tipoPrincipal) 0 else 1 }
    val color = colorDeTipo(tipoPrincipal)
    val colorLinea = lerp(color, Color.Black, 0.15f).copy(alpha = 0.55f)
    val nombre = detalle.nombre.replaceFirstChar { it.uppercase() }

    // Las barras se llenan al entrar
    var animarBarras by remember { mutableStateOf(false) }
    LaunchedEffect(detalle.id) { animarBarras = true }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            // Margen arriba: sin hacer scroll, el marco no queda dentro del desvanecido
            .padding(top = 18.dp, bottom = 24.dp)
            .navigationBarsPadding()
    ) {
        // ----- Panel principal (sin respiro: la imagen llega hasta la madera; Pokébola del marco con luz) -----
        PanelMadera(
            marco = R.drawable.panel_principal,
            respiro = 0.dp,
            brilloInferior = true,
            grosorMarco = GROSOR_MARCO
        ) {
            Column {
                // Línea divisoria vertical a todo el largo, dibujada ENCIMA del contenido
                // (así el recuadro de color no la tapa)
                Row(
                    modifier = Modifier.drawWithContent {
                        drawContent()
                        val separacion = 10.dp.toPx()
                        val x = (size.width - separacion) * PESO_IZQUIERDA - 1.dp.toPx()
                        drawLine(
                            color = colorLinea,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                ) {
                    // Izquierda: imagen pegada al marco con la curva del número, nombre y tipos
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(PESO_IZQUIERDA)
                    ) {
                        ImagenPegadaAlMarco(
                            imagenUrl = detalle.imagenUrl,
                            numero = detalle.id,
                            nombre = nombre,
                            color = color
                        )
                        Text(
                            text = nombre,
                            color = CafeMadera,
                            fontFamily = FuenteMadera,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        // Tipos uno al lado del otro (el de origen primero)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, end = 6.dp, bottom = 6.dp)
                        ) {
                            tiposOrdenados.forEach { tipo ->
                                ChipTipo(tipo = tipo, iconoUrl = tiposInfo[tipo]?.iconoUrl)
                            }
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    // Derecha: altura, peso y habilidades
                    Column(
                        modifier = Modifier
                            .weight(1f - PESO_IZQUIERDA)
                            .padding(top = 24.dp, end = 8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            TarjetaDato(Icons.Rounded.Straighten, "Altura", "%.1f".format(detalle.alturaM), "m", Modifier.weight(1f))
                            TarjetaDato(Icons.Rounded.MonitorWeight, "Peso", "%.1f".format(detalle.pesoKg), "kg", Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(10.dp))
                        TarjetaSeccion(icono = Icons.Rounded.Star, titulo = "Habilidades") {
                            Text(
                                text = detalle.habilidades.joinToString(", "),
                                color = CafeMadera,
                                fontFamily = FuenteMadera,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Abajo, todo el ancho: estadísticas
                Box(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
                    TarjetaSeccion(
                        icono = Icons.Rounded.BarChart,
                        titulo = "Estadísticas",
                        adornoBarras = color
                    ) {
                        val filas = Traductor.ordenEstadisticas
                            .mapNotNull { clave -> detalle.stats[clave]?.let { clave to it } }
                        Column {
                            filas.forEachIndexed { indice, (clave, valor) ->
                                BarraEstadistica(
                                    icono = iconoEstadistica(clave),
                                    nombre = Traductor.estadisticaEs(clave),
                                    valor = valor,
                                    color = color,
                                    animar = animarBarras,
                                    retrasoMs = indice * 80,
                                    mostrarDivisor = indice < filas.lastIndex
                                )
                            }
                        }
                    }
                }
            }
        }

        // ----- Información (con marco; el letrero con luz se monta encima) -----
        Box(modifier = Modifier.fillMaxWidth()) {
            PanelMadera(
                marco = R.drawable.panel_info,
                extraArriba = 26.dp,
                grosorMarco = GROSOR_MARCO,
                modifier = Modifier.padding(top = 22.dp)
            ) {
                Column {
                    Row {
                        // Descripción en recuadro con comillas, brillo y silueta del Pokémon
                        CajaCita(
                            texto = detalle.descripcion.ifBlank { "Sin descripción disponible." },
                            imagenUrl = detalle.imagenUrl,
                            modifier = Modifier.weight(0.62f)
                        )
                        Spacer(Modifier.width(10.dp))
                        // Categoría y género
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(0.38f)
                        ) {
                            if (detalle.categoria.isNotBlank()) {
                                BloqueDato(Icons.Rounded.Style, "Categoría") {
                                    Text(
                                        text = detalle.categoria,
                                        color = CafeMadera,
                                        fontFamily = FuenteMadera,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            BloqueDato(Icons.Rounded.Wc, "Género") {
                                val genero = detalle.genero
                                if (genero == null) {
                                    Text(
                                        text = "Sin género",
                                        color = CafeMadera,
                                        fontFamily = FuenteMadera,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                } else {
                                    // Lado a lado si entran; si no, la segunda baja (nunca se esconde)
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        PildoraGenero("♂ ${porcentaje(genero.first)}", AzulMacho)
                                        PildoraGenero("♀ ${porcentaje(genero.second)}", RosaHembra)
                                    }
                                }
                            }
                        }
                    }

                    if (detalle.debilidades.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        // Separadas de las plantas de las esquinas inferiores
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(start = 16.dp, end = 30.dp, bottom = 14.dp)
                        ) {
                            PestanaDorada(Icons.Rounded.Shield, "Debilidades", tamano = 13)
                            detalle.debilidades.forEach { debilidad ->
                                ChipTipo(
                                    tipo = debilidad.tipo,
                                    iconoUrl = tiposInfo[debilidad.tipo]?.iconoUrl,
                                    extra = "x${debilidad.multiplicador.toInt()}"
                                )
                            }
                        }
                    }
                }
            }
            LetreroSeccion(
                texto = "Información",
                destellos = true,
                icono = { Icon(Icons.Rounded.Info, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // ----- Evolución: medallones flotando sobre el fondo -----
        Spacer(Modifier.height(10.dp))
        LetreroSeccion(
            texto = "Evolución",
            icono = { Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(20.dp)) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (detalle.evolucion.size <= 1) {
            Text(
                text = "Este Pokémon no evoluciona",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(CremaPanel.copy(alpha = 0.93f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            CadenaEvolucion(
                etapas = detalle.evolucion,
                idActual = detalle.id,
                color = color,
                onEtapaClick = onEtapaClick,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }

        // ----- Cartas: sin marco, solo las cartas brillando sobre el fondo -----
        Spacer(Modifier.height(8.dp))
        LetreroSeccion(
            texto = "Cartas",
            icono = { Icon(Icons.Rounded.Style, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(20.dp)) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        CarruselCartas(
            estado = cartas,
            onCartaClick = onCartaClick,
            onReintentar = onReintentarCartas
        )
    }
}

// Imagen del Pokémon pegada al marco: degradado del tipo, Pokébola de fondo, sombra
// y curva crema abajo con el número. Su línea se une a la divisoria vertical de la derecha.
@Composable
private fun ImagenPegadaAlMarco(imagenUrl: String, numero: Int, nombre: String, color: Color) {
    val colorLinea = lerp(color, Color.Black, 0.15f)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
    ) {
        val alto = maxHeight
        val zonaCurva = alto * 0.2f            // alto total de la curva
        val parteBaja = zonaCurva * 0.45f      // franja crema plana a los lados

        // Fondo con el degradado del tipo
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(lerp(color, Color.White, 0.55f), lerp(color, Color.White, 0.05f))
                    )
                )
        )

        // Pokébola de fondo
        Icon(
            imageVector = Icons.Outlined.CatchingPokemon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -zonaCurva / 3)
                .fillMaxSize(0.82f)
        )

        // Sombra a los pies del Pokémon
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = zonaCurva + 2.dp)
                .size(width = 80.dp, height = 12.dp)
        ) {
            drawOval(Color.Black.copy(alpha = 0.22f))
        }

        // Pokémon
        AsyncImage(
            model = imagenUrl,
            contentDescription = nombre,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -zonaCurva / 3)
                .fillMaxSize(0.8f)
        )

        // Curva crema, esquina redondeada y línea de la curva
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val grosorLinea = 2.dp.toPx()
            val yArriba = h - zonaCurva.toPx()
            val yBase = h - parteBaja.toPx()
            val anchoLomo = w * 0.5f
            val izquierda = (w - anchoLomo) / 2f
            val derecha = izquierda + anchoLomo
            val curva = anchoLomo * 0.22f
            val radioEsquina = 22.dp.toPx()

            // Relleno crema: curva del número y esquina inferior derecha redondeada
            val relleno = Path().apply {
                moveTo(0f, yBase)
                lineTo(izquierda - curva, yBase)
                cubicTo(izquierda, yBase, izquierda, yArriba, izquierda + curva, yArriba)
                lineTo(derecha - curva, yArriba)
                cubicTo(derecha, yArriba, derecha, yBase, derecha + curva, yBase)
                lineTo(w - radioEsquina, yBase)
                quadraticTo(w, yBase, w, yBase - radioEsquina)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(relleno, CremaPanel)

            // Línea de la curva: termina subiendo por la esquina hasta unirse a la divisoria vertical
            val linea = Path().apply {
                moveTo(0f, yBase)
                lineTo(izquierda - curva, yBase)
                cubicTo(izquierda, yBase, izquierda, yArriba, izquierda + curva, yArriba)
                lineTo(derecha - curva, yArriba)
                cubicTo(derecha, yArriba, derecha, yBase, derecha + curva, yBase)
                lineTo(w - radioEsquina, yBase)
                quadraticTo(w - grosorLinea / 2f, yBase, w - grosorLinea / 2f, yBase - radioEsquina)
            }
            drawPath(linea, colorLinea.copy(alpha = 0.55f), style = Stroke(width = grosorLinea))
        }

        // Número dentro de la curva
        Text(
            text = "#%03d".format(numero),
            color = GrisDato,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = parteBaja * 0.15f)
        )
    }
}

// Pestaña dorada con ícono y texto (títulos de tarjeta y Debilidades)
@Composable
private fun PestanaDorada(icono: ImageVector, titulo: String, tamano: Int = 15, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(Brush.verticalGradient(listOf(DoradoClaro, DoradoPildora)))
            .border(1.dp, DoradoBorde, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(icono, contentDescription = null, tint = CafeMadera, modifier = Modifier.size((tamano + 1).dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = titulo,
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = tamano.sp
        )
    }
}

// Tarjeta con pestaña dorada encima (Habilidades, Estadísticas)
@Composable
private fun TarjetaSeccion(
    icono: ImageVector,
    titulo: String,
    adornoBarras: Color? = null,
    contenido: @Composable () -> Unit
) {
    val forma = RoundedCornerShape(16.dp)
    Box(modifier = Modifier.fillMaxWidth()) {
        // Tarjeta
        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .shadow(2.dp, forma)
                .clip(forma)
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.9f), Color(0xFFFFF6E6))))
                .border(1.dp, CafeMadera.copy(alpha = 0.12f), forma)
                .padding(start = 10.dp, end = 10.dp, top = 22.dp, bottom = 8.dp)
        ) {
            contenido()
        }

        // Adorno de barritas arriba a la derecha
        if (adornoBarras != null) {
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 22.dp, end = 14.dp)
                    .size(width = 34.dp, height = 14.dp)
            ) {
                val alturas = listOf(0.4f, 0.75f, 0.55f, 1f, 0.65f)
                val ancho = size.width / (alturas.size * 2 - 1)
                alturas.forEachIndexed { i, a ->
                    drawRoundRect(
                        color = adornoBarras.copy(alpha = 0.35f),
                        topLeft = Offset(i * ancho * 2, size.height * (1 - a)),
                        size = Size(ancho, size.height * a),
                        cornerRadius = CornerRadius(ancho / 2)
                    )
                }
            }
        }

        // Pestaña dorada
        PestanaDorada(icono = icono, titulo = titulo, modifier = Modifier.padding(start = 6.dp))
    }
}

// Tarjetita de dato (altura y peso): ícono en círculo dorado, título,
// número en negrita y unidad más chica al lado (así entra completo)
@Composable
private fun TarjetaDato(
    icono: ImageVector,
    titulo: String,
    numero: String,
    unidad: String,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(2.dp, forma)
            .clip(forma)
            .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.9f), Color(0xFFFFEFD2))))
            .border(1.dp, CafeMadera.copy(alpha = 0.12f), forma)
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        // Ícono dentro de un círculo dorado
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(DoradoClaro, DoradoPildora)))
                .border(1.dp, DoradoBorde, CircleShape)
        ) {
            Icon(icono, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(13.dp))
        }
        Spacer(Modifier.width(4.dp))
        Column {
            Text(titulo, color = CafeMadera, fontFamily = FuenteMadera, fontSize = 10.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = numero,
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = " $unidad",
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontSize = 10.sp,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }
    }
}

// Dato con ícono y título pequeño; el valor se pasa como contenido
@Composable
private fun BloqueDato(icono: ImageVector, titulo: String, valor: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icono, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Column {
            Text(titulo, color = GrisDato, fontFamily = FuenteMadera, fontSize = 11.sp)
            valor()
        }
    }
}

// Descripción con comillas doradas, brillo alrededor, destellos y silueta visible del Pokémon
@Composable
private fun CajaCita(texto: String, imagenUrl: String, modifier: Modifier = Modifier) {
    val forma = RoundedCornerShape(14.dp)
    val transicion = rememberInfiniteTransition(label = "destellos")
    val titileo by transicion.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "titileo"
    )

    Box(
        modifier = modifier
            // Brillo dorado suave alrededor
            .drawBehind {
                for (i in 1..3) {
                    val extra = i * 2.dp.toPx()
                    drawRoundRect(
                        color = DoradoCita.copy(alpha = 0.07f),
                        topLeft = Offset(-extra, -extra),
                        size = Size(size.width + extra * 2, size.height + extra * 2),
                        cornerRadius = CornerRadius(14.dp.toPx() + extra)
                    )
                }
            }
            .clip(forma)
            .background(Brush.verticalGradient(listOf(Color(0xFFFFFDF5), Color(0xFFFFF1D6))))
            .border(1.dp, DoradoCita.copy(alpha = 0.6f), forma)
    ) {
        // Silueta del Pokémon, sin contorno, más visible
        AsyncImage(
            model = imagenUrl,
            contentDescription = null,
            colorFilter = ColorFilter.tint(CafeMadera.copy(alpha = 0.2f)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 4.dp)
                .size(78.dp)
        )
        // Destellos que titilan
        Text(
            text = "✦",
            color = DoradoCita.copy(alpha = titileo),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 6.dp, end = 8.dp)
        )
        Text(
            text = "✦",
            color = DoradoCita.copy(alpha = (1.25f - titileo).coerceIn(0f, 1f)),
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 18.dp)
        )

        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(
                text = "“",
                color = DoradoCita,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 22.sp
            )
            Text(
                text = texto,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Text(
                text = "”",
                color = DoradoCita,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Píldora de género compacta (♂ azul, ♀ rosa)
@Composable
private fun PildoraGenero(texto: String, color: Color) {
    Text(
        text = texto,
        color = color,
        fontFamily = FuenteMadera,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        maxLines = 1,
        softWrap = false,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

// Chip de tipo en español con su ícono y color; "extra" para el x2 / x4
@Composable
private fun ChipTipo(tipo: String, iconoUrl: String?, extra: String? = null) {
    val color = colorDeTipo(tipo)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(start = 3.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
    ) {
        if (iconoUrl != null) {
            AsyncImage(
                model = iconoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(3.dp))
        }
        Text(
            text = if (extra != null) "${Traductor.tipoEs(tipo)} $extra" else Traductor.tipoEs(tipo),
            color = Color.White,
            fontFamily = FuenteMadera,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

// 87.5 -> "87.5%"; 50.0 -> "50%"
private fun porcentaje(valor: Double): String =
    if (valor % 1.0 == 0.0) "${valor.toInt()}%" else "%.1f%%".format(valor)