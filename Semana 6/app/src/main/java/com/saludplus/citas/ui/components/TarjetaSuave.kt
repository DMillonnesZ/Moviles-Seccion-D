package com.saludplus.citas.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Tarjeta blanca; es clicable solo si se pasa onClick
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaSuave(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(16.dp)
    val colores = CardDefaults.cardColors(containerColor = Color.White)
    val elevacion = CardDefaults.cardElevation(defaultElevation = 2.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = forma, colors = colores, elevation = elevacion) {
            Column(Modifier.padding(16.dp), content = contenido)
        }
    } else {
        Card(modifier = modifier, shape = forma, colors = colores, elevation = elevacion) {
            Column(Modifier.padding(16.dp), content = contenido)
        }
    }
}