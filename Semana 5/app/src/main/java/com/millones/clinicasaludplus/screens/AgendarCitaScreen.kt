package com.millones.clinicasaludplus.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AgendarCitaScreen(
    nombre: String,
    especialidad: String,
    onConfirmarClick: (String, String) -> Unit
) {
    val fechas = listOf(
        "Lun 28 Sep",
        "Mar 29 Sep",
        "Mié 30 Sep",
        "Jue 01 Oct",
        "Vie 02 Oct"
    )

    val horarios = listOf(
        "09:00 AM",
        "10:00 AM",
        "11:00 AM",
        "03:00 PM",
        "04:00 PM"
    )

    var fechaSeleccionada by remember {
        mutableStateOf("")
    }

    var horarioSeleccionado by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Agendar cita"
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = nombre)
                Text(text = especialidad)
            }
        }

        Text(
            text = "Selecciona una fecha"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(fechas) { fecha ->

                Button(
                    onClick = {
                        fechaSeleccionada = fecha
                    }
                ) {
                    Text(text = fecha)
                }
            }
        }

        Text(
            text = "Selecciona un horario"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(horarios) { horario ->

                Button(
                    onClick = {
                        horarioSeleccionado = horario
                    }
                ) {
                    Text(text = horario)
                }
            }
        }

        Button(
            onClick = {
                onConfirmarClick(
                    fechaSeleccionada,
                    horarioSeleccionado
                )
            },
            enabled = fechaSeleccionada.isNotEmpty() &&
                    horarioSeleccionado.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Confirmar cita"
            )
        }
    }
}