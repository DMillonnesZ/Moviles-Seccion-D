package com.tecsup.pokemon.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R
import kotlin.math.max
import kotlin.math.roundToInt

private val CremaPanel = Color(0xFFFFF4DF)
private val AmarilloBrillo = Color(0xFFFFE066)
private val LuzCalida = Color(0xFFFFC94D)
private val LuzClara = Color(0xFFFFF1B8)

private val DegradadoLetrero = Brush.verticalGradient(
    listOf(Color(0xFFAD671C), Color(0xFF693106), Color(0xFF692B04))
)

// Carga una imagen de drawable con escalado de alta calidad (bordes suaves al estirarla)
@Composable
fun pintorNitido(@DrawableRes id: Int): Painter {
    val imagen = ImageBitmap.imageResource(id)
    return remember(imagen) { BitmapPainter(imagen, filterQuality = FilterQuality.High) }
}

// Márgenes como fracción del ancho (izq, der) y del alto (arr, aba) de la imagen del marco
data class AjusteMarco(
    val izq: Float,
    val der: Float,
    val arr: Float,
    val aba: Float
)

// Bordes de la madera medidos en la imagen:
// exterior = donde empieza la madera; interior = donde termina (empieza el hueco)
private data class MedidasMarco(
    val exterior: AjusteMarco,
    val interior: AjusteMarco
)

// Valores de respaldo si no se puede medir la imagen
private val MEDIDAS_RESPALDO = MedidasMarco(
    exterior = AjusteMarco(0.06f, 0.06f, 0.05f, 0.07f),
    interior = AjusteMarco(0.10f, 0.10f, 0.09f, 0.12f)
)

// Lee los píxeles del marco por la mitad de cada lado y encuentra dónde está la madera.
// Tolera huecos pequeños (brillos o partes semitransparentes) dentro de la madera o los adornos.
// Requiere que el interior del marco sea transparente.
private fun medirMarco(context: Context, @DrawableRes id: Int): MedidasMarco {
    val opciones = BitmapFactory.Options().apply { inScaled = false }
    val imagen = BitmapFactory.decodeResource(context.resources, id, opciones) ?: return MEDIDAS_RESPALDO

    val ancho = imagen.width
    val alto = imagen.height
    val cx = ancho / 2
    val cy = alto / 2
    // Hueco máximo que se considera parte de la madera (en píxeles de la imagen)
    val tolerancia = (minOf(ancho, alto) * 0.02f).roundToInt().coerceAtLeast(4)

    fun esMadera(x: Int, y: Int) = (imagen.getPixel(x, y) ushr 24) > 60

    // Recorre desde "inicio" hacia "paso" hasta "limite": devuelve (inicio de madera, fin de madera)
    fun recorrer(inicio: Int, limite: Int, paso: Int, pixel: (Int) -> Boolean): Pair<Int, Int> {
        var i = inicio
        while (i != limite && !pixel(i)) i += paso
        val exterior = i
        var ultimoMadera = i
        while (i != limite) {
            if (pixel(i)) {
                ultimoMadera = i
            } else if (kotlin.math.abs(i - ultimoMadera) > tolerancia) {
                break
            }
            i += paso
        }
        return exterior to (ultimoMadera + paso)
    }

    val (izqE, izqI) = recorrer(0, cx, 1) { esMadera(it, cy) }
    val (derE, derI) = recorrer(ancho - 1, cx, -1) { esMadera(it, cy) }
    val (arrE, arrI) = recorrer(0, cy, 1) { esMadera(cx, it) }
    val (abaE, abaI) = recorrer(alto - 1, cy, -1) { esMadera(cx, it) }

    imagen.recycle()

    // Si el interior no es transparente, la medición no sirve
    if (izqI >= cx - 1 || arrI >= cy - 1) return MEDIDAS_RESPALDO

    return MedidasMarco(
        exterior = AjusteMarco(
            izqE / ancho.toFloat(), (ancho - 1 - derE) / ancho.toFloat(),
            arrE / alto.toFloat(), (alto - 1 - abaE) / alto.toFloat()
        ),
        interior = AjusteMarco(
            izqI / ancho.toFloat(), (ancho - 1 - derI) / ancho.toFloat(),
            arrI / alto.toFloat(), (alto - 1 - abaI) / alto.toFloat()
        )
    )
}

