package com.tecsup.pokemon.ui.favoritos

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.AparecerSuave
import com.tecsup.pokemon.ui.components.BotonFiltroMadera
import com.tecsup.pokemon.ui.components.BotonVolver
import com.tecsup.pokemon.ui.components.BuscadorMadera
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LetreroSeccion
import com.tecsup.pokemon.ui.components.LocalRellenoBarra
import com.tecsup.pokemon.ui.components.PestanaMetodo
import com.tecsup.pokemon.ui.components.TarjetaPokemon
import com.tecsup.pokemon.ui.components.TipoChip
import com.tecsup.pokemon.ui.components.VacioView
import com.tecsup.pokemon.ui.components.colorDeTipo
import com.tecsup.pokemon.ui.components.desvanecerBordes
import com.tecsup.pokemon.ui.components.tipoParaColor
import com.tecsup.pokemon.util.Traductor
import kotlinx.coroutines.delay

private val RojoCorazon = Color(0xFFE53935)
private val DoradoClaroChip = Color(0xFFFFE7A0)
private val DoradoChip = Color(0xFFF5C451)
private val DoradoBordeChip = Color(0xFFD9A43A)
private val CremaChip = Color(0xFFFFF8EA)
private val CremaTira = Color(0xFFFFF4DF)
private val GrisResumen = Color(0xFF7A6A5A)
private val FondoBarraResumen = Color(0xFFECE4D8)

