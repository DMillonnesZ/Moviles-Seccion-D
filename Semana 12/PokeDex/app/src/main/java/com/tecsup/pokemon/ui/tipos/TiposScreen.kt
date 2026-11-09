package com.tecsup.pokemon.ui.tipos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
    onFavoritosClick: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PokeDex") },
                actions = {
                    IconButton(onClick = onFavoritosClick) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Favoritos",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Aviso de modo sin conexión
            aviso?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp)
                )
            }

            when (val e = estado) {
                is UiState.Cargando -> Cargando("Descargando tipos...")
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::refrescar)
                is UiState.Exito ->
                    if (e.datos.isEmpty()) VacioView("No hay tipos guardados")
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