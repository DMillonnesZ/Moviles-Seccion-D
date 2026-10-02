
package com.tecsup.millones.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.tecsup.millones.ui.theme.Morado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaTopBar(subtitulo: String) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "TECSUP Store",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Morado,
            titleContentColor = Color.White
        )
    )
}