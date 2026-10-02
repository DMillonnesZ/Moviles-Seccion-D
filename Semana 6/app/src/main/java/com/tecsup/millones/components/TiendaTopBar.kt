package com.tecsup.millones.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun TiendaTopBar(
    subtitulo: String,
    onMenuClick: () -> Unit
) {
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
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Morado,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}