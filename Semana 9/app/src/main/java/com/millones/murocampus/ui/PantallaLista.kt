package com.millones.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.millones.murocampus.model.Post
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.AzulBoton1
import com.millones.murocampus.ui.theme.Menta
import com.millones.murocampus.ui.theme.RojoAccion
import com.millones.murocampus.ui.theme.RojoTexto
import com.millones.murocampus.ui.theme.TarjetaGris
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
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val hayMas by vm.hayMas.collectAsStateWithLifecycle()
    val cargandoMas by vm.cargandoMas.collectAsStateWithLifecycle()
    val favoritos by vm.favoritos.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var porEliminar by remember { mutableStateOf<Post?>(null) }
    var menuAbierto by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {

        // Barra de búsqueda + botón Filtrar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = {
                    vm.buscar(it)
                    scope.launch { listState.scrollToItem(0) }
                },
                placeholder = { Text("Buscar por título") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { vm.buscar("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = coloresCampo(),
                modifier = Modifier.weight(1f)
            )

            Box {
                Button(
                    onClick = { menuAbierto = true },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulBoton1,
                        contentColor = Color.White
                    )
                ) {
                    Text("Filtrar", fontWeight = FontWeight.Bold)
                }
                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = TarjetaGris
                ) {
                    opcionesFiltro.forEach { userId ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (userId == null) "Todos" else "Usuario $userId",
                                    color = Color.White
                                )
                            },
                            trailingIcon = {
                                if (filtro == userId) {
                                    Text("✓", color = Menta, fontWeight = FontWeight.Bold)
                                }
                            },
                            onClick = {
                                menuAbierto = false
                                vm.filtrar(userId)
                                scope.launch { listState.scrollToItem(0) }
                            }
                        )
                    }
                }
            }
        }

        // Etiqueta del filtro activo, con ✕ para quitarlo
        if (filtro != null) {
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
                InputChip(
                    selected = true,
                    onClick = { vm.filtrar(null) },
                    label = { Text("Usuario $filtro") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Quitar filtro",
                            modifier = Modifier.width(18.dp)
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = Menta.copy(alpha = 0.15f),
                        selectedLabelColor = Menta,
                        selectedTrailingIconColor = Menta
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
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                if (busqueda.isNotBlank()) "Sin resultados para «${busqueda.trim()}»"
                                else "Sin publicaciones",
                                color = TextoSecundario
                            )
                            // La búsqueda solo mira lo descargado: aquí se pueden traer más
                            if (hayMas || cargandoMas) {
                                Spacer(Modifier.height(12.dp))
                                if (cargandoMas) CircularProgressIndicator(color = Menta)
                                else BotonSecundario("Cargar más", { vm.cargarMas() })
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(e.datos, key = { it.id }) { post ->
                                TarjetaDeslizable(
                                    post = post,
                                    esFavorito = post.id in favoritos,
                                    onFavorito = { vm.alternarFavorito(post) },
                                    onClick = { onPostClick(post.id) },
                                    onSolicitarEliminar = { porEliminar = post },
                                    modifier = Modifier.animateItem()
                                )
                            }

                            // Botón al final de la lista
                            if (hayMas || cargandoMas) {
                                item(key = "cargar-mas") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (cargandoMas) CircularProgressIndicator(color = Menta)
                                        else BotonSecundario("Cargar más", { vm.cargarMas() })
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    porEliminar?.let { post ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            shape = RoundedCornerShape(24.dp),
            containerColor = TarjetaGris,
            titleContentColor = Color.White,
            textContentColor = TextoSecundario,
            title = { Text("Eliminar publicación", fontWeight = FontWeight.Bold) },
            text = { Text("¿Quieres eliminar «${post.title.take(60)}»?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.borrar(post.id)
                    porEliminar = null
                }) { Text("Eliminar", color = RojoTexto, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) {
                    Text("Cancelar", color = TextoSecundario)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaDeslizable(
    post: Post,
    esFavorito: Boolean,
    onFavorito: () -> Unit,
    onClick: () -> Unit,
    onSolicitarEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado = rememberSwipeToDismissBoxState(
        confirmValueChange = { valor ->
            if (valor == SwipeToDismissBoxValue.EndToStart) onSolicitarEliminar()
            false // la tarjeta siempre vuelve a su sitio; desaparece solo si el borrado funciona
        }
    )

    SwipeToDismissBox(
        state = estado,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            // Solo se dibuja mientras se desliza, para que no asome en las esquinas
            if (estado.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(RojoAccion, FormaTarjeta)
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                }
            }
        }
    ) {
        TarjetaConCorazon(
            post = post,
            esFavorito = esFavorito,
            onFavorito = onFavorito,
            onClick = onClick
        )
    }
}