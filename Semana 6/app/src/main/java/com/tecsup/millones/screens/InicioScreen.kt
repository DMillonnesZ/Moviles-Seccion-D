package com.tecsup.millones.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.millones.components.TarjetaProducto
import com.tecsup.millones.model.productosEjemplo

@Composable
fun InicioScreen(modifier: Modifier = Modifier) {
    // Si conservas el LazyRow de categorías del Lab 4, colócalo aquí arriba
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(productosEjemplo, key = { it.id }) { producto ->
            TarjetaProducto(producto = producto)
        }
    }
}