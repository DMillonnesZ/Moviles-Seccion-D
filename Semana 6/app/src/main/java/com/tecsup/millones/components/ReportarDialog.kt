package com.tecsup.millones.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.tecsup.millones.ui.theme.Morado

@Composable
fun ReportarDialog(
    nombreProducto: String,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar producto") },
        text = { Text("¿Quieres reportar este producto ($nombreProducto)?") },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Reportar", color = Morado)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
