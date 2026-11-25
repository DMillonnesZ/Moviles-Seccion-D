package com.millones.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.BordeTarjeta
import com.millones.murocampus.ui.theme.TarjetaGris
import com.millones.murocampus.ui.theme.TextoSecundario
import com.millones.murocampus.viewmodel.DetalleUi
import com.millones.murocampus.viewmodel.PostsViewModel
import com.millones.murocampus.viewmodel.UiState

@Composable
fun PantallaDetalle(vm: PostsViewModel, id: Int, modifier: Modifier = Modifier) {
    LaunchedEffect(id) { vm.abrir(id) }
    val estado by vm.detalle.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val e = estado) {
            UiState.Cargando -> Cargando("Cargando publicación…")
            is UiState.Error -> ErrorConReintento(e.mensaje) { vm.abrir(id, forzar = true) }
            is UiState.Exito ->
                // Evita mostrar un instante el detalle de la publicación anterior
                if (e.datos.post.id != id) Cargando("Cargando publicación…")
                else Contenido(e.datos)
        }
    }
}

@Composable
private fun Contenido(detalle: DetalleUi) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeTarjeta)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        detalle.post.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        detalle.post.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }
            }
        }

        item {
            Text(
                "Comentarios (${detalle.comentarios.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Azul
            )
        }

        items(detalle.comentarios, key = { it.id }) { c ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaGris)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        c.email,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )
                    Text(
                        c.body,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                }
            }
        }
    }
}