package com.saludplus.citas.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.Duraciones
import com.saludplus.citas.ui.theme.GrisTexto
import java.time.LocalDate

private data class DestinoBarra(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
)

private val destinos = listOf(
    DestinoBarra(Rutas.HOME, "Inicio", Icons.Default.Home),
    DestinoBarra(Rutas.MIS_CITAS, "Citas", Icons.Default.CalendarMonth),
    DestinoBarra(Rutas.RESULTADOS, "Resultados", Icons.Default.Description),
    DestinoBarra(Rutas.PERFIL, "Perfil", Icons.Default.Person)
)

private val FondoBarra = Color(0xFFFAFBFE)
private val LineaBarra = Color(0xFFE6EBF3)

@Composable
fun BarraNavegacion(
    rutaActual: String,
    onNavegar: (String) -> Unit
) {
    val hoyIso = remember { LocalDate.now().toString() }
    val citasProximasCount = remember {
        Repositorio.citasDelUsuario().count { it.fecha >= hoyIso }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LineaBarra)
        )
        NavigationBar(
            containerColor = FondoBarra,
            tonalElevation = 0.dp
        ) {
            destinos.forEach { destino ->
                val seleccionado = rutaActual == destino.ruta

                val escalaIcono by animateFloatAsState(
                    targetValue = if (seleccionado) 1.15f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "EscalaIconoNavegacion"
                )

                val colorContenido by animateColorAsState(
                    targetValue = if (seleccionado) AzulPrimario else GrisTexto,
                    animationSpec = tween(durationMillis = Duraciones.corta),
                    label = "ColorNavegacion"
                )

                NavigationBarItem(
                    selected = seleccionado,
                    onClick = { onNavegar(destino.ruta) },
                    icon = {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = destino.icono,
                                contentDescription = destino.titulo,
                                tint = colorContenido,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = escalaIcono
                                    scaleY = escalaIcono
                                }
                            )
                            if (destino.ruta == Rutas.MIS_CITAS && citasProximasCount > 0) {
                                Insignia(
                                    numero = citasProximasCount,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .graphicsLayer {
                                            translationX = 10.dp.toPx()
                                            translationY = (-6).dp.toPx()
                                        }
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = destino.titulo,
                            fontSize = 12.sp,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                            color = colorContenido
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrimario,
                        selectedTextColor = AzulPrimario,
                        indicatorColor = AzulClaro,
                        unselectedIconColor = GrisTexto,
                        unselectedTextColor = GrisTexto
                    )
                )
            }
        }
    }
}
