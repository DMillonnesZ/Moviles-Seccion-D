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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.millones.murocampus.ui.theme.AzulClaro
import com.millones.murocampus.ui.theme.BordeTarjeta
import com.millones.murocampus.ui.theme.RojoTexto
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
    val scope = rememberCoroutineScope()
    var porEliminar by remember { mutableStateOf<Post?>(null) }
    var menuAbierto by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {

        // Barra de búsqueda + botón Filtrar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Azul,
                    unfocusedBorderColor = BordeTarjeta
                ),
                modifier = Modifier.weight(1f)
            )

            Box {
                Button(onClick = { menuAbierto = true }) { Text("Filtrar") }
                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    opcionesFiltro.forEach { userId ->
                        DropdownMenuItem(
                            text = { Text(if (userId == null) "Todos" else "Usuario $userId") },
                            trailingIcon = {
                                if (filtro == userId) Text("✓", color = Azul, fontWeight = FontWeight.Bold)
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
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)) {
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
                        selectedContainerColor = AzulClaro,
                        selectedLabelColor = Azul,
                        selectedTrailingIconColor = Azul
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
                                if (cargandoMas) CircularProgressIndicator()
                                else OutlinedButton(onClick = { vm.cargarMas() }) {
                                    Text("Cargar más")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(e.datos, key = { it.id }) { post ->
                                TarjetaDeslizable(
                                    post = post,
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
                                        if (cargandoMas) CircularProgressIndicator()
                                        else OutlinedButton(onClick = { vm.cargarMas() }) {
                                            Text("Cargar más")
                                        }
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
            title = { Text("Eliminar publicación") },
            text = { Text("¿Quieres eliminar «${post.title.take(60)}»?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.borrar(post.id)
                    porEliminar = null
                }) { Text("Eliminar", color = RojoTexto) }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaDeslizable(
    post: Post,
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
                        .background(RojoTexto, RoundedCornerShape(12.dp))
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
        TarjetaPost(
            userId = post.userId,
            titulo = post.title,
            texto = post.body,
            onClick = onClick
        )
    }
}