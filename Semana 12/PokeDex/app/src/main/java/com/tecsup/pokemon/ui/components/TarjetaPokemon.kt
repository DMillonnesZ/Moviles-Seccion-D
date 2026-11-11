package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item

private val CremaTarjeta = Color(0xFFFFF4DF)
private val CafeTexto = Color(0xFF3E2410)
private val GrisNumero = Color(0xFF8A7A6A)
private val RojoCorazon = Color(0xFFE53935)

// Margen entre el borde de la imagen y la madera del marco (las hojas sobresalen)
private val MARGEN_LADOS = 22.dp
private val MARGEN_ARRIBA = 10.dp
private val MARGEN_ABAJO = 12.dp

// Reparto del interior: el recuadro de color ocupa la parte de arriba
// y la zona crema sube un poco con un lomo curvo donde va el número
private const val FRACCION_COLOR = 0.60f
private const val FRACCION_LOMO = 0.09f

// Datos de un chip de tipo: nombre en inglés (para el color), en español e ícono
data class TipoChip(
    val nombre: String,
    val nombreEs: String,
    val iconoUrl: String
)

// Zona crema con un lomo curvo arriba al centro (ahí va el número)
private fun formaConLomo(fraccionLomo: Float): Shape = GenericShape { tamano, _ ->
    val ancho = tamano.width
    val alto = tamano.height
    val altoLomo = alto * fraccionLomo
    val anchoLomo = ancho * 0.46f
    val izquierda = (ancho - anchoLomo) / 2f
    val derecha = izquierda + anchoLomo
    val curva = anchoLomo * 0.22f

    moveTo(0f, altoLomo)
    lineTo(izquierda - curva, altoLomo)
    // Sube suave por la izquierda
    cubicTo(izquierda, altoLomo, izquierda, 0f, izquierda + curva, 0f)
    lineTo(derecha - curva, 0f)
    // Baja suave por la derecha
    cubicTo(derecha, 0f, derecha, altoLomo, derecha + curva, altoLomo)
    lineTo(ancho, altoLomo)
    lineTo(ancho, alto)
    lineTo(0f, alto)
    close()
}

// Tarjeta moderna de Pokémon: marco de madera, recuadro con el color del tipo,
// Pokébola blanca de fondo, Pokémon que sobresale, íconos de tipos y corazón que late
@Composable
fun TarjetaPokemon(
    item: Item,
    colorPrincipal: Color,
    chips: List<TipoChip>,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionada by interaccion.collectIsPressedAsState()
    val levantar by animateFloatAsState(
        targetValue = if (presionada) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "levantarPokemon"
    )

    // La zona crema mide su parte + el lomo que sube sobre el recuadro de color
    val fraccionCrema = 1f - FRACCION_COLOR + FRACCION_LOMO
    val forma = remember { formaConLomo(FRACCION_LOMO / fraccionCrema) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.78f)
            // Brillo del color del tipo al presionar
            .drawBehind {
                if (levantar > 0f) {
                    val radio = size.maxDimension * 0.7f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colorPrincipal.copy(alpha = 0.55f * levantar), Color.Transparent),
                            center = center,
                            radius = radio
                        ),
                        radius = radio,
                        center = center
                    )
                }
            }
            // Se levanta: crece un poco y sube
            .graphicsLayer {
                val escala = 1f + 0.05f * levantar
                scaleX = escala
                scaleY = escala
                translationY = -8.dp.toPx() * levantar
            }
            .clickable(interactionSource = interaccion, indication = null, onClick = onClick)
    ) {
        val altoInterior = maxHeight - MARGEN_ARRIBA - MARGEN_ABAJO
        val altoLomo = altoInterior * FRACCION_LOMO
        val tamanoPokemon = maxWidth * 0.66f

        // ----- Interior dentro de la madera -----
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = MARGEN_LADOS, end = MARGEN_LADOS, top = MARGEN_ARRIBA, bottom = MARGEN_ABAJO)
                .clip(RoundedCornerShape(10.dp))
        ) {
            // Recuadro de color con la Pokébola blanca de fondo y la sombra
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(FRACCION_COLOR)
                    .background(
                        Brush.verticalGradient(
                            listOf(lerp(colorPrincipal, Color.White, 0.35f), colorPrincipal)
                        )
                    )
            ) {
                // Pokébola blanca, grande y semitransparente
                Icon(
                    imageVector = Icons.Outlined.CatchingPokemon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .aspectRatio(1f)
                )
                // Sombra a los pies del Pokémon, justo encima del lomo
                Canvas(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = altoLomo + 2.dp)
                        .size(width = 60.dp, height = 10.dp)
                ) {
                    drawOval(Color.Black.copy(alpha = 0.25f))
                }
            }

            // Zona crema con lomo: número arriba en la curva, nombre e íconos de tipo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(fraccionCrema)
                    .background(CremaTarjeta, forma)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(altoLomo)
                ) {
                    Text(
                        text = "#%03d".format(item.id),
                        color = GrisNumero,
                        fontFamily = FuenteMadera,
                        fontSize = 12.sp
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = item.nombre.replaceFirstChar { it.uppercase() },
                        color = CafeTexto,
                        fontFamily = FuenteMadera,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        chips.forEach { IconoTipo(it) }
                    }
                }
            }
        }

        // ----- Marco de madera -----
        Image(
            painter = painterResource(R.drawable.tarjeta_pokemon_marco),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // ----- Pokémon que sobresale por arriba del recuadro -----
        // Sin internet no se muestra nada: solo queda la Pokébola de fondo
        SubcomposeAsyncImage(
            model = item.imagenUrl,
            contentDescription = item.nombre,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-8).dp)
                .size(tamanoPokemon),
            loading = {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            error = { }
        )

        // ----- Corazón que late -----
        BotonCorazon(
            esFavorito = item.esFavorito,
            onClick = onFavoritoClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = MARGEN_LADOS + 4.dp, top = MARGEN_ARRIBA + 6.dp)
        )
    }
}

// Ícono de tipo redondo con borde blanco (sin texto, para que quepan dos)
@Composable
private fun IconoTipo(chip: TipoChip) {
    AsyncImage(
        model = chip.iconoUrl,
        contentDescription = chip.nombreEs,
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .border(1.5.dp, Color.White, CircleShape)
    )
}

// Corazón: al cambiar de estado late (crece y rebota)
@Composable
private fun BotonCorazon(
    esFavorito: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val escala = remember { Animatable(1f) }
    var primeraVez by remember { mutableStateOf(true) }

    LaunchedEffect(esFavorito) {
        if (primeraVez) {
            primeraVez = false
        } else {
            escala.animateTo(1.35f, tween(110))
            escala.animateTo(1f, spring(dampingRatio = 0.35f))
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(28.dp)
            .graphicsLayer {
                scaleX = escala.value
                scaleY = escala.value
            }
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.85f))
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = if (esFavorito) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = "Favorito",
            tint = RojoCorazon,
            modifier = Modifier.size(17.dp)
        )
    }
}