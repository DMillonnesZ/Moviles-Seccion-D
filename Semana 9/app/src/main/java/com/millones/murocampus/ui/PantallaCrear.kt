package com.millones.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.millones.murocampus.viewmodel.PostsViewModel
import com.millones.murocampus.viewmodel.UiState

@Composable
fun PantallaCrear(vm: PostsViewModel, modifier: Modifier = Modifier) {
    val estado by vm.crear.collectAsStateWithLifecycle()
    var titulo by rememberSaveable { mutableStateOf("") }
    var contenido by rememberSaveable { mutableStateOf("") }

    val enviando = estado is UiState.Cargando
    val puedePublicar = titulo.isNotBlank() && contenido.isNotBlank() && !enviando

    // Al volver a escribir, desaparece el mensaje de error anterior
    fun alEscribir() {
        if (estado is UiState.Error) vm.limpiarCrear()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it; alEscribir() },
            label = { Text("Título") },
            singleLine = true,
            enabled = !enviando,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it; alEscribir() },
            label = { Text("Contenido") },
            minLines = 4,
            enabled = !enviando,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { vm.publicar(titulo, contenido) },
            enabled = puedePublicar
        ) {
            Text(if (enviando) "Publicando…" else "Publicar")
        }

        // El éxito lo muestra la animación; aquí solo el error, conservando lo escrito
        when (val e = estado) {
            is UiState.Error -> Text(
                e.mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            else -> Unit
        }
    }
}