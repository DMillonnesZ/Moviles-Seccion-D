package com.millones.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.murocampus.model.Usuario
import com.millones.murocampus.ui.theme.AzulBoton1
import com.millones.murocampus.ui.theme.AzulBoton2
import com.millones.murocampus.ui.theme.BordeTarjeta
import com.millones.murocampus.ui.theme.Menta
import com.millones.murocampus.ui.theme.Superficie
import com.millones.murocampus.ui.theme.TextoSecundario
import com.millones.murocampus.viewmodel.UiState
import com.millones.murocampus.viewmodel.UsuariosViewModel
import com.millones.murocampus.viewmodel.UsuariosViewModelFactory

@Composable
fun PantallaUsuarios(
    onUsuarioClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    // Se crea al entrar por primera vez a esta pestaña, no al abrir la app
    vm: UsuariosViewModel = viewModel(factory = UsuariosViewModelFactory())
) {
    val estado by vm.usuarios.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val e = estado) {
            UiState.Cargando -> Cargando("Cargando usuarios…")
            is UiState.Error -> ErrorConReintento(e.mensaje) { vm.cargar() }
            is UiState.Exito -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Toca un usuario para ver sus publicaciones",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                items(e.datos, key = { it.id }) { usuario ->
                    TarjetaUsuario(usuario) { onUsuarioClick(usuario.id) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaUsuario(usuario: Usuario, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = FormaTarjeta,
        colors = CardDefaults.cardColors(
            containerColor = Superficie.copy(alpha = 0.85f),
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, BordeTarjeta.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.linearGradient(listOf(AzulBoton1, AzulBoton2)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    usuario.name.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    usuario.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    usuario.email,
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Usuario ${usuario.id}",
                    color = Menta,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextoSecundario
            )
        }
    }
}