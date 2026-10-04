package com.saludplus.citas.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto

private data class DestinoBarra(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
)

// Los 4 destinos del menú principal
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
    Column {
        // Línea fina encima de la barra
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
                NavigationBarItem(
                    selected = rutaActual == destino.ruta,
                    onClick = { onNavegar(destino.ruta) },
                    icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                    label = { Text(destino.titulo) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrimario,
                        selectedTextColor = AzulPrimario,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = GrisTexto,
                        unselectedTextColor = GrisTexto
                    )
                )
            }
        }
    }
}