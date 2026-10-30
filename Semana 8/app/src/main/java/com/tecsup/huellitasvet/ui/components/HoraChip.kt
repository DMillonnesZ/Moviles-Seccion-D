package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun HoraChip(
    hora: String,
    seleccionado: Boolean,
    ocupado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val fondo = when {
        ocupado -> Color.LightGray.copy(alpha = 0.4f)
        seleccionado -> cs.primary
        else -> cs.primaryContainer
    }
    val texto = when {
        ocupado -> Color.Gray
        seleccionado -> cs.onPrimary
        else -> cs.onPrimaryContainer
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = fondo,
        modifier = modifier.clickable(enabled = !ocupado, onClick = onClick)
    ) {
        Text(
            hora,
            color = texto,
            textAlign = TextAlign.Center,
            textDecoration = if (ocupado) TextDecoration.LineThrough else null,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}