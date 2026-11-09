package com.tecsup.pokemon.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R
import kotlinx.coroutines.delay

// Colores del estilo madera
val CafeMadera = Color(0xFF5A3A1E)
val AzulEnlace = Color(0xFF2F62B5)
val RojoError = Color(0xFFB3261E)

// Letra redondeada tipo manuscrita (res/font/itim_regular.ttf)
val FuenteMadera = FontFamily(Font(R.font.itim_regular))

// Degradado del título "PokeDex": de café claro arriba a café oscuro abajo
private val DegradadoTitulo = Brush.verticalGradient(
    listOf(
        Color(0xFFAD671C),
        Color(0xFF693106),
        Color(0xFF692B04)
    )
)

// Fondo de bosque acuarela que ocupa toda la pantalla
@Composable
fun FondoPokedex(contenido: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo_pokedex),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        contenido()
    }
}

// Cartel grande con la Pokébola y el título sobre la tabla
@Composable
fun CartelTitulo(texto: String = "PokeDex") {
    // El Box toma el tamaño exacto de la imagen, así el texto se ubica sobre la tabla
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.width(380.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_titulo),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = texto,
            style = TextStyle(
                brush = DegradadoTitulo,
                fontFamily = FuenteMadera,
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.offset(y = (-20).dp)   // sube el texto al centro de la tabla
        )
    }
}

// Tablita del subtítulo con rayitas a los lados
@Composable
fun TablaSubtitulo(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Rayitas(izquierda = true)
        Spacer(Modifier.width(4.dp))
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.tabla_subtitulo),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
            Text(
                text = texto,
                fontFamily = FuenteMadera,
                fontSize = 19.sp,
                color = CafeMadera,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        Spacer(Modifier.width(4.dp))
        Rayitas(izquierda = false)
    }
}

// Dos trazos cortos a cada lado del subtítulo
@Composable
private fun Rayitas(izquierda: Boolean) {
    Canvas(modifier = Modifier.size(width = 16.dp, height = 18.dp)) {
        val grosor = 2.5.dp.toPx()
        val w = size.width
        val h = size.height
        if (izquierda) {
            drawLine(CafeMadera, Offset(0f, h * 0.2f), Offset(w, h * 0.4f), grosor, StrokeCap.Round)
            drawLine(CafeMadera, Offset(0f, h * 0.8f), Offset(w, h * 0.6f), grosor, StrokeCap.Round)
        } else {
            drawLine(CafeMadera, Offset(w, h * 0.2f), Offset(0f, h * 0.4f), grosor, StrokeCap.Round)
            drawLine(CafeMadera, Offset(w, h * 0.8f), Offset(0f, h * 0.6f), grosor, StrokeCap.Round)
        }
    }
}

// Campo de texto sobre una tabla de madera
@Composable
fun CampoMadera(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    icono: ImageVector,
    esClave: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    habilitado: Boolean = true
) {
    var verClave by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_campo),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = CafeMadera,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                enabled = habilitado,
                singleLine = true,
                textStyle = TextStyle(
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontSize = 17.sp
                ),
                cursorBrush = SolidColor(CafeMadera),
                visualTransformation = if (esClave && !verClave) PasswordVisualTransformation()
                else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (esClave) KeyboardType.Password else tipoTeclado
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { campo ->
                    Box {
                        if (valor.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = CafeMadera.copy(alpha = 0.6f),
                                fontFamily = FuenteMadera,
                                fontSize = 17.sp
                            )
                        }
                        campo()
                    }
                }
            )
            if (esClave) {
                Icon(
                    imageVector = if (verClave) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = "Ver contraseña",
                    tint = CafeMadera,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { verClave = !verClave }
                )
            }
        }
    }
}

// Botón azul de madera
@Composable
fun BotonMadera(
    texto: String,
    onClick: () -> Unit,
    cargando: Boolean = false
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = !cargando, onClick = onClick)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_boton),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        if (cargando) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = texto,
                color = Color.White,
                fontFamily = FuenteMadera,
                fontSize = 24.sp
            )
        }
    }
}

// Tabla delgada con pregunta + enlace
@Composable
fun EnlaceMadera(
    pregunta: String,
    accion: String,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_enlace),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$pregunta ",
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 16.sp
            )
            Text(
                text = accion,
                color = AzulEnlace,
                fontFamily = FuenteMadera,
                fontSize = 16.sp,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
    }
}

// Mensaje de error en tabla de madera con ícono de alerta
@Composable
fun MensajeError(texto: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_enlace),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = RojoError,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = texto,
                color = RojoError,
                fontFamily = FuenteMadera,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Error que aparece con fundido, dura unos segundos y se oculta solo
@Composable
fun ErrorTemporal(
    mensaje: String?,
    onOcultar: () -> Unit,
    duracionMs: Long = 2000
) {
    // Guarda el último texto para que se vea mientras se desvanece
    var ultimoMensaje by remember { mutableStateOf("") }

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            ultimoMensaje = mensaje
            delay(duracionMs)
            onOcultar()
        }
    }

    AnimatedVisibility(
        visible = mensaje != null,
        enter = fadeIn(tween(300)) + expandVertically(tween(300)),
        exit = fadeOut(tween(600)) + shrinkVertically(tween(600))
    ) {
        Column {
            Spacer(Modifier.height(12.dp))
            MensajeError(ultimoMensaje)
        }
    }
}