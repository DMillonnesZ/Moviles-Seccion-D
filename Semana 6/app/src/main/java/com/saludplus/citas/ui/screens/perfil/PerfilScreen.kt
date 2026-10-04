package com.saludplus.citas.ui.screens.perfil

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.BarraNavegacion

@Composable
fun PerfilScreen(onNavegar: (String) -> Unit) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = { BarraNavegacion(rutaActual = Rutas.PERFIL, onNavegar = onNavegar) }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Perfil (se completa en su commit)")
        }
    }
}