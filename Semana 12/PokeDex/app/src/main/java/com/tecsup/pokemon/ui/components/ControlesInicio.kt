package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R

private val CremaTexto = Color(0xFFFFF3DC)

private val DegradadoTituloInicio = Brush.verticalGradient(
    listOf(Color(0xFFAD671C), Color(0xFF693106), Color(0xFF692B04))
)

// Fondo de las pantallas con barra de navegación
@Composable
fun FondoInicio(contenido: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo_inicio),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        contenido()
    }
}

// Tabla de título con la Pokébola a la izquierda
@Composable
fun TituloTabla(texto: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(300.dp)
            .height(84.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_titulo_inicio),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Pokebola(apertura = 0f, brillo = 0f, modifier = Modifier.size(36.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = texto,
                style = TextStyle(
                    brush = DegradadoTituloInicio,
                    fontFamily = FuenteMadera,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// Buscador sobre tabla de madera, con lupa y botón para borrar
@Composable
fun BuscadorMadera(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.height(52.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_buscador),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = CafeMadera,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                singleLine = true,
                textStyle = TextStyle(color = CafeMadera, fontFamily = FuenteMadera, fontSize = 16.sp),
                cursorBrush = SolidColor(CafeMadera),
                modifier = Modifier.weight(1f),
                decorationBox = { campo ->
                    Box {
                        if (valor.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = CafeMadera.copy(alpha = 0.6f),
                                fontFamily = FuenteMadera,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        campo()
                    }
                }
            )
            if (valor.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Borrar búsqueda",
                    tint = CafeMadera,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onValorChange("") }
                )
            }
        }
    }
}

// Botón "Filtrar" de madera oscura
@Composable
fun BotonFiltroMadera(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_filtro),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.FilterList, contentDescription = null, tint = CremaTexto, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(4.dp))
            Text(texto, color = CremaTexto, fontFamily = FuenteMadera, fontSize = 16.sp)
            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = CremaTexto, modifier = Modifier.size(20.dp))
        }
    }
}