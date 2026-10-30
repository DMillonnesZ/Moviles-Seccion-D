package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FechaChip(fecha: LocalDate, seleccionado: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val dia = fecha.dayOfWeek
        .getDisplayName(TextStyle.SHORT, Locale("es"))
        .replace(".", "")
        .replaceFirstChar { it.uppercase() }

    Column(
        Modifier
            .width(58.dp)
            .background(if (seleccionado) cs.primary else cs.surface, RoundedCornerShape(14.dp))
            .border(1.dp, if (seleccionado) cs.primary else BordeSuave, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            dia,
            style = MaterialTheme.typography.labelSmall,
            color = if (seleccionado) cs.onPrimary else cs.onSurfaceVariant
        )
        Text(
            "${fecha.dayOfMonth}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (seleccionado) cs.onPrimary else cs.onSurface
        )
    }
}