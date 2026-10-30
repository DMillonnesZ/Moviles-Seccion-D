package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.ui.theme.GrisSelector

/** ocupado = gris y tachado, no se puede tocar */
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
        ocupado -> GrisSelector
        seleccionado -> cs.primary
        else -> cs.surface
    }
    val borde = when {
        ocupado -> GrisSelector
        seleccionado -> cs.primary
        else -> BordeSuave
    }
    val texto = when {
        ocupado -> cs.onSurfaceVariant.copy(alpha = 0.5f)
        seleccionado -> cs.onPrimary
        else -> cs.onSurface
    }
    Box(
        modifier
            .height(42.dp)
            .background(fondo, RoundedCornerShape(12.dp))
            .border(1.dp, borde, RoundedCornerShape(12.dp))
            .clickable(enabled = !ocupado, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            hora,
            color = texto,
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (ocupado) TextDecoration.LineThrough else null,
            style = MaterialTheme.typography.labelLarge
        )
    }
}