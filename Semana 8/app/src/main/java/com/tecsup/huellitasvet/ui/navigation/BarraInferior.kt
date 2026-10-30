package com.tecsup.huellitasvet.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class ItemBarra(val ruta: String, val etiqueta: String, val icono: ImageVector)

val itemsDueno = listOf(
    ItemBarra(Rutas.INICIO, "Inicio", Icons.Filled.Home),
    ItemBarra(Rutas.MASCOTAS, "Mascotas", Icons.Outlined.Pets),
    ItemBarra(Rutas.MIS_CITAS, "Citas", Icons.Outlined.CalendarMonth),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Outlined.Person)
)

@Composable
fun BarraInferior(items: List<ItemBarra>, rutaActual: String?, onClick: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    NavigationBar(containerColor = cs.surface, tonalElevation = 0.dp) {
        items.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = { onClick(item.ruta) },
                icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                label = { Text(item.etiqueta, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = cs.primary,
                    selectedTextColor = cs.primary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = cs.onSurfaceVariant.copy(alpha = 0.6f),
                    unselectedTextColor = cs.onSurfaceVariant.copy(alpha = 0.6f)
                )
            )
        }
    }
}