// Luz cálida: resplandor redondo en el centro y dos líneas de luz a los lados
private fun DrawScope.dibujarLuzCalida(
    centro: Offset,
    radio: Float,
    separacion: Float,   // distancia del centro al inicio de cada línea
    largoLinea: Float,
    intensidad: Float
) {
    // Resplandor
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(LuzCalida.copy(alpha = 0.55f * intensidad), Color.Transparent),
            center = centro,
            radius = radio
        ),
        radius = radio,
        center = centro
    )
    // Líneas de luz: una ancha y suave, y otra fina y brillante encima
    listOf(-1f, 1f).forEach { lado ->
        val inicio = Offset(centro.x + lado * separacion, centro.y)
        val fin = Offset(centro.x + lado * (separacion + largoLinea), centro.y)
        drawLine(
            Brush.linearGradient(
                colors = listOf(LuzCalida.copy(alpha = 0.9f * intensidad), Color.Transparent),
                start = inicio,
                end = fin
            ),
            inicio, fin, strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round, alpha = 0.35f
        )
        drawLine(
            Brush.linearGradient(
                colors = listOf(LuzClara.copy(alpha = intensidad), Color.Transparent),
                start = inicio,
                end = fin
            ),
            inicio, fin, strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round
        )
    }
}

// Cortes del marco en la imagen original (en píxeles)
private data class CortesMarco(
    val izq: Int, val der: Int,              // esquinas izquierda y derecha (fijas)
    val centroIni: Int, val centroFin: Int,  // franja central fija (Pokébola, letrero...)
    val arr: Int, val aba: Int               // franjas superior e inferior (fijas)
)

// Dibuja el marco en 3x5 partes: esquinas y centro con tamaño fijo (escalado "k"),
// solo los tramos rectos se estiran. Así hojas, esquinas y adornos nunca se deforman.
private fun DrawScope.dibujarMarcoEstirable(imagen: ImageBitmap, c: CortesMarco, k: Float) {
    val anchoD = size.width
    val altoD = size.height

    // Columnas: [izq fija] [estira] [centro fijo] [estira] [der fija]
    val colOrigen = listOf(0, c.izq, c.centroIni, c.centroFin, imagen.width - c.der, imagen.width)
    val fijasDestino = (c.izq + (c.centroFin - c.centroIni) + c.der) * k
    val estiraH = ((anchoD - fijasDestino) / 2f).coerceAtLeast(0f)
    val colDestino = listOf(
        0f,
        c.izq * k,
        c.izq * k + estiraH,
        c.izq * k + estiraH + (c.centroFin - c.centroIni) * k,
        anchoD - c.der * k,
        anchoD
    )

    // Filas: [arriba fija] [estira] [abajo fija]
    val filaOrigen = listOf(0, c.arr, imagen.height - c.aba, imagen.height)
    val filaDestino = listOf(0f, c.arr * k, altoD - c.aba * k, altoD)

    for (f in 0 until 3) {
        for (col in 0 until 5) {
            val sx = colOrigen[col]
            val sy = filaOrigen[f]
            val sw = colOrigen[col + 1] - sx
            val sh = filaOrigen[f + 1] - sy
            val dx = colDestino[col]
            val dy = filaDestino[f]
            val dw = colDestino[col + 1] - dx
            val dh = filaDestino[f + 1] - dy
            if (sw <= 0 || sh <= 0 || dw <= 0f || dh <= 0f) continue
            drawImage(
                image = imagen,
                srcOffset = IntOffset(sx, sy),
                srcSize = IntSize(sw, sh),
                dstOffset = IntOffset(dx.roundToInt(), dy.roundToInt()),
                dstSize = IntSize(
                    (dx + dw).roundToInt() - dx.roundToInt(),
                    (dy + dh).roundToInt() - dy.roundToInt()
                ),
                filterQuality = FilterQuality.High
            )
        }
    }
}

