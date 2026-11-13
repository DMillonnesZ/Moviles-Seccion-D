package com.tecsup.millones.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tecsup.millones.ui.theme.RojoCorazon

@Composable
fun MenuProducto(
    expanded: Boolean,
    onDismiss: () -> Unit,
    esFavorito: Boolean = false,
    onFavorito: () -> Unit = {},
    onCompartir: () -> Unit = {},
    onReportar: () -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text(if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos") },
            leadingIcon = {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (esFavorito) RojoCorazon else LocalContentColor.current
                )
            },
            onClick = {
                onFavorito()
                onDismiss()
            }
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text("Compartir") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null
                )
            },
            onClick = {
                onCompartir()
                onDismiss()
            }
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text("Reportar") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null
                )
            },
            onClick = {
                onReportar()
                onDismiss()
            }
        )
    }
}
