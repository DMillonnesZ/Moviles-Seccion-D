package com.millones.campusgps.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.millones.campusgps.model.LugarConDistancia
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.estiloCategoria
import com.millones.campusgps.ui.componentes.rememberImagenLugar
import com.millones.campusgps.ui.componentes.vidrioMapa
import com.millones.campusgps.util.formatearDistancia
import java.util.Locale

// Detalle completo de un lugar: imagen grande (se abre a pantalla completa), título,
// categoría, distancia, descripción y acciones
@Composable
fun DetalleLugar(
    item: LugarConDistancia,
    esActual: Boolean,
    onCerrar: () -> Unit,
    onFavorito: () -> Unit,
    onCambiarFoto: () -> Unit,
    onVerEnMapa: () -> Unit,
    onEliminar: () -> Unit
) {
    val lugar = item.lugar
    val estilo = estiloCategoria(lugar.categoria)
    val imagen = rememberImagenLugar(lugar.foto, ladoMaximo = 900)
    var verImagen by rememberSaveable { mutableStateOf(false) }

    val escala = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 24.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .vidrioMapa(RoundedCornerShape(28.dp), fondo = VidrioMapa.FondoSolido.copy(alpha = 0.97f))
                .verticalScroll(rememberScrollState())
        ) {
            // ----- Imagen -----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(Brush.verticalGradient(listOf(estilo.color.copy(alpha = 0.6f), VidrioMapa.FondoSolido)))
                    .clickable(enabled = imagen != null) { verImagen = true }
            ) {
                if (imagen != null) {
                    Image(
                        bitmap = imagen,
                        contentDescription = "Foto de ${lugar.nombre}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Degradado abajo para que el título se lea
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0.55f to Color.Transparent,
                                    1f to VidrioMapa.FondoSolido.copy(alpha = 0.9f)
                                )
                            )
                    )
                    BotonCircular(
                        icono = Icons.Rounded.ZoomOutMap,
                        descripcion = "Ver imagen completa",
                        onClick = { verImagen = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(estilo.icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Sin foto",
                            color = Color.White.copy(alpha = 0.7f),
                            fontFamily = FuenteCampus,
                            fontSize = 13.sp
                        )
                    }
                }

                BotonCircular(
                    icono = Icons.Rounded.Close,
                    descripcion = "Cerrar",
                    onClick = onCerrar,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )
                // Solo el autor puede cambiar la foto
                if (lugar.esMio) {
                    BotonCircular(
                        icono = Icons.Rounded.PhotoCamera,
                        descripcion = "Cambiar foto",
                        onClick = onCambiarFoto,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // ----- Título y estrella -----
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lugar.nombre,
                        color = Color.White,
                        fontFamily = FuenteCampus,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                        modifier = Modifier.weight(1f)
                    )
                    EstrellaFavorito(activa = lugar.favorito, onClick = onFavorito)
                }

                // Quién lo registró
                Text(
                    text = if (lugar.esMio) "Registrado por ti"
                    else "Registrado por ${lugar.autorNombre.ifBlank { "otro usuario" }}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = FuenteCampus,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(10.dp))

                // ----- Etiquetas -----
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Etiqueta(
                        texto = lugar.categoria,
                        color = lerp(estilo.color, Color.White, 0.3f)
                    )
                    item.metros?.let {
                        Etiqueta(texto = "~ ${formatearDistancia(it)}", color = Color.White.copy(alpha = 0.85f))
                    }
                    if (esActual) {
                        Etiqueta(texto = "Estás aquí", color = Color(0xFFFF8A8A))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ----- Descripción -----
                Text(
                    text = "Descripción",
                    color = Color.White.copy(alpha = 0.5f),
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = lugar.descripcion.ifBlank { "Este lugar no tiene descripción." },
                    color = Color.White.copy(alpha = if (lugar.descripcion.isBlank()) 0.5f else 0.9f),
                    fontFamily = FuenteCampus,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(14.dp))

                // ----- Coordenadas -----
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = VidrioMapa.Cian, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "%.5f, %.5f".format(Locale.US, lugar.latitud, lugar.longitud),
                        color = VidrioMapa.Cian,
                        fontFamily = FuenteCampus,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                BotonCampus(texto = "Ver en el mapa", onClick = onVerEnMapa, alto = 52.dp)

                Spacer(Modifier.height(8.dp))

                // Solo el autor puede eliminarlo
                if (lugar.esMio) Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onEliminar)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = Color(0xFFFF8A8A), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Eliminar lugar",
                        color = Color(0xFFFF8A8A),
                        fontFamily = FuenteCampus,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    if (verImagen && lugar.foto != null) {
        VisorImagen(ruta = lugar.foto, titulo = lugar.nombre, onCerrar = { verImagen = false })
    }
}

// Imagen a pantalla completa: pellizca para acercar, arrastra para mover, doble toque para volver
@Composable
private fun VisorImagen(ruta: String, titulo: String, onCerrar: () -> Unit) {
    val imagen = rememberImagenLugar(ruta, ladoMaximo = 1600)
    var zoom by remember { mutableFloatStateOf(1f) }
    var desplazamiento by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, mover, cambioZoom, _ ->
                        zoom = (zoom * cambioZoom).coerceIn(1f, 5f)
                        desplazamiento = if (zoom == 1f) Offset.Zero else desplazamiento + mover
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        zoom = if (zoom > 1f) 1f else 2.5f
                        desplazamiento = Offset.Zero
                    })
                }
        ) {
            if (imagen != null) {
                Image(
                    bitmap = imagen,
                    contentDescription = titulo,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = zoom
                            scaleY = zoom
                            translationX = desplazamiento.x
                            translationY = desplazamiento.y
                        }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)))
                    .windowInsetsPadding(InsetsCampus)
                    .padding(16.dp)
            ) {
                Text(
                    text = titulo,
                    color = Color.White,
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f)
                )
                BotonCircular(icono = Icons.Rounded.Close, descripcion = "Cerrar imagen", onClick = onCerrar)
            }
        }
    }
}

@Composable
private fun BotonCircular(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.45f))
            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick)
    ) {
        Icon(icono, contentDescription = descripcion, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun Etiqueta(texto: String, color: Color) {
    Text(
        text = texto,
        color = color,
        fontFamily = FuenteCampus,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
