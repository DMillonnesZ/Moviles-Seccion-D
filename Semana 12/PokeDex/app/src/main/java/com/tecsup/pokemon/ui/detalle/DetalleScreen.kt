package com.tecsup.pokemon.ui.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val esFavorito by viewModel.esFavorito.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::alternarFavorito) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val e = estado) {
                is UiState.Cargando -> Cargando("Cargando detalle...")
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::cargar)
                is UiState.Exito -> DetalleContenido(e.datos)
            }
        }
    }
}

@Composable
private fun DetalleContenido(detalle: ItemDetalle) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = detalle.imagenUrl,
            contentDescription = detalle.nombre,
            modifier = Modifier.size(220.dp)
        )

        Text(
            text = "#%03d".format(detalle.id),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = detalle.nombre.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // Tipos
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            detalle.tipos.forEach { tipo ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = tipo.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Altura y peso
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Dato(titulo = "Altura", valor = "${detalle.alturaM} m")
            Dato(titulo = "Peso", valor = "${detalle.pesoKg} kg")
        }

        Spacer(Modifier.height(16.dp))

        // Habilidades
        Text("Habilidades", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            text = detalle.habilidades.joinToString(", ") {
                it.replace("-", " ").replaceFirstChar { c -> c.uppercase() }
            },
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(16.dp))

        // Stats
        Text("Estadísticas base", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        detalle.stats.forEach { (nombre, valor) ->
            StatBarra(nombre = nombreStat(nombre), valor = valor)
        }
    }
}

@Composable
private fun Dato(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(titulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun StatBarra(nombre: String, valor: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(nombre, modifier = Modifier.width(110.dp), style = MaterialTheme.typography.bodyMedium)
        Text(valor.toString(), modifier = Modifier.width(36.dp), fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { valor / 255f },   // 255 es el máximo de un stat
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
        )
    }
}

// Nombre del stat en español
private fun nombreStat(nombre: String): String = when (nombre) {
    "hp" -> "PS"
    "attack" -> "Ataque"
    "defense" -> "Defensa"
    "special-attack" -> "Ataque Esp."
    "special-defense" -> "Defensa Esp."
    "speed" -> "Velocidad"
    else -> nombre
}