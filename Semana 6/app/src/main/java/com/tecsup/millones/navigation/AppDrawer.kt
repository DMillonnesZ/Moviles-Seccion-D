package com.tecsup.millones.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.millones.components.DrawerHeader
import com.tecsup.millones.ui.theme.Morado
import com.tecsup.millones.ui.theme.MoradoSeleccion

@Composable
fun AppDrawer(
    rutaActual: String,
    onDestinoClick: (Destino) -> Unit,
    onCerrarSesion: () -> Unit
) {
    // Colores del ítem activo: fondo lila, texto e ícono morados
    val coloresItem = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = MoradoSeleccion,
        selectedTextColor = Morado,
        selectedIconColor = Morado
    )

    ModalDrawerSheet {
        DrawerHeader(
            nombre = "Daniel Millones",
            correo = "daniel.millones@tecsup.edu.pe"
        )

        destinosDrawer.forEach { destino ->
            NavigationDrawerItem(
                label = { Text(destino.titulo) },
                selected = rutaActual == destino.ruta,
                onClick = { onDestinoClick(destino) },
                icon = {
                    Icon(
                        imageVector = destino.icono,
                        contentDescription = null
                    )
                },
                colors = coloresItem,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }

        NavigationDrawerItem(
            label = { Text("Cerrar sesión") },
            selected = false,
            onClick = onCerrarSesion,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null
                )
            },
            colors = coloresItem,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )
    }
}