package com.tecsup.pokemon.ui.components

import android.os.Build
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

// Muestra un GIF o WebP animado guardado en res/raw, en bucle
@Composable
fun AnimacionRaw(
    @RawRes recurso: Int,
    modifier: Modifier = Modifier,
    descripcion: String? = null
) {
    val context = LocalContext.current

    // Cargador de imágenes que sabe reproducir animaciones
    val cargador = remember(context) {
        ImageLoader.Builder(context)
            .components {
                // Android 9+: GIF y WebP animado; antes solo GIF
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(recurso)
            .build(),
        contentDescription = descripcion,
        imageLoader = cargador,
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}