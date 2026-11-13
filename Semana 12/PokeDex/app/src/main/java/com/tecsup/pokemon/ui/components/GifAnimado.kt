package com.tecsup.pokemon.ui.components

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

private val CremaEstado = Color(0xFFFFF4DF)
private val DoradoClaroEstado = Color(0xFFFFE7A0)
private val DoradoBordeEstado = Color(0xFFD9A43A)

// GIF animado desde res/drawable-nodpi.
// pixelado = true dibuja sin suavizar (para sprites pixel art)
@Composable
fun GifAnimado(
    @DrawableRes recurso: Int,
    modifier: Modifier = Modifier,
    pixelado: Boolean = false,
    descripcion: String? = null
) {
    val context = LocalContext.current
    val solicitud = remember(recurso) {
        ImageRequest.Builder(context)
            .data(recurso)
            // Android 9+ usa ImageDecoder; antes, el decodificador GIF de Coil
            .decoderFactory(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoderDecoder.Factory()
                else GifDecoder.Factory()
            )
            .build()
    }
    AsyncImage(
        model = solicitud,
        contentDescription = descripcion,
        contentScale = ContentScale.Fit,
        filterQuality = if (pixelado) FilterQuality.None else FilterQuality.Low,
        modifier = modifier
    )
}

// Tarjeta crema con borde dorado, centrada (para error y vacío)
@Composable
fun TarjetaEstado(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(24.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, forma)
                .clip(forma)
                .background(Brush.verticalGradient(listOf(Color.White, CremaEstado)))
                .border(1.5.dp, Brush.verticalGradient(listOf(DoradoClaroEstado, DoradoBordeEstado)), forma)
                .padding(horizontal = 20.dp, vertical = 22.dp),
            content = contenido
        )
    }
}