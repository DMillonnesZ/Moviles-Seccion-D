package com.tecsup.pokemon.ui.evolucion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.pokemon.domain.model.MetodoEvolucion
import com.tecsup.pokemon.ui.AppViewModelProvider
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.AparecerSuave
import com.tecsup.pokemon.ui.components.BotonFiltroMadera
import com.tecsup.pokemon.ui.components.BotonVolver
import com.tecsup.pokemon.ui.components.BuscadorMadera
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.ErrorView
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LetreroSeccion
import com.tecsup.pokemon.ui.components.LocalRellenoBarra
import com.tecsup.pokemon.ui.components.TarjetaCadena
import com.tecsup.pokemon.ui.components.VacioView
import com.tecsup.pokemon.ui.components.colorDeTipo
import com.tecsup.pokemon.ui.components.colorMetodo
import com.tecsup.pokemon.ui.components.desvanecerBordes
import com.tecsup.pokemon.ui.components.iconoMetodo
import com.tecsup.pokemon.ui.components.tipoParaColor

private val DoradoClaroChip = Color(0xFFFFE7A0)
private val DoradoChip = Color(0xFFF5C451)
private val CremaChip = Color(0xFFFFF8EA)
private val CremaTira = Color(0xFFFFF4DF)

// Pestaña Evolución: todas las cadenas evolutivas, con buscador, filtro por método y orden
@Composable
fun EvolucionScreen(
    viewModel: EvolucionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onCadenaClick: (Int) -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val metodo by viewModel.metodo.collectAsStateWithLifecycle()
    val orden by viewModel.orden.collectAsStateWithLifecycle()
    val progreso by viewModel.progreso.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val tipos by viewModel.tiposPorPokemon.collectAsStateWithLifecycle()
    val totalGuardadas by viewModel.totalGuardadas.collectAsStateWithLifecycle()
    var menuAbierto by remember { mutableStateOf(false) }

    // Color de cada Pokémon según su tipo
    val colorDe: (Int) -> Color = { id -> colorDeTipo(tipoParaColor(tipos[id]) ?: "") }

    FondoInicio {
        Column(modifier = Modifier.fillMaxSize()) {
            // ----- Cabecera: letrero centrado + botón de descarga -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 36.dp, bottom = 4.dp)
            ) {
                // Mismo ancho que el botón, para que el letrero quede centrado
                Spacer(Modifier.width(40.dp))
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
                BotonVolver(
                    onClick = viewModel::descargar,
                    icono = Icons.Rounded.CloudDownload,
                    descripcion = "Descargar evoluciones"
                )
            }

            // ----- Buscador + Filtrar -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                // Texto corto: entra dentro del recuadro claro de la tabla
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
                        OrdenCadenas.entries.forEach { opcion ->
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

            // ----- Chips de métodos -----
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                item {
                    ChipMetodo(
                        texto = "Todos",
                        icono = iconoMetodo(null),
                        colorIcono = colorMetodo(null),
                        seleccionado = metodo == null,
                        onClick = { viewModel.cambiarMetodo(null) }
                    )
                }
                items(MetodoEvolucion.entries) { opcion ->
                    ChipMetodo(
                        texto = opcion.etiqueta,
                        icono = iconoMetodo(opcion),
                        colorIcono = colorMetodo(opcion),
                        seleccionado = metodo == opcion,
                        onClick = { viewModel.cambiarMetodo(opcion) }
                    )
                }
            }

            // ----- Tira: cantidad de cadenas o avance de la descarga -----
            val cantidadVisible = (estado as? UiState.Exito)?.datos?.size ?: 0
            TiraEstado(
                progreso = progreso,
                cantidad = cantidadVisible,
                filtro = metodo?.etiqueta ?: "Todos",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Aviso de error con opción de reintentar
            aviso?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontFamily = FuenteMadera,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = viewModel::descargar) {
                        Text("Reintentar", fontFamily = FuenteMadera)
                    }
                }
            }

            // ----- Lista: termina justo encima de la barra y se desvanece en los bordes -----
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = LocalRellenoBarra.current)
                    .desvanecerBordes(arriba = 20.dp, abajo = 20.dp)
            ) {
                when (val e = estado) {
                    is UiState.Cargando -> Cargando("Cargando evoluciones...")
                    is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::descargar)
                    is UiState.Exito -> when {
                        e.datos.isEmpty() && progreso != null -> Cargando("Descargando evoluciones...")
                        e.datos.isEmpty() && totalGuardadas == 0 ->
                            VacioView("Aún no hay evoluciones guardadas.\nToca la nube para descargarlas.")
                        e.datos.isEmpty() -> VacioView("No hay cadenas que coincidan")
                        else -> LazyColumn(
                            // Márgenes laterales menores: más ancho para cadenas de 3 etapas
                            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 16.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            itemsIndexed(e.datos, key = { _, cadena -> cadena.id }) { indice, cadena ->
                                AparecerSuave(retrasoMs = (indice % 6) * 60) {
                                    TarjetaCadena(
                                        cadena = cadena,
                                        colorDe = colorDe,
                                        onClick = { onCadenaClick(cadena.id) }
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

// Chip de un método: dorado si está seleccionado, crema si no
@Composable
private fun ChipMetodo(
    texto: String,
    icono: ImageVector,
    colorIcono: Color,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(if (seleccionado) 3.dp else 1.dp, forma)
            .clip(forma)
            .background(
                if (seleccionado) Brush.verticalGradient(listOf(DoradoClaroChip, DoradoChip))
                else Brush.verticalGradient(listOf(Color.White, CremaChip))
            )
            .border(
                width = if (seleccionado) 1.5.dp else 1.dp,
                color = if (seleccionado) CafeMadera.copy(alpha = 0.7f) else CafeMadera.copy(alpha = 0.3f),
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            text = texto,
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

// Tira crema: "552 cadenas · Nivel" o el avance de la descarga con su barra
@Composable
private fun TiraEstado(
    progreso: ProgresoDescarga?,
    cantidad: Int,
    filtro: String,
    modifier: Modifier = Modifier
) {
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
        if (progreso != null) {
            Icon(Icons.Rounded.CloudDownload, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                text = if (progreso.total > 0) "Descargando ${progreso.hechas} / ${progreso.total}" else "Preparando descarga...",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 14.sp
            )
            Spacer(Modifier.width(10.dp))
            LinearProgressIndicator(
                progress = { if (progreso.total > 0) progreso.hechas / progreso.total.toFloat() else 0f },
                color = DoradoChip,
                trackColor = CafeMadera.copy(alpha = 0.12f),
                modifier = Modifier
                    .width(90.dp)
                    .height(6.dp)
                    .clip(forma)
            )
        } else {
            Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                text = "$cantidad cadenas · $filtro",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 15.sp
            )
        }
    }
}