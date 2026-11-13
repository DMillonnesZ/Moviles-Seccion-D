package com.tecsup.pokemon.ui.tipos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Anuncio
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.BotonFiltroMadera
import com.tecsup.pokemon.ui.components.BuscadorMadera
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.ErrorView
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LocalRellenoBarra
import com.tecsup.pokemon.ui.components.TarjetaTipo
import com.tecsup.pokemon.ui.components.TituloTabla
import com.tecsup.pokemon.ui.components.VacioView
import com.tecsup.pokemon.ui.components.desvanecerBordes

private val CremaAnuncio = Color(0xFFFFF4DF)
private val DoradoClaroAnuncio = Color(0xFFFFE7A0)
private val DoradoBordeAnuncio = Color(0xFFD9A43A)
private val AzulAnuncio = Color(0xFF3D8FD6)
private val GrisAnuncio = Color(0xFF7A6A5A)

@Composable
fun TiposScreen(
    viewModel: TiposViewModel,
    onTipoClick: (String) -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val sincronizando by viewModel.sincronizando.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val orden by viewModel.orden.collectAsStateWithLifecycle()
    val anuncio by viewModel.anuncio.collectAsStateWithLifecycle()
    var menuAbierto by remember { mutableStateOf(false) }

    FondoInicio {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ----- Título + botón Sincronizar -----
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                TituloTabla("PokeDex")
                BotonSincronizar(
                    sincronizando = sincronizando,
                    onClick = viewModel::refrescar,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                )
            }

            // ----- Buscador + Filtrar -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                BuscadorMadera(
                    valor = busqueda,
                    onValorChange = viewModel::onBusquedaChange,
                    placeholder = "Buscar tipo...",
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
                        OrdenTipos.entries.forEach { opcion ->
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

            // ----- Anuncio del admin para todos -----
            BannerAnuncio(anuncio = anuncio, onCerrar = viewModel::cerrarAnuncio)

            // Aviso de modo sin conexión con opción de reintentar
            aviso?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = viewModel::refrescar, enabled = !sincronizando) {
                        Text("Reintentar")
                    }
                }
            }

            // ----- Contenido: termina justo encima de la barra de navegación -----
            // Se desvanece suave arriba (bajo el buscador) y abajo (sobre la barra)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = LocalRellenoBarra.current)
                    .desvanecerBordes(arriba = 24.dp, abajo = 20.dp)
            ) {
                when (val e = estado) {
                    is UiState.Cargando -> Cargando("Descargando tipos...")
                    is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::refrescar)
                    is UiState.Exito ->
                        if (e.datos.isEmpty()) {
                            if (busqueda.isNotBlank()) VacioView("No hay tipos que coincidan con \"$busqueda\"")
                            else VacioView("No hay tipos guardados.\nToca sincronizar para descargarlos.")
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 104.dp),
                                // Márgenes arriba y abajo: sin scroll, nada queda desvanecido
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 20.dp,
                                    bottom = 24.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(e.datos, key = { it.nombre }) { tipo ->
                                    TarjetaTipo(
                                        tipo = tipo,
                                        onClick = { onTipoClick(tipo.nombre) },
                                        // Se reacomoda animada al filtrar u ordenar
                                        modifier = Modifier.animateItem()
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

// Anuncio del admin: megáfono, texto, autor y botón para cerrarlo
@Composable
private fun BannerAnuncio(anuncio: Anuncio?, onCerrar: () -> Unit) {
    // Recuerda el último para que el texto no desaparezca mientras se oculta
    var recordado by remember { mutableStateOf<Anuncio?>(null) }
    SideEffect { if (anuncio != null) recordado = anuncio }
    val mostrado = anuncio ?: recordado
    val forma = RoundedCornerShape(18.dp)

    AnimatedVisibility(
        visible = anuncio != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth()
                .shadow(4.dp, forma)
                .clip(forma)
                .background(Brush.verticalGradient(listOf(Color.White, CremaAnuncio)))
                .border(1.5.dp, Brush.verticalGradient(listOf(DoradoClaroAnuncio, DoradoBordeAnuncio)), forma)
                .padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF8CC4F2), AzulAnuncio)))
                    .border(2.dp, Color.White, CircleShape)
            ) {
                Icon(Icons.Rounded.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Anuncio",
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = mostrado?.texto.orEmpty(),
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontSize = 14.sp
                )
                Text(
                    text = "— ${mostrado?.autor.orEmpty()}",
                    color = GrisAnuncio,
                    fontFamily = FuenteMadera,
                    fontSize = 12.sp
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onCerrar)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Cerrar anuncio", tint = CafeMadera.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

// Botón redondo de madera que gira mientras sincroniza
@Composable
private fun BotonSincronizar(
    sincronizando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transicion = rememberInfiniteTransition(label = "sincronizar")
    val giro by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "giroSincronizar"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .clickable(enabled = !sincronizando, onClick = onClick)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_nombre_tipo),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = "Sincronizar",
            tint = CafeMadera,
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer { rotationZ = if (sincronizando) giro else 0f }
        )
    }
}