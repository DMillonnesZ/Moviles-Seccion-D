package com.tecsup.millones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tecsup.millones.components.TiendaTopBar
import com.tecsup.millones.screens.InicioScreen
import com.tecsup.millones.ui.theme.TecsupStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TecsupStoreTheme {
                Scaffold(
                    topBar = { TiendaTopBar(subtitulo = "Más vendidos") }
                ) { padding ->
                    InicioScreen(modifier = Modifier.padding(padding))
                }
            }
        }
    }
}