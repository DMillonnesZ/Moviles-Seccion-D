package com.millones.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.campusgps.model.UbicacionUi
import com.millones.campusgps.viewmodel.CampusViewModelFactory
import com.millones.campusgps.viewmodel.MapaViewModel

@Composable
fun PantallaMapa() {
    val vm: MapaViewModel = viewModel(factory = CampusViewModelFactory(LocalContext.current))
    val estado by vm.estado.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { vm.buscar() },
            enabled = estado !is UbicacionUi.Buscando
        ) {
            Text("Obtener mi ubicación")
        }

        when (val e = estado) {
            UbicacionUi.Inicial -> Text("Pulsa el botón para empezar")
            UbicacionUi.Buscando -> Text("Buscando...")
            is UbicacionUi.Lista -> Text(
                "Latitud: ${e.posicion.latitud}\nLongitud: ${e.posicion.longitud}"
            )
            is UbicacionUi.Error -> Text(
                e.mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
