package com.tecsup.pokemon.ui.favoritos

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LetreroSeccion
import com.tecsup.pokemon.ui.components.TarjetaPokemon
import com.tecsup.pokemon.ui.components.TipoChip
import com.tecsup.pokemon.ui.components.colorDeTipo
import com.tecsup.pokemon.ui.components.tipoParaColor
import com.tecsup.pokemon.util.Traductor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

private val CremaPoster = Color(0xFFFFF4DF)
private val DoradoClaroPoster = Color(0xFFFFE7A0)
private val DoradoPoster = Color(0xFFF5C451)
private val RojoPoster = Color(0xFFE53935)
private val VerdeClaroPoster = Color(0xFF7BD857)
private val VerdePoster = Color(0xFF35A82C)

// Pokémon por imagen (4 filas de 3); si hay más, se generan varias imágenes
private const val POR_POSTER = 12

// Ancho con el que la tarjeta se ve bien (el de la pantalla Favoritos, 2 columnas)
private val ANCHO_NATURAL_TARJETA = 174.dp

// Ventana con la vista previa de los pósters de favoritos y las opciones para compartir
@Composable
fun DialogoCompartirFavoritos(
    favoritos: List<Item>,
    conteoTipos: List<Pair<String, Int>>,
    tiposInfo: Map<String, Tipo>,
    onCompartirTexto: () -> Unit,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current
    val alcance = rememberCoroutineScope()
    // Los favoritos repartidos en grupos de 12: un póster por grupo
    val paginas = remember(favoritos) { favoritos.chunked(POR_POSTER) }
    // Una capa por póster, donde se "fotografía" cada uno
    val capas = remember(paginas.size) { arrayOfNulls<GraphicsLayer>(paginas.size) }

    // Espera a que carguen las imágenes antes de permitir compartir
    var listo by remember { mutableStateOf(false) }
    var compartiendo by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1200L + paginas.size * 300L)
        listo = true
    }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(horizontal = 12.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Vista previa",
                color = Color.White,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(10.dp))

            // Pósters uno debajo del otro (se deslizan)
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                paginas.forEachIndexed { indice, grupo ->
                    key(indice) {
                        val capa = rememberGraphicsLayer()
                        SideEffect { capas[indice] = capa }

                        if (paginas.size > 1) {
                            Text(
                                text = "Imagen ${indice + 1} de ${paginas.size}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontFamily = FuenteMadera,
                                fontSize = 14.sp
                            )
                        }
                        PosterFavoritos(
                            grupo = grupo,
                            totalFavoritos = favoritos.size,
                            pagina = indice + 1,
                            totalPaginas = paginas.size,
                            conteoTipos = conteoTipos,
                            tiposInfo = tiposInfo,
                            modifier = Modifier.drawWithContent {
                                // Graba lo que se dibuja en la capa y luego la muestra
                                capa.record { this@drawWithContent.drawContent() }
                                drawLayer(capa)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Botones
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotonPoster(
                    texto = when {
                        !listo -> "Preparando..."
                        paginas.size > 1 -> "Compartir ${paginas.size} imágenes"
                        else -> "Compartir imagen"
                    },
                    icono = Icons.Rounded.Image,
                    fondo = Brush.verticalGradient(listOf(VerdeClaroPoster, VerdePoster)),
                    colorTexto = Color.White,
                    habilitado = listo && !compartiendo,
                    cargando = !listo || compartiendo,
                    onClick = {
                        alcance.launch {
                            compartiendo = true
                            runCatching {
                                // Fotografía cada póster y lo guarda como PNG
                                val uris = capas.mapIndexed { indice, capa ->
                                    val imagen = requireNotNull(capa) { "Póster ${indice + 1} sin dibujar" }.toImageBitmap()
                                    guardarPoster(context, imagen, indice + 1)
                                }
                                compartirImagenes(context, uris)
                            }.onFailure { error ->
                                // Muestra el motivo para saber qué falló
                                Log.e("PosterFavoritos", "No se pudo compartir la imagen", error)
                                Toast.makeText(
                                    context,
                                    "No se pudo crear la imagen: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            compartiendo = false
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                BotonPoster(
                    texto = "Compartir texto",
                    icono = Icons.AutoMirrored.Rounded.Notes,
                    fondo = Brush.verticalGradient(listOf(DoradoClaroPoster, DoradoPoster)),
                    colorTexto = CafeMadera,
                    habilitado = true,
                    cargando = false,
                    onClick = onCompartirTexto,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Cerrar",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = FuenteMadera,
                fontSize = 15.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onCerrar)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

// Póster: fondo del bosque, letrero, resumen (con número de página), tarjetas en 3 columnas y pie
@Composable
private fun PosterFavoritos(
    grupo: List<Item>,
    totalFavoritos: Int,
    pagina: Int,
    totalPaginas: Int,
    conteoTipos: List<Pair<String, Int>>,
    tiposInfo: Map<String, Tipo>,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(24.dp)
    val tipoFavorito = conteoTipos.firstOrNull()?.first

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(forma)
    ) {
        // Fondo
        Image(
            painter = painterResource(R.drawable.fondo_inicio),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp)
        ) {
            LetreroSeccion(
                texto = "Mis favoritos",
                alto = 60.dp,
                tamanoTexto = 20.sp,
                icono = {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(22.dp))
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(Modifier.height(8.dp))

            // Resumen: cantidad, tipo favorito y página
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CremaPoster.copy(alpha = 0.95f))
                        .border(1.dp, CafeMadera.copy(alpha = 0.25f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RojoPoster, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = buildString {
                            append("$totalFavoritos Pokémon")
                            if (tipoFavorito != null) {
                                append(" · Tipo favorito: ")
                                append(tiposInfo[tipoFavorito]?.nombreEs ?: Traductor.tipoEs(tipoFavorito))
                            }
                        },
                        color = CafeMadera,
                        fontFamily = FuenteMadera,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                if (totalPaginas > 1) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "$pagina/$totalPaginas",
                        color = CafeMadera,
                        fontFamily = FuenteMadera,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Brush.verticalGradient(listOf(DoradoClaroPoster, DoradoPoster)))
                            .border(1.dp, CafeMadera.copy(alpha = 0.4f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Tarjetas en 3 columnas: cada una se arma con su ancho normal y se achica entera
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                grupo.chunked(3).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                Escalado(anchoNatural = ANCHO_NATURAL_TARJETA) {
                                    TarjetaPokemon(
                                        item = item,
                                        colorPrincipal = colorDeTipo(tipoParaColor(item.tipos) ?: "normal"),
                                        chips = item.tipos
                                            .mapNotNull { nombre -> tiposInfo[nombre]?.let { TipoChip(nombre, it.nombreEs, it.iconoUrl) } }
                                            .take(2),
                                        onClick = {},
                                        onFavoritoClick = {}
                                    )
                                }
                            }
                        }
                        // Huecos para completar la última fila
                        repeat(3 - fila.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            // Pie
            Text(
                text = "Hecho con mi PokeDex",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 13.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brush.verticalGradient(listOf(DoradoClaroPoster, DoradoPoster)))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            )
        }
    }
}

// Arma el contenido con un ancho fijo (anchoNatural) y lo achica para que entre
// en el espacio disponible, como una foto reducida: nada se aprieta ni se corta
@Composable
private fun Escalado(
    anchoNatural: Dp,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    Layout(content = contenido, modifier = modifier) { medibles, restricciones ->
        val natural = anchoNatural.roundToPx()
        val disponible = restricciones.maxWidth
        val escala = (disponible / natural.toFloat()).coerceAtMost(1f)
        val pieza = medibles.first().measure(Constraints.fixedWidth(natural))
        val ancho = (natural * escala).roundToInt()
        val alto = (pieza.height * escala).roundToInt()
        layout(ancho, alto) {
            pieza.placeWithLayer(0, 0) {
                scaleX = escala
                scaleY = escala
                transformOrigin = TransformOrigin(0f, 0f)
            }
        }
    }
}

// Botón en forma de píldora para la vista previa
@Composable
private fun BotonPoster(
    texto: String,
    icono: ImageVector,
    fondo: Brush,
    colorTexto: Color,
    habilitado: Boolean,
    cargando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(48.dp)
            .shadow(4.dp, forma)
            .clip(forma)
            .background(fondo)
            .border(1.5.dp, Color.White.copy(alpha = 0.6f), forma)
            .clickable(enabled = habilitado, onClick = onClick)
            .padding(horizontal = 10.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(color = colorTexto, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
        } else {
            Icon(icono, contentDescription = null, tint = colorTexto, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            color = colorTexto,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

// Guarda un póster como PNG en la caché y devuelve su dirección para compartirlo
private suspend fun guardarPoster(context: Context, imagen: ImageBitmap, numero: Int): Uri = withContext(Dispatchers.IO) {
    val carpeta = File(context.cacheDir, "compartidos").apply { mkdirs() }
    // Nombre distinto cada vez: así ninguna app reutiliza una imagen vieja
    val archivo = File(carpeta, "mis_favoritos_${System.currentTimeMillis()}_$numero.png")
    // Copia en memoria normal (las imágenes de la GPU no se pueden comprimir directo)
    val bitmap = imagen.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)
    FileOutputStream(archivo).use { salida ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
    }
    FileProvider.getUriForFile(context, "${context.packageName}.archivos", archivo)
}

// Abre el selector de apps con las imágenes (una o varias) y da permiso de lectura a la app elegida
private fun compartirImagenes(context: Context, uris: List<Uri>) {
    // Todas las direcciones van en el ClipData para que el permiso pase por el selector
    val clip = ClipData.newRawUri("Mis favoritos", uris.first()).apply {
        uris.drop(1).forEach { addItem(ClipData.Item(it)) }
    }
    val intent = if (uris.size == 1) {
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uris.first())
        }
    } else {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
        }
    }.apply {
        type = "image/png"
        clipData = clip
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val selector = Intent.createChooser(intent, "Compartir póster").apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(selector)
}