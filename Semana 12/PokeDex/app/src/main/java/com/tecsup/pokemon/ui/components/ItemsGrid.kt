package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.Item

// Grid de Pokémon reutilizado en Lista y Favoritos
@Composable
fun ItemsGrid(
        items: List<Item>,
        onItemClick: (Int) -> Unit,
        onFavoritoClick: (Int) -> Unit
) {
        LazyVerticalGrid(
                columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
        ItemCard(
                item = item,
                onClick = { onItemClick(item.id) },
        onFavoritoClick = { onFavoritoClick(item.id) }
        )
        }
        }
        }

        @Composable
        private fun ItemCard(
                item: Item,
                onClick: () -> Unit,
        onFavoritoClick: () -> Unit
) {
        Card(
                modifier = Modifier
                        .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box {
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
            ) {
                AsyncImage(
                        model = item.imagenUrl,
                        contentDescription = item.nombre,
                        modifier = Modifier.size(110.dp)
                )
                Text(
                        text = "#%03d".format(item.id),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                )
                Text(
                        text = item.nombre.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                    onClick = onFavoritoClick,
                    modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                        imageVector = if (item.esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
            }