// Panel con marco de madera (imagen) y fondo crema.
// - grosorMarco: tamaño del borde y sus adornos respecto a la imagen original.
// - Estirado inteligente: esquinas, hojas y adornos centrales (Pokébola) no se deforman.
// - El alto se ajusta al contenido, sin espacio de más.
// - El crema queda escondido debajo de la madera y el contenido dentro del borde interior.
// - El marco se dibuja ENCIMA del contenido: las hojas quedan por delante.
// - brilloInferior: luz cálida que late sobre el adorno del borde inferior (ej. la Pokébola).
@Composable
fun PanelMadera(
    @DrawableRes marco: Int,
    modifier: Modifier = Modifier,
    margenLateral: Dp = 10.dp,  // espacio a cada lado de la pantalla
    respiro: Dp = 8.dp,         // espacio entre la madera y el contenido
    extraArriba: Dp = 0.dp,     // espacio extra arriba (por ejemplo, para un letrero encima)
    brilloInferior: Boolean = false,
    grosorMarco: Float = 0.75f,
    contenido: @Composable () -> Unit
) {
    val context = LocalContext.current
    val medidas = remember(marco) { medirMarco(context, marco) }
    val imagen = ImageBitmap.imageResource(marco)

    // Cortes: al menos todo el borde de madera, o un mínimo para incluir hojas y adornos
    val cortes = remember(imagen, medidas) {
        val w = imagen.width
        val h = imagen.height
        val anchoCentro = (w * 0.26f).roundToInt()
        CortesMarco(
            izq = (max(medidas.interior.izq, 0.16f) * w).roundToInt(),
            der = (max(medidas.interior.der, 0.16f) * w).roundToInt(),
            centroIni = (w - anchoCentro) / 2,
            centroFin = (w + anchoCentro) / 2,
            arr = (max(medidas.interior.arr, 0.14f) * h).roundToInt(),
            aba = (max(medidas.interior.aba, 0.14f) * h).roundToInt()
        )
    }

    val transicion = rememberInfiniteTransition(label = "brilloPanel")
    val pulso by transicion.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulsoPanel"
    )

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = margenLateral),
        content = {
            // 0: fondo crema
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(CremaPanel)
            )
            // 1: contenido
            Box(
                modifier = Modifier.padding(
                    start = respiro,
                    end = respiro,
                    top = respiro + extraArriba,
                    bottom = respiro
                )
            ) {
                contenido()
            }
            // 2: marco estirado de forma inteligente (por delante del contenido)
            Canvas(modifier = Modifier) {
                val k = size.width / imagen.width * grosorMarco
                dibujarMarcoEstirable(imagen, cortes, k)
            }
            // 3: luz centrada sobre el adorno del borde inferior
            Canvas(modifier = Modifier) {
                if (brilloInferior) {
                    val k = size.width / imagen.width * grosorMarco
                    val ext = medidas.exterior
                    val int = medidas.interior
                    // El adorno ocupa desde el borde exterior hasta el interior: su centro está en medio
                    val grosor = (int.aba - ext.aba) * imagen.height * k
                    val distanciaAbajo = (ext.aba + int.aba) / 2f * imagen.height * k
                    val centro = Offset(size.width / 2f, size.height - distanciaAbajo)
                    dibujarLuzCalida(
                        centro = centro,
                        radio = grosor * 0.9f,
                        separacion = grosor * 0.7f,
                        largoLinea = size.width * 0.16f,
                        intensidad = pulso
                    )
                }
            }
        }
    ) { medibles, restricciones ->
        val ext = medidas.exterior
        val int = medidas.interior
        val ancho = restricciones.maxWidth
        // Escala de los bordes (esquinas y adornos) en pantalla
        val k = ancho / imagen.width.toFloat() * grosorMarco
        val w = imagen.width
        val h = imagen.height

        // Bordes interiores en píxeles de pantalla
        val izqInterior = int.izq * w * k
        val derInterior = int.der * w * k
        val arrInterior = int.arr * h * k
        val abaInterior = int.aba * h * k

        // Crema: a mitad de la madera
        val izqFondo = (ext.izq + int.izq) / 2f * w * k
        val derFondo = (ext.der + int.der) / 2f * w * k
        val arrFondo = (ext.arr + int.arr) / 2f * h * k
        val abaFondo = (ext.aba + int.aba) / 2f * h * k

        // El contenido se mide con el ancho del hueco interior
        val anchoContenido = (ancho - izqInterior - derInterior).roundToInt()
        val contenidoMedido = medibles[1].measure(
            Constraints(minWidth = anchoContenido, maxWidth = anchoContenido)
        )

        // Alto: bordes de madera + contenido, nunca menos que las dos franjas fijas
        val altoMinimo = ((cortes.arr + cortes.aba) * k).roundToInt()
        val alto = max(altoMinimo, (arrInterior + contenidoMedido.height + abaInterior).roundToInt())

        val fondoMedido = medibles[0].measure(
            Constraints.fixed(
                (ancho - izqFondo - derFondo).roundToInt(),
                (alto - arrFondo - abaFondo).roundToInt().coerceAtLeast(0)
            )
        )
        val marcoMedido = medibles[2].measure(Constraints.fixed(ancho, alto))
        val luzMedida = medibles[3].measure(Constraints.fixed(ancho, alto))

        // Orden de dibujo: crema, contenido, marco por delante y la luz encima de todo
        layout(ancho, alto) {
            fondoMedido.place(izqFondo.roundToInt(), arrFondo.roundToInt())
            contenidoMedido.place(izqInterior.roundToInt(), arrInterior.roundToInt())
            marcoMedido.place(0, 0)
            luzMedida.place(0, 0)
        }
    }
}

