package com.saludplus.citas.ui.screens.agendamiento

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.ChipFiltro
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.Esqueleto
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto

enum class OrdenMedico {
    CALIFICACION,
    EXPERIENCIA
}

@Composable
private fun EstrellasCalificacion(
    calificacion: Double,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val enteras = calificacion.toInt()
        val tieneMedia = (calificacion - enteras) >= 0.4

        for (i in 1..5) {
            val icono = when {
                i <= enteras -> Icons.Default.Star
                i == enteras + 1 && tieneMedia -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Default.StarOutline
            }
            val tint = if (i <= enteras || (i == enteras + 1 && tieneMedia)) Color(0xFFF5B301) else Color(0xFFD1D5DB)

            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun MedicosScreen(
    especialidadId: Int,
    onAtras: () -> Unit,
    onMedico: (Int) -> Unit
) {
    val especialidad = Repositorio.obtenerEspecialidad(especialidadId)
    val nombreEspecialidad = especialidad?.nombre ?: "Especialidad"

    var buscando by remember { mutableStateOf(false) }
    var busqueda by remember { mutableStateOf("") }
    var orden by remember { mutableStateOf(OrdenMedico.CALIFICACION) }
    var soloDisponibleHoy by remember { mutableStateOf(false) }
    var enFoco by remember { mutableStateOf(false) }

    val colorBordeFoco by animateColorAsState(
        targetValue = if (enFoco) AzulPrimario else Color.Transparent,
        animationSpec = tween(200),
        label = "BordeBuscadorMedicos"
    )

    // Lista de médicos filtrada y ordenada dinámicamente
    val medicos = remember(especialidadId, busqueda, orden, soloDisponibleHoy) {
        var base = Repositorio.buscarMedicos(especialidadId, busqueda)
        if (soloDisponibleHoy) {
            base = base.filter { it.disponibilidad.contains("hoy", ignoreCase = true) }
        }
        when (orden) {
            OrdenMedico.CALIFICACION -> base.sortedByDescending { it.calificacion }
            OrdenMedico.EXPERIENCIA -> base.sortedByDescending { it.aniosExperiencia }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            BarraSuperior(
                titulo = "Médicos de $nombreEspecialidad",
                onAtras = onAtras,
                acciones = {
                    IconButton(
                        onClick = {
                            buscando = !buscando
                            if (!buscando) busqueda = ""
                        },
                        modifier = Modifier.efectoPresion {
                            buscando = !buscando
                            if (!buscando) busqueda = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar médico",
                            tint = AzulOscuro
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Buscador con animación de foco
            if (buscando) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar médico por nombre...", color = Color(0xFF9CA3AF)) },
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
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .onFocusChanged { enFoco = it.isFocused }
                )
            }

            // Chips de orden y filtros
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ChipFiltro(
                        seleccionado = orden == OrdenMedico.CALIFICACION,
                        texto = "Mejor calificados",
                        onClick = { orden = OrdenMedico.CALIFICACION }
                    )
                }
                item {
                    ChipFiltro(
                        seleccionado = orden == OrdenMedico.EXPERIENCIA,
                        texto = "Más experiencia",
                        onClick = { orden = OrdenMedico.EXPERIENCIA }
                    )
                }
                item {
                    ChipFiltro(
                        seleccionado = soloDisponibleHoy,
                        texto = "Disponible hoy",
                        onClick = { soloDisponibleHoy = !soloDisponibleHoy }
                    )
                }
            }

            if (medicos.isEmpty()) {
                EstadoVacio(
                    titulo = "Sin médicos encontrados",
                    mensaje = "No hay médicos que coincidan con los criterios de búsqueda o filtros seleccionados.",
                    icono = Icons.Default.Search,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(medicos, key = { it.id }) { medico ->
                        TarjetaSuave(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .efectoPresion { onMedico(medico.id) },
                            onClick = { onMedico(medico.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarMedico(
                                    nombre = medico.nombre,
                                    foto = medico.foto,
                                    tamano = 56.dp
                                )
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = medico.nombre,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulOscuro
                                    )
                                    Text(
                                        text = "$nombreEspecialidad · ${medico.aniosExperiencia} años exp.",
                                        fontSize = 13.sp,
                                        color = GrisTexto
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        EstrellasCalificacion(calificacion = medico.calificacion)
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "${medico.calificacion} (${medico.resenas})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GrisTexto
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            // Etiqueta verde de disponibilidad, abajo a la derecha
                            Box(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (medico.disponibilidad.contains("hoy", ignoreCase = true))
                                            Color(0xFFDDF7E8)
                                        else Color(0xFFEFF6FF)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = medico.disponibilidad,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (medico.disponibilidad.contains("hoy", ignoreCase = true))
                                        Color(0xFF1E9E5A)
                                    else AzulPrimario
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
