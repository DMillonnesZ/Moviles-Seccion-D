package com.millones.campusgps.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.millones.campusgps.model.BorradorLugar
import com.millones.campusgps.model.CATEGORIAS_LUGAR
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.CampoCampus
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.CianCampus
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.estiloCategoria
import com.millones.campusgps.ui.componentes.vidrioMapa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

// Ventana de vidrio para registrar un lugar en el punto elegido del mapa:
// foto, título, descripción y categoría. Entra rebotando.
@Composable
fun FormularioLugar(
    borrador: BorradorLugar,
    guardando: Boolean,
    onGuardar: (titulo: String, descripcion: String, categoria: String, fotoUri: String?) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf(CATEGORIAS_LUGAR.first()) }
    var fotoUri by rememberSaveable { mutableStateOf<String?>(null) }

    val elegirFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) fotoUri = uri.toString() }

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
                .padding(horizontal = 20.dp, vertical = 24.dp)
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
            // Cabecera: título y cerrar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nuevo lugar",
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

            // Dónde queda
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = CianCampus, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = borrador.direccion
                        ?: "%.5f, %.5f".format(Locale.US, borrador.latitud, borrador.longitud),
                    color = CianCampus,
                    fontFamily = FuenteCampus,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(18.dp))

            // Foto circular: toca para elegir
            SelectorFoto(
                uri = fotoUri,
                categoria = categoria,
                onClick = {
                    elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )
            Text(
                text = if (fotoUri == null) "Agregar foto" else "Cambiar foto",
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = FuenteCampus,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(Modifier.height(18.dp))

            CampoCampus(
                valor = titulo,
                onValorChange = { titulo = it },
                placeholder = "Título del lugar",
                icono = Icons.Rounded.Place,
                habilitado = !guardando,
                valido = titulo.isNotBlank(),
                alto = 56.dp
            )

            Spacer(Modifier.height(12.dp))

            CampoDescripcion(
                valor = descripcion,
                onValorChange = { descripcion = it },
                habilitado = !guardando
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Categoría",
                color = Color.White.copy(alpha = 0.75f),
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                CATEGORIAS_LUGAR.forEach { opcion ->
                    ChipCategoria(
                        texto = opcion,
                        activa = opcion == categoria,
                        onClick = { categoria = opcion }
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            BotonCampus(
                texto = "Guardar lugar",
                onClick = { onGuardar(titulo, descripcion, categoria, fotoUri) },
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
}

// Círculo grande con la foto elegida o un ícono de cámara del color de la categoría
@Composable
private fun SelectorFoto(uri: String?, categoria: String, onClick: () -> Unit) {
    val contexto = LocalContext.current
    val color = estiloCategoria(categoria).color

    // Vista previa reducida (solo para mostrar; la copia definitiva la hace el repositorio)
    val imagen by produceState<ImageBitmap?>(initialValue = null, uri) {
        value = uri?.let {
            withContext(Dispatchers.IO) {
                runCatching {
                    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
                    contexto.contentResolver.openInputStream(Uri.parse(it))?.use { entrada ->
                        BitmapFactory.decodeStream(entrada, null, opciones)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(104.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(color.copy(alpha = 0.55f), color.copy(alpha = 0.18f))))
            .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
            .clickable(onClick = onClick)
    ) {
        Crossfade(targetState = imagen, label = "vistaPrevia") { foto ->
            if (foto != null) {
                Image(
                    bitmap = foto,
                    contentDescription = "Foto elegida",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Rounded.AddAPhoto,
                        contentDescription = "Elegir foto",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

// Campo de varias líneas para la descripción (mismo estilo de vidrio que CampoCampus)
@Composable
private fun CampoDescripcion(
    valor: String,
    onValorChange: (String) -> Unit,
    habilitado: Boolean
) {
    val interaccion = remember { MutableInteractionSource() }
    val enfocado by interaccion.collectIsFocusedAsState()
    val colorBorde by animateColorAsState(
        targetValue = if (enfocado) CianCampus else Color.White.copy(alpha = 0.22f),
        label = "bordeDescripcion"
    )
    val forma = RoundedCornerShape(18.dp)

    BasicTextField(
        value = valor,
        onValueChange = { if (it.length <= 160) onValorChange(it) },
        enabled = habilitado,
        interactionSource = interaccion,
        textStyle = TextStyle(color = Color.White, fontFamily = FuenteCampus, fontSize = 15.sp),
        cursorBrush = SolidColor(CianCampus),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .clip(forma)
            .background(if (enfocado) CianCampus.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.07f))
            .border(1.5.dp, colorBorde, forma)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        decorationBox = { campo ->
            Box {
                if (valor.isEmpty()) {
                    Text(
                        text = "Descripción (por ejemplo: segundo piso, junto a la escalera)",
                        color = Color.White.copy(alpha = 0.55f),
                        fontFamily = FuenteCampus,
                        fontSize = 15.sp
                    )
                }
                campo()
            }
        }
    )
    Text(
        text = "${valor.length}/160",
        color = Color.White.copy(alpha = 0.45f),
        fontFamily = FuenteCampus,
        fontSize = 11.sp,
        textAlign = TextAlign.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, end = 6.dp)
    )
}

// Píldora de categoría con su ícono; la activa se pinta con su color
@Composable
private fun ChipCategoria(texto: String, activa: Boolean, onClick: () -> Unit) {
    val estilo = estiloCategoria(texto)
    val color = if (estilo.color == CampusColores.TextoSecundario) Color(0xFFB8C4D9) else estilo.color
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(forma)
            .background(if (activa) color.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (activa) color else Color.White.copy(alpha = 0.15f), forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(estilo.icono, contentDescription = null, tint = if (activa) color else Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            color = if (activa) Color.White else Color.White.copy(alpha = 0.75f),
            fontFamily = FuenteCampus,
            fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}
