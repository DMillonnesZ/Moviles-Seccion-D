package com.millones.avisoscampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.millones.avisoscampus.model.Aviso
import com.millones.avisoscampus.model.Origen
import com.millones.avisoscampus.ui.theme.EtiquetaBoton
import com.millones.avisoscampus.ui.theme.EtiquetaModoAvion
import com.millones.avisoscampus.ui.theme.EtiquetaRecordatorio
import com.millones.avisoscampus.ui.theme.TarjetaGris
import com.millones.avisoscampus.ui.theme.TextoRecordatorio
import com.millones.avisoscampus.ui.theme.TextoSecundario

/** Tarjeta Historial: los 20 avisos más recientes. No conoce el ViewModel. */
@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    TarjetaAviso("Historial") {
        if (avisos.isEmpty()) {
            Text("Sin avisos todavía", color = TextoSecundario)
        } else {
            // Column + forEach (no LazyColumn): la pantalla ya se desplaza
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                avisos.take(20).forEach { aviso -> FilaAviso(aviso) }
            }
        }
    }
}

@Composable
private fun FilaAviso(aviso: Aviso) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TarjetaGris, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EtiquetaOrigen(aviso.origen)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                aviso.titulo,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                aviso.texto,
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
        }
        Text(
            aviso.hora,
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario
        )
    }
}

/** Etiqueta de origen: Modo avión (morado), Recordatorio (ámbar) o Botón (azul). */
@Composable
private fun EtiquetaOrigen(origen: Origen) {
    val (fondo, colorTexto) = when (origen) {
        Origen.MODO_AVION -> EtiquetaModoAvion to Color.White
        Origen.RECORDATORIO -> EtiquetaRecordatorio to TextoRecordatorio
        Origen.BOTON -> EtiquetaBoton to Color.White
    }
    Text(
        origen.texto,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        color = colorTexto,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
    )
}
