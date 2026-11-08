package com.tecsup.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.pokemon.ui.lista.DatosPrueba
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.theme.PokeDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeDexTheme {
                // Temporal: datos de prueba. Luego irá AppNav()
                ListaScreen(
                    titulo = "Pokémon de prueba",
                    items = DatosPrueba.items,
                    onItemClick = {},
                    onFavoritoClick = {}
                )
            }
        }
    }
}