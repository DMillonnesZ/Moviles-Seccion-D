package com.millones.campusgps.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.millones.campusgps.model.Lugar
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.CampoCampus
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.vidrioMapa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone

// Formulario para publicar un evento: imagen, nombre, descripción, fecha, hora y lugar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioEvento(
    lugares: List<Lugar>,
    guardando: Boolean,
    onPublicar: (titulo: String, descripcion: String, fecha: Long?, lugar: Lugar?, fotoUri: String?) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var lugarId by rememberSaveable { mutableStateOf<String?>(null) }
    // Día elegido (medianoche UTC, como lo da el DatePicker) y hora
    var diaUtc by rememberSaveable { mutableStateOf<Long?>(null) }
    var hora by rememberSaveable { mutableStateOf<Int?>(null) }
    var minuto by rememberSaveable { mutableStateOf(0) }
    var verCalendario by remember { mutableStateOf(false) }
    var verReloj by remember { mutableStateOf(false) }

    val elegirFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) fotoUri = uri.toString() }

    // Fecha final en hora local (null hasta tener día y hora)
    val fecha: Long? = diaUtc?.let { dia ->
        hora?.let { h ->
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = dia }
            Calendar.getInstance().apply {
                clear()
                set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH), h, minuto)
            }.timeInMillis
        }
    }

    val escala = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Dialog(
        onDismissRequest = onCancelar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 24.dp)
                .imePadding()
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .vidrioMapa(RoundedCornerShape(28.dp), fondo = VidrioMapa.FondoSolido.copy(alpha = 0.97f))
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nuevo evento",
                    color = Color.White,
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(enabled = !guardando, onClick = onCancelar)
                        .padding(6.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            PortadaEvento(
                uri = fotoUri,
                onClick = {
                    elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )

            Spacer(Modifier.height(16.dp))

            CampoCampus(
                valor = titulo,
                onValorChange = { titulo = it },
                placeholder = "Nombre del evento",
                icono = Icons.Rounded.Event,
                habilitado = !guardando,
                valido = titulo.isNotBlank(),
                alto = 56.dp
            )

            Spacer(Modifier.height(12.dp))

            CampoDescripcion(
                valor = descripcion,
                onValorChange = { descripcion = it },
                habilitado = !guardando,
                maximo = 500,
                placeholder = "¿De qué trata? ¿Qué hay que llevar?"
            )

            Spacer(Modifier.height(14.dp))

            // Fecha y hora
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                BotonDato(
                    icono = Icons.Rounded.CalendarMonth,
                    texto = fecha?.let { formatearFecha(it, "EEE d MMM") }
                        ?: diaUtc?.let { "Día elegido" }
                        ?: "Fecha",
                    elegido = diaUtc != null,
                    onClick = { verCalendario = true },
                    modifier = Modifier.weight(1f)
                )
                BotonDato(
                    icono = Icons.Rounded.Schedule,
                    texto = hora?.let { "%02d:%02d".format(it, minuto) } ?: "Hora",
                    elegido = hora != null,
                    onClick = { verReloj = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Lugar (de los registrados en el mapa)
            Text(
                text = "¿Dónde?",
                color = Color.White.copy(alpha = 0.75f),
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            if (lugares.isEmpty()) {
                Text(
                    text = "Registra lugares en el mapa (mantén presionado) para elegirlos aquí.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontFamily = FuenteCampus,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    ChipLugar(texto = "Sin lugar", activo = lugarId == null, onClick = { lugarId = null })
                    lugares.forEach { lugar ->
                        ChipLugar(
                            texto = lugar.nombre,
                            activo = lugar.id == lugarId,
                            onClick = { lugarId = lugar.id }
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            BotonCampus(
                texto = "Publicar evento",
                onClick = {
                    onPublicar(titulo, descripcion, fecha, lugares.firstOrNull { it.id == lugarId }, fotoUri)
                },
                cargando = guardando,
                alto = 54.dp
            )
            Text(
                text = "Cancelar",
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable(enabled = !guardando, onClick = onCancelar)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    // ----- Calendario -----
    if (verCalendario) {
        val estadoFecha = rememberDatePickerState(
            initialSelectedDateMillis = diaUtc ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { verCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    diaUtc = estadoFecha.selectedDateMillis
                    verCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { verCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }

    // ----- Reloj -----
    if (verReloj) {
        val estadoHora = rememberTimePickerState(
            initialHour = hora ?: 12,
            initialMinute = minuto,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { verReloj = false },
            title = { Text("Hora del evento") },
            text = { TimePicker(state = estadoHora) },
            confirmButton = {
                TextButton(onClick = {
                    hora = estadoHora.hour
                    minuto = estadoHora.minute
                    verReloj = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { verReloj = false }) { Text("Cancelar") }
            }
        )
    }
}

// Portada del evento: la imagen elegida o un recuadro para agregarla
@Composable
private fun PortadaEvento(uri: String?, onClick: () -> Unit) {
    val contexto = LocalContext.current
    val imagen by produceState<ImageBitmap?>(initialValue = null, uri) {
        value = uri?.let {
            withContext(Dispatchers.IO) {
                runCatching {
                    contexto.contentResolver.openInputStream(Uri.parse(it))?.use { entrada ->
                        BitmapFactory.decodeStream(entrada, null, BitmapFactory.Options().apply { inSampleSize = 4 })
                            ?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }
    val forma = RoundedCornerShape(20.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(forma)
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.5.dp, VidrioMapa.Cian.copy(alpha = 0.4f), forma)
            .clickable(onClick = onClick)
    ) {
        if (imagen != null) {
            Image(
                bitmap = imagen!!,
                contentDescription = "Imagen del evento",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null, tint = VidrioMapa.Cian, modifier = Modifier.size(36.dp))
                Spacer(Modifier.height(6.dp))
                Text("Agregar imagen", color = Color.White.copy(alpha = 0.75f), fontFamily = FuenteCampus, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun BotonDato(
    icono: ImageVector,
    texto: String,
    elegido: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(forma)
            .background(if (elegido) VidrioMapa.Cian.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.07f))
            .border(1.5.dp, if (elegido) VidrioMapa.Cian else Color.White.copy(alpha = 0.22f), forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Icon(icono, contentDescription = null, tint = if (elegido) VidrioMapa.Cian else Color.White.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = texto,
            color = Color.White,
            fontFamily = FuenteCampus,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ChipLugar(texto: String, activo: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    Text(
        text = texto,
        color = if (activo) Color.White else Color.White.copy(alpha = 0.75f),
        fontFamily = FuenteCampus,
        fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium,
        fontSize = 13.sp,
        maxLines = 1,
        modifier = Modifier
            .clip(forma)
            .background(if (activo) CampusColores.AzulCielo.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (activo) CampusColores.AzulCielo else Color.White.copy(alpha = 0.15f), forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}
