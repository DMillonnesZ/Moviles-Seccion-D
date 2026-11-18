package com.millones.campusgps.ui.componentes

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

// Lee la foto de un lugar del disco fuera del hilo principal, reducida a ladoMaximo px
// (los círculos usan poco; la vista completa usa más)
@Composable
fun rememberImagenLugar(ruta: String?, ladoMaximo: Int = 256): ImageBitmap? {
    val imagen by produceState<ImageBitmap?>(initialValue = null, ruta, ladoMaximo) {
        value = ruta?.let { cargarImagen(it, ladoMaximo) }
    }
    return imagen
}

// Imagen guardada como texto Base64 dentro de un documento de Firestore (eventos)
@Composable
fun rememberImagenBase64(base64: String?): ImageBitmap? {
    val imagen by produceState<ImageBitmap?>(initialValue = null, base64) {
        value = base64?.let {
            withContext(Dispatchers.Default) {
                runCatching {
                    val bytes = Base64.decode(it, Base64.NO_WRAP)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                }.getOrNull()
            }
        }
    }
    return imagen
}

private suspend fun cargarImagen(ruta: String, ladoMaximo: Int): ImageBitmap? = withContext(Dispatchers.IO) {
    runCatching {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, medidas)
        var muestra = 1
        while (max(medidas.outWidth, medidas.outHeight) / (muestra * 2) >= ladoMaximo) muestra *= 2
        BitmapFactory.decodeFile(ruta, BitmapFactory.Options().apply { inSampleSize = muestra })?.asImageBitmap()
    }.getOrNull()
}

// Ícono circular del lugar: la foto que subió el usuario o, si no hay, el ícono de su categoría.
// Con onCambiarFoto muestra una camarita y al tocarlo abre el selector de fotos.
@Composable
fun FotoLugar(
    ruta: String?,
    categoria: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 48.dp,
    colorBorde: Color = Color.White.copy(alpha = 0.85f),
    onCambiarFoto: (() -> Unit)? = null
) {
    val estilo = estiloCategoria(categoria)
    val imagen = rememberImagenLugar(ruta)

    Box(modifier = modifier.size(tamano)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .shadow(6.dp, CircleShape, ambientColor = estilo.color, spotColor = estilo.color)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(lerp(estilo.color, Color.White, 0.25f), estilo.color)))
                .border(2.5.dp, colorBorde, CircleShape)
                .then(if (onCambiarFoto != null) Modifier.clickable(onClick = onCambiarFoto) else Modifier)
        ) {
            Crossfade(targetState = imagen, label = "fotoLugar") { foto ->
                if (foto != null) {
                    Image(
                        bitmap = foto,
                        contentDescription = "Foto del lugar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = estilo.icono,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(tamano * 0.46f)
                        )
                    }
                }
            }
        }

        // Camarita: indica que se puede subir o cambiar la foto
        if (onCambiarFoto != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(tamano * 0.4f)
                    .clip(CircleShape)
                    .background(VidrioMapa.Cian)
                    .border(1.5.dp, VidrioMapa.FondoSolido, CircleShape)
                    .clickable(onClick = onCambiarFoto)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PhotoCamera,
                    contentDescription = "Cambiar foto",
                    tint = VidrioMapa.FondoSolido,
                    modifier = Modifier.size(tamano * 0.24f)
                )
            }
        }
    }
}

// Marcador de un evento: recuadro violeta con el día y el mes, y una puntita
@Composable
fun PinEvento(dia: String, mes: String, asisto: Boolean) {
    val violeta = CampusColores.Violeta
    val borde = if (asisto) CampusColores.VerdeMenta else Color.White
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(borde)
                .padding(2.5.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.verticalGradient(listOf(lerp(violeta, Color.White, 0.2f), violeta)))
                .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
            Text(
                text = dia,
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                lineHeight = 18.sp
            )
            Text(
                text = mes,
                color = Color.White.copy(alpha = 0.85f),
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                lineHeight = 11.sp
            )
        }
        Canvas(modifier = Modifier.size(width = 14.dp, height = 9.dp)) {
            val punta = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(punta, borde)
        }
    }
}

// Marcador de una persona que comparte su ubicación: inicial en círculo violeta y su nombre
@Composable
fun PinPersona(nombre: String) {
    val violeta = CampusColores.Violeta
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = nombre.substringBefore(" ").take(14),
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 1,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(violeta)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(top = 3.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(3.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(lerp(violeta, Color.White, 0.3f), violeta)))
        ) {
            Text(
                text = nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
    }
}

// Marcador del mapa: círculo con la foto del lugar (o el ícono de su categoría),
// borde de color y una puntita abajo que señala el punto exacto.
// Se dibuja como imagen (MarkerComposable), por eso la foto llega ya cargada.
@Composable
fun PinFoto(
    imagen: ImageBitmap?,
    categoria: String,
    colorBorde: Color,
    tamano: Dp = 54.dp
) {
    val estilo = estiloCategoria(categoria)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(tamano)
                .clip(CircleShape)
                .background(colorBorde)
                .padding(3.dp)   // borde de color alrededor de la foto
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(lerp(estilo.color, Color.White, 0.25f), estilo.color)))
            ) {
                if (imagen != null) {
                    Image(
                        bitmap = imagen,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = estilo.icono,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(tamano * 0.45f)
                    )
                }
            }
        }
        // Puntita
        Canvas(modifier = Modifier.size(width = 16.dp, height = 10.dp)) {
            val punta = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(punta, colorBorde)
        }
    }
}
