package com.millones.murocampus.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.millones.murocampus.model.Post
import com.millones.murocampus.ui.theme.RojoTexto
import com.millones.murocampus.ui.theme.TextoSecundario

/** TarjetaPost con un corazón arriba a la derecha para marcar favorito. */
@Composable
fun TarjetaConCorazon(
    post: Post,
    esFavorito: Boolean,
    onFavorito: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        TarjetaPost(
            userId = post.userId,
            titulo = post.title,
            texto = post.body,
            onClick = onClick
        )
        IconButton(
            onClick = onFavorito,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
                .size(36.dp)
        ) {
            Icon(
                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                tint = if (esFavorito) RojoTexto else TextoSecundario
            )
        }
    }
}