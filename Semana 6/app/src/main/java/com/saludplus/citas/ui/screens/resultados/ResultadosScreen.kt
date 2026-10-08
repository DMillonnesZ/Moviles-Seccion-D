package com.saludplus.citas.ui.screens.resultados

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.BarraNavegacion
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.ChipFiltro
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.components.estiloEspecialidad
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.ColoresSemanticos
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.util.Fechas

@Composable
private fun TextoResaltado(
    texto: String,
    busqueda: String,
    modifier: Modifier = Modifier
) {
    if (busqueda.isBlank()) {
        Text(
            text = texto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AzulOscuro,
            modifier = modifier
        )
        return
    }

    val annotated = remember(texto, busqueda) {
        buildAnnotatedString {
            val lowerTexto = texto.lowercase()
            val lowerQuery = busqueda.trim().lowercase()
            var startIndex = 0

            while (true) {
                val index = lowerTexto.indexOf(lowerQuery, startIndex)
                if (index == -1) {
                    append(texto.substring(startIndex))
                    break
                }
                append(texto.substring(startIndex, index))
                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = AzulPrimario)) {
                    append(texto.substring(index, index + lowerQuery.length))
                }
                startIndex = index + lowerQuery.length
            }
        }
    }

    Text(
        text = annotated,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = AzulOscuro,
        modifier = modifier
    )
}

@Composable
fun ResultadosScreen(
    onNavegar: (String) -> Unit,
    onResultado: (Int) -> Unit = {}
) {
    var busqueda by remember { mutableStateOf("") }
    var enFoco by remember { mutableStateOf(false) }
    var filtroEstado by remember { mutableStateOf("Todos") }

    val colorBordeFoco by animateColorAsState(
        targetValue = if (enFoco) AzulPrimario else Color.Transparent,
        animationSpec = tween(200),
        label = "BordeBuscadorResultados"
    )

    // Todos los resultados ordenados del más reciente al más antiguo
    val todosResultados = remember { Repositorio.resultados.sortedByDescending { it.fecha } }

    // Filtrar por estado y término de búsqueda
    val resultadosFiltrados = todosResultados.filter { res ->
        val coincideEstado = when (filtroEstado) {
            "Disponible" -> res.estado == "Disponible"
            "En proceso" -> res.estado == "En proceso"
            else -> true
        }
        val especialidad = Repositorio.obtenerEspecialidad(res.especialidadId)
        val nombreEspecialidad = especialidad?.nombre ?: ""

        val coincideBusqueda = busqueda.isBlank() ||
                res.examen.contains(busqueda, ignoreCase = true) ||
                nombreEspecialidad.contains(busqueda, ignoreCase = true)

        coincideEstado && coincideBusqueda
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Resultados") },
        bottomBar = { BarraNavegacion(rutaActual = Rutas.RESULTADOS, onNavegar = onNavegar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Buscador por examen o especialidad
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar examen...", color = Color(0xFF9CA3AF)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = if (enFoco) AzulPrimario else GrisTexto
                    )
                },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = GrisTexto)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5FB),
                    unfocusedContainerColor = Color(0xFFF1F5FB),
                    focusedBorderColor = colorBordeFoco,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = AzulPrimario
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .onFocusChanged { enFoco = it.isFocused }
            )

            // Chips de filtro por estado
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                val opcionesFiltro = listOf("Todos", "Disponible", "En proceso")
                items(opcionesFiltro) { opcion ->
                    ChipFiltro(
                        seleccionado = filtroEstado == opcion,
                        texto = opcion,
                        onClick = { filtroEstado = opcion }
                    )
                }
            }

            if (resultadosFiltrados.isEmpty()) {
                EstadoVacio(
                    titulo = "Sin resultados",
                    mensaje = if (busqueda.isNotEmpty()) {
                        "No encontramos exámenes que coincidan con \"$busqueda\"."
                    } else {
                        "No hay exámenes registrados con el estado \"$filtroEstado\"."
                    },
                    icono = Icons.Default.Description,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${resultadosFiltrados.size} de ${todosResultados.size} resultados",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrisTexto
                        )
                    }

                    items(resultadosFiltrados, key = { it.id }) { resultado ->
                        val especialidad = Repositorio.obtenerEspecialidad(resultado.especialidadId)
                        val estilo = estiloEspecialidad(resultado.especialidadId)
                        val disponible = resultado.estado == "Disponible"
                        val fechaTexto = Fechas.fechaEnTexto(resultado.fecha)

                        TarjetaSuave(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .efectoPresion { onResultado(resultado.id) },
                            onClick = { onResultado(resultado.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(estilo.fondo),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = estilo.icono,
                                        contentDescription = null,
                                        tint = estilo.color,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    TextoResaltado(
                                        texto = resultado.examen,
                                        busqueda = busqueda
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "${especialidad?.nombre ?: ""} · $fechaTexto",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrisTexto
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Ver detalle",
                                    tint = GrisTexto
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (disponible) ColoresSemanticos.ExitoFondo else ColoresSemanticos.AdvertenciaFondo)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = resultado.estado,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (disponible) ColoresSemanticos.Exito else ColoresSemanticos.Advertencia
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
