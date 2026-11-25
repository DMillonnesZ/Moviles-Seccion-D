package com.millones.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.millones.murocampus.ui.theme.RojoFondo
import com.millones.murocampus.ui.theme.RojoTexto
import com.millones.murocampus.ui.theme.TextoSecundario
import com.millones.murocampus.viewmodel.PostsViewModel
import com.millones.murocampus.viewmodel.UiState

private const val MAX_CONTENIDO = 500

@Composable
fun PantallaCrear(
    vm: PostsViewModel,
    modifier: Modifier = Modifier,
    onCancelar: () -> Unit = {}
) {
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
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .vidrio(RoundedCornerShape(28.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Nueva publicación",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it; alEscribir() },
                label = { Text("Título") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                singleLine = true,
                enabled = !enviando,
                shape = RoundedCornerShape(20.dp),
                colors = coloresCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contenido,
                onValueChange = {
                    if (it.length <= MAX_CONTENIDO) {
                        contenido = it
                        alEscribir()
                    }
                },
                label = { Text("Contenido") },
                placeholder = { Text("¿De qué trata tu publicación?") },
                minLines = 4,
                enabled = !enviando,
                shape = RoundedCornerShape(20.dp),
                colors = coloresCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "${contenido.length}/$MAX_CONTENIDO",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )

            // El éxito lo muestra la animación; aquí solo el error, conservando lo escrito
            when (val e = estado) {
                is UiState.Error -> Text(
                    e.mensaje,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RojoFondo, RoundedCornerShape(16.dp))
                        .border(1.dp, RojoTexto.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    color = Color(0xFFFFB4BC),
                    textAlign = TextAlign.Center
                )
                else -> Unit
            }

            BotonPrimario(
                texto = if (enviando) "Publicando…" else "Publicar",
                onClick = { vm.publicar(titulo, contenido) },
                enabled = puedePublicar
            )

            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = TextoSecundario, fontWeight = FontWeight.Bold)
            }
        }
    }
}