package com.millones.murocampus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.lifecycleScope
import com.millones.murocampus.data.remote.RetrofitClient
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.FondoPantalla
import com.millones.murocampus.ui.theme.MuroCampusTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // PRUEBA TEMPORAL: se borra antes del commit
        lifecycleScope.launch {
            try {
                val posts = RetrofitClient.api.listar()
                Log.d("MuroCampus", "Llegaron ${posts.size} publicaciones")
            } catch (e: Exception) {
                Log.e("MuroCampus", "Falló la llamada", e)
            }
        }

        setContent {
            MuroCampusTheme {
                Scaffold(
                    containerColor = FondoPantalla,
                    topBar = {
                        TopAppBar(
                            title = { Text("MuroCampus", fontWeight = FontWeight.Bold) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Azul,
                                titleContentColor = Color.White
                            )
                        )
                    }
                ) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding)) // pantalla vacía
                }
            }
        }
    }
}