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
fun PerfilMedicoScreen(
    nombre: String,
    especialidad: String,
    calificacion: String,
    onAgendarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Perfil del médico"
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = nombre
                )

                Text(
                    text = especialidad
                )

                Text(
                    text = "★ $calificacion"
                )
            }
        }

        Button(
            onClick = onAgendarClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Agendar cita"
            )
        }
    }
}