package com.millones.clinicasaludplus.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Medico(
    val nombre: String,
    val especialidad: String,
    val calificacion: Double
)

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier
) {

    val especialidades = listOf(
        "Cardiología",
        "Pediatría",
        "Dermatología"
    )

    val medicos = listOf(
        Medico(
            nombre = "Dra. Ana Torres",
            especialidad = "Cardiología",
            calificacion = 4.9
        ),
        Medico(
            nombre = "Dr. Luis Vega",
            especialidad = "Pediatría",
            calificacion = 4.8
        ),
        Medico(
            nombre = "Dra. Rosa Díaz",
            especialidad = "Dermatología",
            calificacion = 4.7
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Especialidades"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            items(especialidades) { especialidad ->

                Card {
                    Text(
                        text = especialidad,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                    )
                }
            }
        }

        Text(
            text = "Médicos"
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            items(medicos) { medico ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = medico.nombre
                        )

                        Text(
                            text = medico.especialidad
                        )

                        Text(
                            text = "★ ${medico.calificacion}"
                        )
                    }
                }
            }
        }
    }
}