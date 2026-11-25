package com.millones.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.murocampus.ui.PantallaCrear
import com.millones.murocampus.ui.PantallaDetalle
import com.millones.murocampus.ui.PantallaLista
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.FondoPantalla
import com.millones.murocampus.ui.theme.MuroCampusTheme
import com.millones.murocampus.viewmodel.PostsViewModel
import com.millones.murocampus.viewmodel.PostsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                val vm: PostsViewModel = viewModel(factory = PostsViewModelFactory())

                // null = se ve la lista; con un id = se ve el detalle
                var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
                var creando by rememberSaveable { mutableStateOf(false) }
                // Se guarda aquí para que la lista conserve el scroll al volver
                val listState = rememberLazyListState()

                // Atrás del sistema: de crear o del detalle vuelve a la lista
                BackHandler(enabled = creando) {
                    creando = false
                    vm.limpiarCrear()
                }
                BackHandler(enabled = !creando && idSeleccionado != null) {
                    idSeleccionado = null
                }

                Scaffold(
                    containerColor = FondoPantalla,
                    topBar = {
                        TopAppBar(
                            title = { Text("MuroCampus", fontWeight = FontWeight.Bold) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Azul,
                                titleContentColor = Color.White
                            )
                        )
                    },
                    floatingActionButton = {
                        // El + solo aparece en la lista
                        if (!creando && idSeleccionado == null) {
                            FloatingActionButton(
                                onClick = { creando = true },
                                containerColor = Azul,
                                contentColor = Color.White
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
                            }
                        }
                    }
                ) { padding ->
                    val id = idSeleccionado
                    when {
                        creando -> PantallaCrear(vm, Modifier.padding(padding))
                        id != null -> PantallaDetalle(vm, id, Modifier.padding(padding))
                        else -> PantallaLista(
                            vm = vm,
                            onPostClick = { idSeleccionado = it },
                            modifier = Modifier.padding(padding),
                            listState = listState
                        )
                    }
                }
            }
        }
    }
}