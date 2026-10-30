package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FechaChip(fecha: LocalDate, seleccionado: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (seleccionado) cs.primary else cs.surface,
        contentColor = if (seleccionado) cs.onPrimary else cs.onSurface,
        tonalElevation = 2.dp,
        modifier = Modifier.width(56.dp).clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge
            )
            Text("${fecha.dayOfMonth}", style = MaterialTheme.typography.titleLarge)
        }
    }
}