package com.tecsup.pokemon.ui.lista

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.OrdenPokemon
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.components.AparecerSuave
import com.tecsup.pokemon.ui.components.BotonFiltroMadera
import com.tecsup.pokemon.ui.components.BotonVolver
import com.tecsup.pokemon.ui.components.BuscadorMadera
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LluviaCorazones
import com.tecsup.pokemon.ui.components.LocalRellenoBarra
import com.tecsup.pokemon.ui.components.Paginador
import com.tecsup.pokemon.ui.components.TarjetaPokemon
import com.tecsup.pokemon.ui.components.TipoChip
import com.tecsup.pokemon.ui.components.TituloTipo
import com.tecsup.pokemon.ui.components.VacioView
import com.tecsup.pokemon.ui.components.colorDeTipo

private val RojoCorazon = Color(0xFFE53935)

@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onItemClick: (Int) -> Unit,
    onVolver: () -> Unit
) {
    val tiposInfo by viewModel.tiposInfo.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val orden by viewModel.orden.collectAsStateWithLifecycle()
    val soloFavoritos by viewModel.soloFavoritos.collectAsStateWithLifecycle()
    val pagina by viewModel.pagina.collectAsStateWithLifecycle()
    val pokemon = viewModel.pokemon.collectAsLazyPagingItems()
    var menuAbierto by remember { mutableStateOf(false) }

    // Cada vez que aumenta, se lanza la lluvia de corazones
    var disparoCorazones by remember { mutableIntStateOf(0) }

    val infoTipo = tiposInfo[viewModel.tipo]
    val colorPrincipal = colorDeTipo(viewModel.tipo)

    // Total y páginas (con placeholders, itemCount es el total de la consulta)
    val total = pokemon.itemCount
    val totalPaginas = maxOf(1, (total + POKEMON_POR_PAGINA - 1) / POKEMON_POR_PAGINA)
    val paginaSegura = pagina.coerceIn(0, totalPaginas - 1)
    val indices = (paginaSegura * POKEMON_POR_PAGINA until minOf(total, (paginaSegura + 1) * POKEMON_POR_PAGINA)).toList()

    // Si la página quedó fuera de rango (por ejemplo, al filtrar), se corrige
    LaunchedEffect(totalPaginas) {
        if (pagina > totalPaginas - 1) viewModel.irAPagina(totalPaginas - 1)
    }

    // Al cambiar de página, la lista vuelve arriba
    val estadoGrilla = rememberLazyGridState()
    LaunchedEffect(paginaSegura) { estadoGrilla.scrollToItem(0) }

    FondoInicio {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ----- Volver + letrero del tipo (en fila, sin chocar) -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp)
            ) {
                BotonVolver(onClick = onVolver)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    TituloTipo(
                        nombre = infoTipo?.nombreEs ?: viewModel.tipo,
                        iconoUrl = infoTipo?.iconoUrl
                    )
                }
                // Mismo ancho que el botón, para que el letrero quede centrado en la pantalla
                Spacer(Modifier.width(48.dp))
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
                        // Orden
                        OrdenPokemon.entries.forEach { opcion ->
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
                        HorizontalDivider()
                        // Solo favoritos (activar o desactivar)
                        DropdownMenuItem(
                            text = { Text("Solo favoritos", fontFamily = FuenteMadera) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (soloFavoritos) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = null,
                                    tint = RojoCorazon
                                )
                            },
                            trailingIcon = {
                                if (soloFavoritos) Icon(Icons.Rounded.Check, contentDescription = null)
                            },
                            onClick = {
                                viewModel.alternarSoloFavoritos()
                                menuAbierto = false
                            }
                        )
                    }
                }
            }

            // ----- Contenido + paginador: termina justo encima de la barra -----
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = LocalRellenoBarra.current)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    val cargandoInicio = pokemon.loadState.refresh is LoadState.Loading && total == 0
                    val sinResultados = pokemon.loadState.refresh is LoadState.NotLoading && total == 0

                    when {
                        cargandoInicio -> Cargando("Cargando Pokémon...")

                        sinResultados -> VacioView(
                            when {
                                busqueda.isNotBlank() -> "No hay Pokémon que coincidan con \"$busqueda\""
                                soloFavoritos -> "No tienes favoritos de este tipo"
                                else -> "No hay Pokémon de este tipo"
                            }
                        )

                        else -> LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = estadoGrilla,
                            // Arriba deja espacio para el Pokémon que sobresale de la tarjeta
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(22.dp)
                        ) {
                            items(indices, key = { it }) { indice ->
                                // Pedir el elemento hace que Paging cargue esa página de Room
                                val item = pokemon[indice]
                                if (item != null) {
                                    // Entrada en cascada al cambiar de página
                                    AparecerSuave(retrasoMs = (indice % POKEMON_POR_PAGINA) * 60) {
                                        TarjetaPokemon(
                                            item = item,
                                            colorPrincipal = colorPrincipal,
                                            chips = chipsDe(item, tiposInfo, viewModel.tipo),
                                            onClick = { onItemClick(item.id) },
                                            onFavoritoClick = {
                                                // Solo al marcar favorito cae la lluvia de corazones
                                                if (!item.esFavorito) disparoCorazones++
                                                viewModel.alternarFavorito(item.id)
                                            }
                                        )
                                    }
                                } else {
                                    TarjetaCargando()
                                }
                            }
                        }
                    }
                }

                // ----- Paginador fijo abajo -----
                if (total > 0) {
                    Paginador(
                        paginaActual = paginaSegura,
                        totalPaginas = totalPaginas,
                        onCambiarPagina = viewModel::irAPagina,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 4.dp)
                    )
                }
            }
        }

        // Lluvia de corazones encima de toda la pantalla (no bloquea los toques)
        LluviaCorazones(disparo = disparoCorazones)
    }
}

// Tarjeta vacía mientras Room carga la página
@Composable
private fun TarjetaCargando() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.78f)
    ) {
        Image(
            painter = painterResource(R.drawable.tarjeta_pokemon_marco),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Tipos del Pokémon: primero el tipo de la pantalla, máximo 2
private fun chipsDe(item: Item, tiposInfo: Map<String, Tipo>, tipoActual: String): List<TipoChip> =
    item.tipos
        .sortedBy { if (it == tipoActual) 0 else 1 }
        .mapNotNull { nombre ->
            tiposInfo[nombre]?.let { TipoChip(nombre, it.nombreEs, it.iconoUrl) }
        }
        .take(2)