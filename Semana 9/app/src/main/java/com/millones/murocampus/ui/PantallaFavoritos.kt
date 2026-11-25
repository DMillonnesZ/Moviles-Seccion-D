package com.millones.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.Menta
import com.millones.murocampus.ui.theme.TextoSecundario
import com.millones.murocampus.viewmodel.PostsViewModel

@Composable
fun PantallaFavoritos(
    vm: PostsViewModel,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onIrAInicio: () -> Unit = {}
) {
    val favoritos by vm.favoritos.collectAsStateWithLifecycle()
    // Los últimos que marcaste salen primero
    val lista = favoritos.values.toList().asReversed()

    if (lista.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Azul
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Aún no tienes favoritos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Toca el corazón de una publicación para guardarla aquí",
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onIrAInicio) {
                Text("Ver publicaciones", color = Menta, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lista, key = { it.id }) { post ->
                TarjetaConCorazon(
                    post = post,
                    esFavorito = true,
                    onFavorito = { vm.alternarFavorito(post) },
                    onClick = { onPostClick(post.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}