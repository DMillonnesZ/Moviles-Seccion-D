package com.millones.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.murocampus.ui.BotonIcono
import com.millones.murocampus.ui.Encabezado
import com.millones.murocampus.ui.ExitoAnimado
import com.millones.murocampus.ui.FondoDegradado
import com.millones.murocampus.ui.PantallaCrear
import com.millones.murocampus.ui.PantallaDetalle
import com.millones.murocampus.ui.PantallaFavoritos
import com.millones.murocampus.ui.PantallaLista
import com.millones.murocampus.ui.PantallaUsuarios
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.Menta
import com.millones.murocampus.ui.theme.MuroCampusTheme
import com.millones.murocampus.ui.theme.RojoFondo
import com.millones.murocampus.ui.theme.RojoTexto
import com.millones.murocampus.ui.theme.TextoSecundario
import com.millones.murocampus.ui.vidrio
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El fondo siempre es oscuro: los íconos de las barras del sistema deben ser claros
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
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

                FondoDegradado {
                    Scaffold(
                        containerColor = Color.Transparent,
                        snackbarHost = {
                            SnackbarHost(snackbar) { data ->
                                Snackbar(
                                    snackbarData = data,
                                    shape = RoundedCornerShape(16.dp),
                                    containerColor = RojoFondo,
                                    contentColor = Color.White
                                )
                            }
                        },
                        topBar = {
                            val id = idSeleccionado
                            when {
                                id != null -> Encabezado(
                                    titulo = "Publicación",
                                    subtitulo = "Detalle y comentarios",
                                    inicio = {
                                        BotonIcono(
                                            icono = Icons.AutoMirrored.Filled.ArrowBack,
                                            descripcion = "Volver",
                                            onClick = { idSeleccionado = null }
                                        )
                                    }
                                )
                                seccion == Seccion.Inicio -> Encabezado(
                                    titulo = "Muro",
                                    acento = "Campus",
                                    subtitulo = "Lo que pasa en el campus",
                                    fin = {
                                        BotonIcono(
                                            icono = Icons.Default.Add,
                                            descripcion = "Nueva publicación",
                                            onClick = { seccion = Seccion.Crear },
                                            destacado = true
                                        )
                                    }
                                )
                                seccion == Seccion.Crear -> Encabezado(
                                    titulo = "Crear",
                                    subtitulo = "Comparte algo con el campus"
                                )
                                seccion == Seccion.Favoritos -> Encabezado(
                                    titulo = "Favoritos",
                                    subtitulo = "Tus publicaciones guardadas"
                                )
                                else -> Encabezado(
                                    titulo = "Usuarios",
                                    subtitulo = "Explora por autor"
                                )
                            }
                        },
                        bottomBar = {
                            BarraInferior(
                                seleccionada = if (idSeleccionado == null) seccion else null,
                                favoritos = favoritos.size,
                                onSeleccion = { s ->
                                    // Al salir de Crear con un error pendiente, se limpia
                                    if (seccion == Seccion.Crear && s != Seccion.Crear) {
                                        vm.limpiarCrear()
                                    }
                                    idSeleccionado = null
                                    seccion = s
                                }
                            )
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
                            seccion == Seccion.Crear -> PantallaCrear(
                                vm = vm,
                                modifier = contenido,
                                onCancelar = {
                                    vm.limpiarCrear()
                                    seccion = Seccion.Inicio
                                }
                            )
                            seccion == Seccion.Favoritos -> PantallaFavoritos(
                                vm = vm,
                                onPostClick = { idSeleccionado = it },
                                modifier = contenido,
                                onIrAInicio = { seccion = Seccion.Inicio }
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

/** Barra flotante con esquinas redondeadas; la pestaña activa lleva borde y una línea menta debajo. */
@Composable
private fun BarraInferior(
    seleccionada: Seccion?,
    favoritos: Int,
    onSeleccion: (Seccion) -> Unit
) {
    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .fillMaxWidth()
            .vidrio(RoundedCornerShape(28.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Seccion.entries.forEach { s ->
            val activa = s == seleccionada
            val color = if (activa) Menta else TextoSecundario
            val forma = RoundedCornerShape(20.dp)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(forma)
                    .then(
                        if (activa) {
                            Modifier
                                .background(Azul.copy(alpha = 0.12f))
                                .border(1.dp, Azul.copy(alpha = 0.55f), forma)
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onSeleccion(s) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BadgedBox(
                    badge = {
                        if (s == Seccion.Favoritos && favoritos > 0) {
                            Badge(containerColor = RojoTexto) { Text("$favoritos") }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (activa) s.iconoActivo else s.icono,
                        contentDescription = s.titulo,
                        tint = color
                    )
                }
                Text(
                    s.titulo,
                    color = color,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(3.dp)
                        .background(
                            if (activa) Menta else Color.Transparent,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}