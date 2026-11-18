package com.millones.campusgps.ui.componentes

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Acción para abrir el menú lateral desde cualquier pantalla (el botón ☰ del mapa)
val LocalAbrirMenu = staticCompositionLocalOf<() -> Unit> { {} }

private val RojoSuave = Color(0xFFFF8A8A)

// Menú lateral desplegable de vidrio oscuro: logo, saludo, secciones con descripción,
// consejo de uso y cerrar sesión
@Composable
fun MenuLateral(
    nombre: String?,
    correo: String?,
    items: List<ItemBarra>,
    seleccionada: Int,
    onSeleccionar: (Int) -> Unit,
    onCerrarSesion: () -> Unit,
    // Foto de perfil en Base64 (null = inicial)
    fotoBase64: String? = null
) {
    ModalDrawerSheet(
        drawerContainerColor = VidrioMapa.FondoSolido,
        drawerContentColor = Color.White,
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        modifier = Modifier.width(310.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF15306A), Color(0xFF0D1F45), VidrioMapa.FondoSolido)
                    )
                )
                .windowInsetsPadding(InsetsCampus)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            // ----- Logo y frase -----
            Row(verticalAlignment = Alignment.CenterVertically) {
                LogoGps(tamano = 46.dp)
                Spacer(Modifier.width(4.dp))
                Column {
                    NombreCampus(fontSize = 26.sp)
                    Text(
                        text = "Encuentra tu camino en el campus",
                        color = Color.White.copy(alpha = 0.6f),
                        fontFamily = FuenteCampus,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // ----- Saludo y usuario -----
            TarjetaUsuario(nombre = nombre, correo = correo, fotoBase64 = fotoBase64)

            Spacer(Modifier.height(24.dp))

            TituloSeccion("Navegación")

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEachIndexed { indice, item ->
                    OpcionMenu(
                        item = item,
                        activa = indice == seleccionada,
                        onClick = { onSeleccionar(indice) }
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            TituloSeccion("Consejo")
            Consejo()

            Spacer(Modifier.height(22.dp))

            // ----- Cerrar sesión -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(RojoSuave.copy(alpha = 0.10f))
                    .border(1.dp, RojoSuave.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .clickable(onClick = onCerrarSesion)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                IconoCuadrado(icono = Icons.AutoMirrored.Rounded.Logout, color = RojoSuave, activo = true)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cerrar sesión",
                        color = RojoSuave,
                        fontFamily = FuenteCampus,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Salir de tu cuenta en este celular",
                        color = Color.White.copy(alpha = 0.55f),
                        fontFamily = FuenteCampus,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                text = "CampusGPS · versión 1.0",
                color = Color.White.copy(alpha = 0.35f),
                fontFamily = FuenteCampus,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun TituloSeccion(texto: String) {
    Text(
        text = texto.uppercase(),
        color = Color.White.copy(alpha = 0.45f),
        fontFamily = FuenteCampus,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.4.sp,
        modifier = Modifier.padding(start = 6.dp, bottom = 10.dp)
    )
}

// Saludo con el primer nombre, la inicial en círculo y el correo
@Composable
private fun TarjetaUsuario(nombre: String?, correo: String?, fotoBase64: String?) {
    val nombreVisible = nombre?.takeIf { it.isNotBlank() }
    val primerNombre = nombreVisible?.substringBefore(" ") ?: "estudiante"
    val inicial = (nombreVisible ?: correo ?: "?").first().uppercaseChar()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, VidrioMapa.Borde, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        AvatarUsuario(
            nombre = "$inicial",
            fotoBase64 = fotoBase64,
            tamano = 48.dp
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    append("Hola, ")
                    withStyle(SpanStyle(color = CampusColores.VerdeMenta)) { append(primerNombre) }
                },
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (correo != null) {
                Text(
                    text = correo,
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = FuenteCampus,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Opción con ícono en cuadro de color, título y descripción; la activa se resalta
@Composable
private fun OpcionMenu(item: ItemBarra, activa: Boolean, onClick: () -> Unit) {
    val colorTitulo by animateColorAsState(
        targetValue = if (activa) Color.White else Color.White.copy(alpha = 0.85f),
        label = "colorOpcion"
    )
    val forma = RoundedCornerShape(18.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(if (activa) item.color.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.03f))
            .border(1.dp, if (activa) item.color.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.06f), forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        IconoCuadrado(icono = item.icono, color = item.color, activo = activa)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.etiqueta,
                color = colorTitulo,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            if (item.descripcion.isNotBlank()) {
                Text(
                    text = item.descripcion,
                    color = Color.White.copy(alpha = 0.55f),
                    fontFamily = FuenteCampus,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (activa) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(item.color)
            )
        } else {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Ícono dentro de un cuadro redondeado de su color
@Composable
private fun IconoCuadrado(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    activo: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = if (activo) 0.28f else 0.14f))
            .border(1.dp, color.copy(alpha = if (activo) 0.6f else 0.25f), RoundedCornerShape(12.dp))
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
    }
}

// Tarjeta con un consejo de uso del mapa
@Composable
private fun Consejo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(VidrioMapa.Cian.copy(alpha = 0.08f))
            .border(1.dp, VidrioMapa.Cian.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        IconoCuadrado(icono = Icons.Rounded.TouchApp, color = VidrioMapa.Cian, activo = false)
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Mantén presionado un punto del mapa para guardar un lugar con su título, descripción y foto.",
            color = Color.White.copy(alpha = 0.8f),
            fontFamily = FuenteCampus,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