// Letrero recto de madera para los títulos, con un brillo amarillo suave detrás.
// destellos: líneas de luz cálidas sobre el borde superior (como la Pokébola del marco).
@Composable
fun LetreroSeccion(
    texto: String,
    modifier: Modifier = Modifier,
    alto: Dp = 56.dp,
    tamanoTexto: TextUnit = 20.sp,
    brillo: Boolean = true,
    destellos: Boolean = false,
    icono: (@Composable () -> Unit)? = null
) {
    val transicion = rememberInfiniteTransition(label = "brilloLetrero")
    val pulso by transicion.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "pulsoLetrero"
    )
    val pulsoLuz by transicion.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulsoLuzLetrero"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(alto)
            .drawBehind {
                if (brillo) {
                    // Resplandor suave, apenas más grande que el letrero
                    val ancho = size.width * 1.05f
                    val altoBrillo = size.height * 1.35f
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(AmarilloBrillo.copy(alpha = pulso), Color.Transparent),
                            center = center,
                            radius = ancho / 2f
                        ),
                        topLeft = Offset(center.x - ancho / 2f, center.y - altoBrillo / 2f),
                        size = Size(ancho, altoBrillo)
                    )
                }
            }
            .drawWithContent {
                drawContent()
                if (destellos) {
                    // Líneas de luz sobre el borde superior de la tabla
                    dibujarLuzCalida(
                        centro = Offset(size.width / 2f, size.height * 0.2f),
                        radio = size.height * 0.35f,
                        separacion = size.width * 0.18f,
                        largoLinea = size.width * 0.2f,
                        intensidad = pulsoLuz * 0.8f
                    )
                }
            }
    ) {
        // Tabla de madera (escalado de alta calidad)
        Image(
            painter = pintorNitido(R.drawable.tabla_seccion),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            if (icono != null) {
                icono()
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = texto,
                style = TextStyle(
                    brush = DegradadoLetrero,
                    fontFamily = FuenteMadera,
                    fontSize = tamanoTexto,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}