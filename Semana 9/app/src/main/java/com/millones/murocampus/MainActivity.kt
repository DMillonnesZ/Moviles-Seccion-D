package com.millones.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.murocampus.ui.ExitoAnimado
import com.millones.murocampus.ui.PantallaCrear
import com.millones.murocampus.ui.PantallaDetalle
import com.millones.murocampus.ui.PantallaFavoritos
import com.millones.murocampus.ui.PantallaLista
import com.millones.murocampus.ui.PantallaUsuarios
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.AzulClaro
import com.millones.murocampus.ui.theme.FondoPantalla
import com.millones.murocampus.ui.theme.MuroCampusTheme
import com.millones.murocampus.ui.theme.RojoTexto
import com.millones.murocampus.ui.theme.TextoSecundario
import com.millones.murocampus.viewmodel.EventoUi
import com.millones.murocampus.viewmodel.PostsViewModel
import com.millones.murocampus.viewmodel.PostsViewModelFactory
import com.millones.murocampus.viewmodel.UiState
import kotlinx.coroutines.launch

// Los cuatro apartados de la barra inferior
private enum class Seccion(
    val titulo: String,
    val icono: ImageVector,
    val iconoActivo: ImageVector
) {
    Inicio("Inicio", Icons.Default.Home, Icons.Default.Home),
    Crear("Crear", Icons.Default.Add, Icons.Default.Add),
    Favoritos("Favoritos", Icons.Default.FavoriteBorder, Icons.Default.Favorite),
    Usuarios("Usuarios", Icons.Default.Person, Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                val vm: PostsViewModel = viewModel(factory = PostsViewModelFactory())
                val scope = rememberCoroutineScope()

                var seccion by rememberSaveable { mutableStateOf(Seccion.Inicio) }
                // null = se ve el apartado; con un id = se ve el detalle de esa publicación
                var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
                val listState = rememberLazyListState()
                val snackbar = remember { SnackbarHostState() }

                val evento by vm.evento.collectAsStateWithLifecycle()
                val crearEstado by vm.crear.collectAsStateWithLifecycle()
                val favoritos by vm.favoritos.collectAsStateWithLifecycle()

                // Al publicar con éxito: vuelve a Inicio y sube para ver la nueva publicación
                LaunchedEffect(crearEstado) {
                    if (crearEstado is UiState.Exito) {
                        seccion = Seccion.Inicio
                        idSeleccionado = null
                        vm.limpiarCrear()
                        listState.scrollToItem(0)
                    }
                }

                // Los errores se muestran en un snackbar
                LaunchedEffect(evento) {
                    val e = evento
                    if (e is EventoUi.Error) {
                        snackbar.showSnackbar(e.mensaje)
                        vm.consumirEvento()
                    }
                }

                // Atrás: del detalle vuelve al apartado; de otro apartado vuelve a Inicio
                BackHandler(enabled = idSeleccionado != null || seccion != Seccion.Inicio) {
                    if (idSeleccionado != null) {
                        idSeleccionado = null
                    } else {
                        if (seccion == Seccion.Crear) vm.limpiarCrear()
                        seccion = Seccion.Inicio
                    }
                }

                Box(Modifier.fillMaxSize()) {
                    Scaffold(
                        containerColor = FondoPantalla,
                        snackbarHost = { SnackbarHost(snackbar) },
                        topBar = {
                            TopAppBar(
                                title = { Text("MuroCampus", fontWeight = FontWeight.Bold) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Azul,
                                    titleContentColor = Color.White
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(containerColor = Color.White) {
                                Seccion.entries.forEach { s ->
                                    val activa = seccion == s && idSeleccionado == null
                                    NavigationBarItem(
                                        selected = activa,
                                        onClick = {
                                            // Al salir de Crear con un error pendiente, se limpia
                                            if (seccion == Seccion.Crear && s != Seccion.Crear) {
                                                vm.limpiarCrear()
                                            }
                                            idSeleccionado = null
                                            seccion = s
                                        },
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (s == Seccion.Favoritos && favoritos.isNotEmpty()) {
                                                        Badge(containerColor = RojoTexto) {
                                                            Text("${favoritos.size}")
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (activa) s.iconoActivo else s.icono,
                                                    contentDescription = s.titulo
                                                )
                                            }
                                        },
                                        label = { Text(s.titulo) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Azul,
                                            selectedTextColor = Azul,
                                            indicatorColor = AzulClaro,
                                            unselectedIconColor = TextoSecundario,
                                            unselectedTextColor = TextoSecundario
                                        )
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        val id = idSeleccionado
                        val contenido = Modifier.padding(padding)
                        when {
                            id != null -> PantallaDetalle(vm, id, contenido)
                            seccion == Seccion.Inicio -> PantallaLista(
                                vm = vm,
                                onPostClick = { idSeleccionado = it },
                                modifier = contenido,
                                listState = listState
                            )
                            seccion == Seccion.Crear -> PantallaCrear(vm, contenido)
                            seccion == Seccion.Favoritos -> PantallaFavoritos(
                                vm = vm,
                                onPostClick = { idSeleccionado = it },
                                modifier = contenido
                            )
                            else -> PantallaUsuarios(
                                onUsuarioClick = { userId ->
                                    // Abre Inicio filtrado por ese usuario, sin búsqueda previa
                                    vm.buscar("")
                                    vm.filtrar(userId)
                                    seccion = Seccion.Inicio
                                    scope.launch { listState.scrollToItem(0) }
                                },
                                modifier = contenido
                            )
                        }
                    }

                    // Check animado con confeti por encima de todo
                    val exito = evento as? EventoUi.Exito
                    if (exito != null) {
                        key(exito.id) {
                            ExitoAnimado(
                                mensaje = exito.mensaje,
                                onTerminar = { vm.consumirEvento() }
                            )
                        }
                    }
                }
            }
        }
    }
}