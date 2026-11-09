package com.tecsup.pokemon.ui.tipos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.ErrorView
import com.tecsup.pokemon.ui.components.VacioView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiposScreen(
    viewModel: TiposViewModel,
    onTipoClick: (String) -> Unit,
    onFavoritosClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val sincronizando by viewModel.sincronizando.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PokeDex") },
                actions = {
                    IconButton(
                        onClick = viewModel::refrescar,
                        enabled = !sincronizando
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Sincronizar")
                    }
                    IconButton(onClick = onFavoritosClick) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Favoritos",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Progreso mientras sincroniza sin ocultar los datos
            if (sincronizando && estado is UiState.Exito) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Aviso de modo sin conexión con opción de reintentar
            aviso?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = viewModel::refrescar,
                        enabled = !sincronizando
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            when (val e = estado) {
                is UiState.Cargando -> Cargando("Descargando tipos...")
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::refrescar)
                is UiState.Exito ->
                    if (e.datos.isEmpty()) VacioView("No hay tipos guardados.\nToca sincronizar para descargarlos.")
                    else TiposGrid(e.datos, onTipoClick)
            }
        }
    }
}

@Composable
private fun TiposGrid(
    tipos: List<Tipo>,
    onTipoClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tipos, key = { it.nombre }) { tipo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTipoClick(tipo.nombre) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    AsyncImage(
                        model = tipo.iconoUrl,
                        contentDescription = tipo.nombreEs,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = tipo.nombreEs,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}