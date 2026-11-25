package com.millones.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.millones.murocampus.ui.theme.BordeTarjeta
import com.millones.murocampus.ui.theme.Menta
import com.millones.murocampus.ui.theme.RojoFondo
import com.millones.murocampus.ui.theme.RojoTexto
import com.millones.murocampus.ui.theme.Superficie
import com.millones.murocampus.ui.theme.TextoSecundario

// Colores de la insignia "Usuario N": fondo y texto
private val coloresInsignia = listOf(
    Color(0xFF2B7BD0) to Color.White,        // 1: azul
    Color(0xFFF5A623) to Color(0xFF3A2600),  // 2: naranja
    Color(0xFF2E9E5B) to Color.White,        // 3: verde
    Color(0xFF8E5BD0) to Color.White,        // 4: morado
    Color(0xFFD0475B) to Color.White         // 5: rojo
)

@Composable
fun TarjetaPost(
    userId: Int,
    titulo: String,
    texto: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val (fondo, colorTexto) = coloresInsignia[(userId - 1).mod(coloresInsignia.size)]

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = FormaTarjeta,
        colors = CardDefaults.cardColors(
            containerColor = Superficie.copy(alpha = 0.85f),
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, BordeTarjeta.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Usuario $userId",
                modifier = Modifier
                    .background(fondo, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colorTexto
            )
            Text(
                titulo,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                texto,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
        }
    }
}

@Composable
fun Cargando(mensaje: String = "Cargando publicaciones…") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .vidrio()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Menta,
                trackColor = BordeTarjeta.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(12.dp))
            Text(mensaje, color = TextoSecundario)
        }
    }
}

@Composable
fun ErrorConReintento(mensaje: String, onReintentar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            mensaje,
            modifier = Modifier
                .fillMaxWidth()
                .background(RojoFondo, FormaTarjeta)
                .border(1.dp, RojoTexto.copy(alpha = 0.5f), FormaTarjeta)
                .padding(16.dp),
            color = Color(0xFFFFB4BC),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Reintentar", onReintentar)
    }
}