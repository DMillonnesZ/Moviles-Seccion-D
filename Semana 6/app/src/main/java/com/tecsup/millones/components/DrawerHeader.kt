package com.tecsup.millones.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.millones.ui.theme.Morado
import com.tecsup.millones.ui.theme.MoradoIcono

@Composable
fun DrawerHeader(
    nombre: String,
    correo: String,
    modifier: Modifier = Modifier
) {
    // Iniciales a partir del nombre: "Maria Rojas" -> "MR"
    val iniciales = nombre
        .split(" ")
        .mapNotNull { it.firstOrNull() }
        .take(2)
        .joinToString("")

    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar circular con iniciales
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MoradoIcono),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = Morado,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(16.dp))

            // Nombre y correo del usuario
            Column {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = correo,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
    }
}