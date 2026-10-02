package com.tecsup.millones.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    rutaActual: String,
    onDestinoClick: (Destino) -> Unit,
    onCerrarSesion: () -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(24.dp))

        // Un ítem por cada destino; el activo se marca con selected = true
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )
    }
}