package com.saludplus.citas.ui.screens.resultados

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.BarraNavegacion
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.estiloEspecialidad
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.GrisTexto

@Composable
fun ResultadosScreen(onNavegar: (String) -> Unit) {
    // Del resultado más reciente al más antiguo
    val resultados = Repositorio.resultados.sortedByDescending { it.fecha }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Resultados") },
        bottomBar = { BarraNavegacion(rutaActual = Rutas.RESULTADOS, onNavegar = onNavegar) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "${resultados.size} resultados de exámenes",
                    fontSize = 14.sp,
                    color = GrisTexto
                )
            }
            items(resultados, key = { it.id }) { resultado ->
                val especialidad = Repositorio.obtenerEspecialidad(resultado.especialidadId)
                val estilo = estiloEspecialidad(resultado.especialidadId)
                val disponible = resultado.estado == "Disponible"

                TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
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
                            Text(
                                text = resultado.examen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulOscuro
                            )
                            Text(
                                text = "${especialidad?.nombre ?: ""} · ${resultado.fecha}",
                                fontSize = 13.sp,
                                color = GrisTexto
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Etiqueta de estado, abajo a la derecha
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (disponible) Color(0xFFDDF7E8) else Color(0xFFFFF1D6))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = resultado.estado,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (disponible) Color(0xFF1E9E5A) else Color(0xFFB7791F)
                        )
                    }
                }
            }
        }
    }
}