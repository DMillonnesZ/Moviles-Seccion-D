package com.millones.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.TextoSecundario
import com.millones.murocampus.viewmodel.PostsViewModel
import com.millones.murocampus.viewmodel.UiState
import kotlinx.coroutines.launch

// null = Todos, luego Usuario 1 al 10
private val opcionesFiltro: List<Int?> = listOf(null) + (1..10).toList()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {

        // Fila de chips desplazable; siempre visible para poder cambiar el filtro
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(opcionesFiltro) { userId ->
                FilterChip(
                    selected = filtro == userId,
                    onClick = {
                        vm.filtrar(userId)
                        // Al cambiar de filtro, la lista vuelve arriba
                        scope.launch { listState.scrollToItem(0) }
                    },
                    label = { Text(if (userId == null) "Todos" else "Usuario $userId") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        labelColor = Azul,
                        selectedContainerColor = Azul,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val e = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorConReintento(e.mensaje) { vm.cargar() }
                is UiState.Exito ->
                    if (e.datos.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Sin publicaciones", color = TextoSecundario)
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            // bottom = 88.dp: deja espacio para que el botón + no tape la última tarjeta
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(e.datos, key = { it.id }) { post ->
                                TarjetaPost(
                                    userId = post.userId,
                                    titulo = post.title,
                                    texto = post.body,
                                    onClick = { onPostClick(post.id) }
                                )
                            }
                        }
                    }
            }
        }
    }
}