// Pestaña Favoritos: resumen de la colección y tarjetas de los Pokémon marcados,
// con buscador, filtro por tipo, orden y opción para compartir (póster o texto).
// onItemClick recibe el id y el tipo con el que se abre el Detalle (el filtrado, si hay).
// onVolver lleva a Inicio (botón "Explorar Pokémon" cuando no hay favoritos).
@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    onItemClick: (Int, String?) -> Unit,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val tipoFiltro by viewModel.tipo.collectAsStateWithLifecycle()
    val orden by viewModel.orden.collectAsStateWithLifecycle()
    val tiposDisponibles by viewModel.tiposDisponibles.collectAsStateWithLifecycle()
    val tiposInfo by viewModel.tiposInfo.collectAsStateWithLifecycle()
    val total by viewModel.total.collectAsStateWithLifecycle()
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val conteoTipos by viewModel.conteoTipos.collectAsStateWithLifecycle()
    val quitado by viewModel.quitado.collectAsStateWithLifecycle()
    var menuAbierto by remember { mutableStateOf(false) }
    // Ventana con la vista previa del póster
    var mostrarPoster by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // El aviso de "Deshacer" se oculta solo
    LaunchedEffect(quitado) {
        if (quitado != null) {
            delay(3500)
            viewModel.ocultarAviso()
        }
    }

    // Si el tipo elegido ya no tiene favoritos, vuelve a "Todos"
    LaunchedEffect(tiposDisponibles) {
        if (tipoFiltro != null && tipoFiltro !in tiposDisponibles) viewModel.cambiarTipo(null)
    }

    // Comparte la lista de favoritos como texto
    fun compartirTexto() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textoCompartir(todos, tiposInfo))
        }
        context.startActivity(Intent.createChooser(intent, "Compartir favoritos"))
    }

    FondoInicio {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ----- Cabecera: letrero centrado + compartir -----
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 36.dp, bottom = 4.dp)
                ) {
                    // Mismo ancho que el botón, para que el letrero quede centrado
                    Spacer(Modifier.width(48.dp))
                    LetreroSeccion(
                        texto = "Favoritos",
                        alto = 62.dp,
                        tamanoTexto = 20.sp,
                        icono = {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(22.dp))
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    if (total > 0) {
                        // Abre la vista previa del póster (desde ahí se elige imagen o texto)
                        BotonVolver(
                            onClick = { mostrarPoster = true },
                            icono = Icons.Rounded.Share,
                            descripcion = "Compartir favoritos"
                        )
                    } else {
                        Spacer(Modifier.width(40.dp))
                    }
                }

                // ----- Buscador + Filtrar -----
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    BuscadorMadera(
                        valor = busqueda,
                        onValorChange = viewModel::onBusquedaChange,
                        placeholder = "Nombre o N°",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Box {
                        BotonFiltroMadera(
                            texto = "Filtrar",
                            onClick = { menuAbierto = true },
                            modifier = Modifier.width(120.dp)
                        )
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            OrdenFavoritos.entries.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion.etiqueta, fontFamily = FuenteMadera) },
                                    leadingIcon = {
                                        if (opcion == orden) Icon(Icons.Rounded.Check, contentDescription = null)
                                    },
                                    onClick = {
                                        viewModel.cambiarOrden(opcion)
                                        menuAbierto = false
                                    }
                                )
                            }
                        }
                    }
                }

                // ----- Chips: "Todos" y los tipos que hay en los favoritos -----
                if (tiposDisponibles.size > 1) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        item {
                            ChipFiltro(
                                texto = "Todos",
                                info = null,
                                color = DoradoChip,
                                seleccionado = tipoFiltro == null,
                                onClick = { viewModel.cambiarTipo(null) }
                            )
                        }
                        items(tiposDisponibles, key = { it }) { tipo ->
                            ChipFiltro(
                                texto = tiposInfo[tipo]?.nombreEs ?: Traductor.tipoEs(tipo),
                                info = tiposInfo[tipo],
                                color = colorDeTipo(tipo),
                                seleccionado = tipoFiltro == tipo,
                                onClick = { viewModel.cambiarTipo(tipo) }
                            )
                        }
                    }
                }

                // ----- Tira con la cantidad -----
                if (total > 0) {
                    val visibles = (estado as? UiState.Exito)?.datos?.size ?: 0
                    TiraFavoritos(
                        cantidad = visibles,
                        filtro = tipoFiltro?.let { tiposInfo[it]?.nombreEs ?: Traductor.tipoEs(it) } ?: "Todos",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                }

                // ----- Contenido: termina encima de la barra y se desvanece en los bordes -----
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = LocalRellenoBarra.current)
                        .desvanecerBordes(arriba = 24.dp, abajo = 20.dp)
                ) {
                    when (val e = estado) {
                        is UiState.Cargando -> Cargando("Cargando favoritos...")
                        is UiState.Error -> VacioView(e.mensaje)
                        is UiState.Exito -> when {
                            total == 0 -> SinFavoritos(onExplorar = onVolver)
                            e.datos.isEmpty() -> VacioView("No hay favoritos que coincidan")
                            else -> {
                                // El resumen sale solo sin filtro ni búsqueda
                                val mostrarResumen = tipoFiltro == null && busqueda.isBlank() && conteoTipos.isNotEmpty()
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    // Arriba: espacio para el desvanecido (y el Pokémon que sobresale si no hay resumen)
                                    contentPadding = PaddingValues(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = if (mostrarResumen) 18.dp else 34.dp,
                                        bottom = 24.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(22.dp)
                                ) {
                                    if (mostrarResumen) {
                                        item(key = "resumen", span = { GridItemSpan(maxLineSpan) }) {
                                            Box(modifier = Modifier.animateItem()) {
                                                AparecerSuave {
                                                    ResumenColeccion(
                                                        conteo = conteoTipos,
                                                        total = total,
                                                        tiposInfo = tiposInfo,
                                                        onTipoClick = viewModel::cambiarTipo
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    itemsIndexed(e.datos, key = { _, item -> item.id }) { indice, item ->
                                        val tipoColor = colorDe(item, tipoFiltro)
                                        // Se reacomoda animado al quitar un favorito
                                        Box(modifier = Modifier.animateItem()) {
                                            AparecerSuave(retrasoMs = (indice % 6) * 60) {
                                                TarjetaPokemon(
                                                    item = item,
                                                    colorPrincipal = colorDeTipo(tipoColor),
                                                    chips = chipsDe(item, tiposInfo, tipoFiltro),
                                                    // El Detalle se abre con el mismo tipo (el filtrado, si hay)
                                                    onClick = { onItemClick(item.id, tipoColor) },
                                                    onFavoritoClick = { viewModel.quitar(item) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ----- Aviso para deshacer -----
            AvisoDeshacer(
                item = quitado,
                onDeshacer = viewModel::deshacer,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = LocalRellenoBarra.current + 10.dp)
            )
        }
    }

    // ----- Vista previa del póster: compartir como imagen o como texto -----
    if (mostrarPoster) {
        DialogoCompartirFavoritos(
            favoritos = todos,
            conteoTipos = conteoTipos,
            tiposInfo = tiposInfo,
            onCompartirTexto = {
                mostrarPoster = false
                compartirTexto()
            },
            onCerrar = { mostrarPoster = false }
        )
    }
}

// Resumen de la colección: tipo favorito y barritas de los tipos con más favoritos.
// Tocar una barra filtra por ese tipo.
@Composable
private fun ResumenColeccion(
    conteo: List<Pair<String, Int>>,
    total: Int,
    tiposInfo: Map<String, Tipo>,
    onTipoClick: (String) -> Unit
) {
    val (tipoFavorito, cantidadFavorito) = conteo.first()
    val color = colorDeTipo(tipoFavorito)
    val forma = RoundedCornerShape(20.dp)

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth()
                .shadow(4.dp, forma)
                .clip(forma)
                .background(Brush.verticalGradient(listOf(Color.White, CremaTira)))
                .border(1.5.dp, Brush.verticalGradient(listOf(DoradoClaroChip, DoradoBordeChip)), forma)
                .padding(start = 14.dp, end = 14.dp, top = 24.dp, bottom = 12.dp)
        ) {
            // Tipo favorito
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(4.dp, CircleShape, ambientColor = color, spotColor = color)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(lerp(color, Color.White, 0.5f), color)))
                        .border(2.5.dp, Color.White, CircleShape)
                ) {
                    AsyncImage(
                        model = tiposInfo[tipoFavorito]?.iconoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tu tipo favorito", color = GrisResumen, fontFamily = FuenteMadera, fontSize = 12.sp)
                    Text(
                        text = tiposInfo[tipoFavorito]?.nombreEs ?: Traductor.tipoEs(tipoFavorito),
                        color = lerp(color, Color.Black, 0.3f),
                        fontFamily = FuenteMadera,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "$cantidadFavorito de $total Pokémon",
                        color = CafeMadera,
                        fontFamily = FuenteMadera,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Barritas de los tipos con más favoritos
            conteo.take(5).forEachIndexed { indice, (tipo, cantidad) ->
                FilaTipoResumen(
                    tipo = tipo,
                    info = tiposInfo[tipo],
                    cantidad = cantidad,
                    maximo = cantidadFavorito,
                    retrasoMs = indice * 90,
                    onClick = { onTipoClick(tipo) }
                )
            }
            if (conteo.size > 5) {
                Text(
                    text = "y ${conteo.size - 5} ${if (conteo.size - 5 == 1) "tipo más" else "tipos más"}",
                    color = GrisResumen,
                    fontFamily = FuenteMadera,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Pestaña dorada
        PestanaMetodo(
            texto = "Tu colección",
            icono = Icons.Rounded.Insights,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

// Fila del resumen: ícono, nombre, barra que se llena animada y cantidad
@Composable
private fun FilaTipoResumen(
    tipo: String,
    info: Tipo?,
    cantidad: Int,
    maximo: Int,
    retrasoMs: Int,
    onClick: () -> Unit
) {
    val color = colorDeTipo(tipo)
    var listo by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { listo = true }
    val fraccion by animateFloatAsState(
        targetValue = if (listo) cantidad / maximo.toFloat() else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = retrasoMs),
        label = "barraResumen"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        AsyncImage(
            model = info?.iconoUrl,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = info?.nombreEs ?: Traductor.tipoEs(tipo),
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(76.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(FondoBarraResumen)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraccion)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(color, lerp(color, Color.White, 0.3f))))
            )
        }
        Text(
            text = "$cantidad",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.width(28.dp)
        )
    }
}

// Chip de filtro: dorado para "Todos", color del tipo para cada tipo
@Composable
private fun ChipFiltro(
    texto: String,
    info: Tipo?,
    color: Color,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(50)
    val esTodos = info == null
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(if (seleccionado) 3.dp else 1.dp, forma)
            .clip(forma)
            .background(
                when {
                    seleccionado && esTodos -> Brush.verticalGradient(listOf(DoradoClaroChip, DoradoChip))
                    seleccionado -> Brush.verticalGradient(listOf(lerp(color, Color.White, 0.3f), color))
                    else -> Brush.verticalGradient(listOf(Color.White, CremaChip))
                }
            )
            .border(
                width = if (seleccionado) 1.5.dp else 1.dp,
                color = when {
                    seleccionado && esTodos -> CafeMadera.copy(alpha = 0.7f)
                    seleccionado -> lerp(color, Color.Black, 0.2f)
                    else -> CafeMadera.copy(alpha = 0.3f)
                },
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(start = 6.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
    ) {
        if (info != null) {
            AsyncImage(
                model = info.iconoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
        } else {
            Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RojoCorazon, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(5.dp))
        Text(
            text = texto,
            color = if (seleccionado && !esTodos) Color.White else CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

// Tira crema: "♥ 12 favoritos · Fuego"
@Composable
private fun TiraFavoritos(cantidad: Int, filtro: String, modifier: Modifier = Modifier) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(forma)
            .background(CremaTira.copy(alpha = 0.95f))
            .border(1.dp, CafeMadera.copy(alpha = 0.2f), forma)
            .padding(horizontal = 14.dp)
    ) {
        Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RojoCorazon, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = "$cantidad ${if (cantidad == 1) "favorito" else "favoritos"} · $filtro",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 15.sp
        )
    }
}

// Sin favoritos: corazón grande, mensaje y botón para ir a Inicio
@Composable
private fun SinFavoritos(onExplorar: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .shadow(4.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(CremaTira.copy(alpha = 0.95f))
                .border(1.5.dp, CafeMadera.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(RojoCorazon.copy(alpha = 0.12f))
            ) {
                Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = RojoCorazon, modifier = Modifier.size(52.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Aún no tienes favoritos",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Toca el corazón de un Pokémon para guardarlo aquí.",
                color = CafeMadera.copy(alpha = 0.8f),
                fontFamily = FuenteMadera,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .shadow(3.dp, RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(Brush.verticalGradient(listOf(DoradoClaroChip, DoradoChip)))
                    .border(1.5.dp, CafeMadera.copy(alpha = 0.6f), RoundedCornerShape(50))
                    .clickable(onClick = onExplorar)
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Outlined.CatchingPokemon, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Explorar Pokémon",
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// Aviso "Quitaste a X · Deshacer" que sube desde abajo
@Composable
private fun AvisoDeshacer(item: Item?, onDeshacer: () -> Unit, modifier: Modifier = Modifier) {
    // Recuerda el último para que el texto no desaparezca mientras se oculta
    var recordado by remember { mutableStateOf<Item?>(null) }
    SideEffect { if (item != null) recordado = item }
    val mostrado = item ?: recordado
    val forma = RoundedCornerShape(50)

    AnimatedVisibility(
        visible = item != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, forma)
                .clip(forma)
                .background(CremaTira)
                .border(1.5.dp, CafeMadera.copy(alpha = 0.4f), forma)
                .padding(start = 16.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = RojoCorazon, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Quitaste a ${mostrado?.nombre?.replaceFirstChar { it.uppercase() } ?: ""}",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Deshacer",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .clip(forma)
                    .background(Brush.verticalGradient(listOf(DoradoClaroChip, DoradoChip)))
                    .border(1.dp, CafeMadera.copy(alpha = 0.5f), forma)
                    .clickable(onClick = onDeshacer)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

// Tipo que da el color (y con el que se abre el Detalle): el filtrado si lo tiene; si no, su tipo principal
private fun colorDe(item: Item, tipoFiltro: String?): String =
    tipoFiltro?.takeIf { it in item.tipos } ?: tipoParaColor(item.tipos) ?: "normal"

// Íconos de tipo de la tarjeta: primero el filtrado, máximo 2
private fun chipsDe(item: Item, tiposInfo: Map<String, Tipo>, tipoFiltro: String?): List<TipoChip> =
    item.tipos
        .sortedBy { if (it == tipoFiltro) 0 else 1 }
        .mapNotNull { nombre -> tiposInfo[nombre]?.let { TipoChip(nombre, it.nombreEs, it.iconoUrl) } }
        .take(2)

// Texto para compartir: "⭐ Mis Pokémon favoritos (3)" y una línea por Pokémon
private fun textoCompartir(lista: List<Item>, tiposInfo: Map<String, Tipo>): String = buildString {
    appendLine("⭐ Mis Pokémon favoritos (${lista.size})")
    appendLine()
    lista.forEach { item ->
        val nombre = item.nombre.replaceFirstChar { it.uppercase() }
        val tipos = item.tipos.joinToString(" / ") { tiposInfo[it]?.nombreEs ?: Traductor.tipoEs(it) }
        append("• #%03d %s".format(item.id, nombre))
        if (tipos.isNotEmpty()) append(" ($tipos)")
        appendLine()
    }
    appendLine()
    append("Enviado desde mi PokeDex")
}