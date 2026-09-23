package com.millones.clinicasaludplus.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmacionScreen(
    nombre: String,
    especialidad: String,
    fecha: String,
    horario: String,
    onFinalizarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Cita confirmada"
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Tu cita ha sido registrada correctamente."
                )

                Text(
                    text = "Médico: $nombre"
                )

                Text(
                    text = "Especialidad: $especialidad"
                )

                Text(
                    text = "Fecha: $fecha"
                )

                Text(
                    text = "Horario: $horario"
                )
            }
        }

        Button(
            onClick = onFinalizarClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Volver al inicio"
            )
        }
    }
}