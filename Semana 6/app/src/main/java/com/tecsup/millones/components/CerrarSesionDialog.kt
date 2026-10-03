package com.tecsup.millones.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.tecsup.millones.ui.theme.Morado

@Composable
fun CerrarSesionDialog(
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cerrar sesión") },
        text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Cerrar sesión", color = Morado)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
