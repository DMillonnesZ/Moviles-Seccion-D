package com.tecsup.pokemon.ui.perfil

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.pokemon.domain.model.PerfilUsuario
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.ui.components.AparecerSuave
import com.tecsup.pokemon.ui.components.BotonVolver
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LetreroSeccion
import com.tecsup.pokemon.ui.components.LocalRellenoBarra
import com.tecsup.pokemon.ui.components.PestanaMetodo
import com.tecsup.pokemon.ui.components.Pokebola
import com.tecsup.pokemon.ui.components.desvanecerBordes
import com.tecsup.pokemon.util.ImagenPerfil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CremaPerfil = Color(0xFFFFF4DF)
private val DoradoClaro = Color(0xFFFFE7A0)
private val Dorado = Color(0xFFF5C451)
private val DoradoBorde = Color(0xFFD9A43A)
private val RojoPerfil = Color(0xFFE53935)
private val RojoClaroPerfil = Color(0xFFFF7A6E)
private val VerdePerfil = Color(0xFF35A82C)
private val VerdeClaroPerfil = Color(0xFF7BD857)
private val NaranjaPerfil = Color(0xFFF39C12)
private val AzulPerfil = Color(0xFF3D8FD6)
private val GrisPerfil = Color(0xFF7A6A5A)
private val FondoBarraPerfil = Color(0xFFECE4D8)

// Rango del entrenador según cuántos favoritos tiene
private data class Rango(val nombre: String, val desde: Int, val icono: ImageVector, val color: Color)

private val RANGOS = listOf(
    Rango("Novato", 0, Icons.Outlined.CatchingPokemon, Color(0xFF6DBF4B)),
    Rango("Entrenador", 5, Icons.Rounded.MilitaryTech, Color(0xFF3D8FD6)),
    Rango("Experto", 15, Icons.Rounded.WorkspacePremium, Color(0xFF9B59B6)),
    Rango("Maestro Pokémon", 30, Icons.Rounded.EmojiEvents, Color(0xFFE0A21B))
)

// Perfil del entrenador: foto, nombre, rango por favoritos y opciones de la cuenta.
// El título queda fijo; solo el contenido se desliza y se desvanece en los bordes.
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    esAdmin: Boolean,
    cantidadFavoritos: Int,
    onAdminClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val perfil by viewModel.perfil.collectAsStateWithLifecycle()
    val guardandoFoto by viewModel.guardandoFoto.collectAsStateWithLifecycle()
    val guardandoNombre by viewModel.guardandoNombre.collectAsStateWithLifecycle()
    val clave by viewModel.clave.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var verOpcionesFoto by remember { mutableStateOf(false) }
    var verNombre by remember { mutableStateOf(false) }
    var verClave by remember { mutableStateOf(false) }
    var verSalir by remember { mutableStateOf(false) }

    // Galería: selector de fotos de Android (no pide permisos)
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.cambiarFoto(context.contentResolver, uri)
    }

    // Cámara: guarda la foto en un archivo temporal y luego se procesa
    var uriCamara by rememberSaveable { mutableStateOf<Uri?>(null) }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        val uri = uriCamara
        if (tomada && uri != null) viewModel.cambiarFoto(context.contentResolver, uri)
    }

    // Avisos cortos después de guardar
    LaunchedEffect(mensaje) {
        val texto = mensaje ?: return@LaunchedEffect
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
        viewModel.mensajeMostrado()
    }

    // Al cambiar la contraseña se cierra la ventana
    LaunchedEffect(clave.cambiada) {
        if (clave.cambiada) {
            verClave = false
            viewModel.reiniciarClave()
        }
    }

    val esAdministrador = esAdmin || perfil?.rol == Rol.ADMIN

    FondoInicio {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // ----- Cabecera fija -----
            LetreroSeccion(
                texto = "Mi perfil",
                alto = 62.dp,
                tamanoTexto = 20.sp,
                icono = {
                    Icon(Icons.Rounded.Person, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(22.dp))
                },
                modifier = Modifier
                    .padding(top = 36.dp, bottom = 4.dp)
                    .fillMaxWidth(0.78f)
            )

            // ----- Contenido: termina encima de la barra y se desvanece en los bordes -----
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = LocalRellenoBarra.current)
                    .desvanecerBordes(arriba = 24.dp, abajo = 20.dp)
            ) {
                val datos = perfil
                if (datos == null) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(color = Dorado)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            // Espacio arriba y abajo para el desvanecido
                            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 24.dp)
                    ) {
                        AparecerSuave {
                            TarjetaEntrenador(
                                perfil = datos,
                                esAdmin = esAdministrador,
                                guardandoFoto = guardandoFoto,
                                onCambiarFoto = { verOpcionesFoto = true },
                                onEditarNombre = { verNombre = true }
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        AparecerSuave(retrasoMs = 120) {
                            TarjetaColeccion(cantidad = cantidadFavoritos)
                        }
                        Spacer(Modifier.height(20.dp))
                        AparecerSuave(retrasoMs = 240) {
                            TarjetaCuenta(
                                correo = datos.correo,
                                esAdmin = esAdministrador,
                                onCambiarClave = { verClave = true },
                                onAdminClick = onAdminClick,
                                onCerrarSesion = { verSalir = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // ----- Ventanas -----

    if (verOpcionesFoto) {
        DialogoFoto(
            tieneFoto = perfil?.foto != null,
            onGaleria = {
                verOpcionesFoto = false
                galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onCamara = {
                verOpcionesFoto = false
                runCatching {
                    val uri = crearUriCamara(context)
                    uriCamara = uri
                    camara.launch(uri)
                }.onFailure {
                    Toast.makeText(context, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
                }
            },
            onQuitar = {
                verOpcionesFoto = false
                viewModel.quitarFoto()
            },
            onCerrar = { verOpcionesFoto = false }
        )
    }

    if (verNombre) {
        DialogoNombre(
            actual = perfil?.nombre.orEmpty(),
            guardando = guardandoNombre,
            onGuardar = { nuevo -> viewModel.guardarNombre(nuevo) { verNombre = false } },
            onCerrar = { verNombre = false }
        )
    }

    if (verClave) {
        DialogoClave(
            estado = clave,
            onActualChange = viewModel::onActualChange,
            onNuevaChange = viewModel::onNuevaChange,
            onConfirmarChange = viewModel::onConfirmarChange,
            onGuardar = viewModel::cambiarClave,
            onCerrar = {
                verClave = false
                viewModel.reiniciarClave()
            }
        )
    }

    if (verSalir) {
        DialogoCerrarSesion(
            nombre = perfil?.nombre,
            onConfirmar = {
                verSalir = false
                onCerrarSesion()
            },
            onCancelar = { verSalir = false }
        )
    }
}

// ===================== Tarjetas =====================

// Tarjeta crema con borde dorado y pestaña arriba (mismo estilo que el resumen de Favoritos)
@Composable
private fun TarjetaPerfil(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(20.dp)
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth()
                .shadow(4.dp, forma)
                .clip(forma)
                .background(Brush.verticalGradient(listOf(Color.White, CremaPerfil)))
                .border(1.5.dp, Brush.verticalGradient(listOf(DoradoClaro, DoradoBorde)), forma)
                .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 16.dp),
            content = contenido
        )
        PestanaMetodo(
            texto = titulo,
            icono = icono,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

// Foto, nombre editable, rol y fecha de registro
@Composable
private fun TarjetaEntrenador(
    perfil: PerfilUsuario,
    esAdmin: Boolean,
    guardandoFoto: Boolean,
    onCambiarFoto: () -> Unit,
    onEditarNombre: () -> Unit
) {
    TarjetaPerfil(titulo = "Entrenador", icono = Icons.Rounded.Badge) {
        AvatarPerfil(foto = perfil.foto, guardando = guardandoFoto, onClick = onCambiarFoto)

        Spacer(Modifier.height(12.dp))

        // Nombre: se toca para editarlo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onEditarNombre)
                .padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Text(
                text = perfil.nombre,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Rounded.Edit, contentDescription = "Editar nombre", tint = DoradoBorde, modifier = Modifier.size(20.dp))
        }

        Spacer(Modifier.height(6.dp))
        ChipRol(esAdmin = esAdmin)

        perfil.creadoEn?.let { fecha ->
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = GrisPerfil, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Entrenador desde el ${formatearFecha(fecha)}",
                    color = GrisPerfil,
                    fontFamily = FuenteMadera,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// Foto redonda con aro dorado que gira y botón de cámara
@Composable
private fun AvatarPerfil(foto: String?, guardando: Boolean, onClick: () -> Unit) {
    val imagen = remember(foto) { ImagenPerfil.aImagen(foto) }
    val giro by rememberInfiniteTransition(label = "halo").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 8000, easing = LinearEasing)),
        label = "giro"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
        // Aro dorado
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = giro }
                .clip(CircleShape)
                .background(Brush.sweepGradient(listOf(Dorado, Color.White, DoradoBorde, DoradoClaro, Dorado)))
        )
        // Foto o Pokébola
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(126.dp)
                .clip(CircleShape)
                .background(CremaPerfil)
                .border(3.dp, Color.White, CircleShape)
                .clickable(onClick = onClick)
        ) {
            if (imagen != null) {
                Image(
                    bitmap = imagen,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Pokebola(apertura = 0f, brillo = 0f, modifier = Modifier.size(80.dp))
            }
            if (guardando) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
                }
            }
        }
        // Botón dorado de cámara (el mismo de la app)
        BotonVolver(
            onClick = onClick,
            icono = Icons.Rounded.PhotoCamera,
            descripcion = "Cambiar foto",
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

// Chip del rol: dorado para admin, crema para entrenador
@Composable
private fun ChipRol(esAdmin: Boolean) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(2.dp, forma)
            .clip(forma)
            .background(
                if (esAdmin) Brush.verticalGradient(listOf(DoradoClaro, Dorado))
                else Brush.verticalGradient(listOf(Color.White, CremaPerfil))
            )
            .border(1.dp, CafeMadera.copy(alpha = if (esAdmin) 0.6f else 0.3f), forma)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = if (esAdmin) Icons.Rounded.AdminPanelSettings else Icons.Outlined.CatchingPokemon,
            contentDescription = null,
            tint = if (esAdmin) CafeMadera else RojoPerfil,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = if (esAdmin) "Administrador" else "Entrenador Pokémon",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

// Favoritos + rango del entrenador con barra hacia el siguiente rango
@Composable
private fun TarjetaColeccion(cantidad: Int) {
    val actual = RANGOS.last { cantidad >= it.desde }
    val siguiente = RANGOS.firstOrNull { it.desde > cantidad }
    val objetivo = if (siguiente == null) 1f
    else (cantidad - actual.desde).toFloat() / (siguiente.desde - actual.desde)

    var listo by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { listo = true }
    val fraccion by animateFloatAsState(
        targetValue = if (listo) objetivo else 0f,
        animationSpec = tween(durationMillis = 900, delayMillis = 250),
        label = "rango"
    )

    TarjetaPerfil(titulo = "Mi colección", icono = Icons.Rounded.EmojiEvents) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            // Favoritos
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RojoPerfil, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$cantidad",
                        color = CafeMadera,
                        fontFamily = FuenteMadera,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                }
                Text(
                    text = if (cantidad == 1) "favorito" else "favoritos",
                    color = GrisPerfil,
                    fontFamily = FuenteMadera,
                    fontSize = 13.sp
                )
            }

            // Línea divisoria
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(56.dp)
                    .background(CafeMadera.copy(alpha = 0.15f))
            )

            // Rango
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(4.dp, CircleShape, ambientColor = actual.color, spotColor = actual.color)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(lerp(actual.color, Color.White, 0.45f), actual.color)))
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(actual.icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = actual.nombre,
                    color = lerp(actual.color, Color.Black, 0.3f),
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Barra hacia el siguiente rango
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(FondoBarraPerfil)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraccion)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(actual.color, lerp(actual.color, Color.White, 0.35f))))
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (siguiente == null) {
                "¡Llegaste al rango máximo!"
            } else {
                val faltan = siguiente.desde - cantidad
                "Te ${if (faltan == 1) "falta 1 favorito" else "faltan $faltan favoritos"} para ${siguiente.nombre}"
            },
            color = GrisPerfil,
            fontFamily = FuenteMadera,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Correo, cambiar contraseña, Panel Admin y cerrar sesión
@Composable
private fun TarjetaCuenta(
    correo: String,
    esAdmin: Boolean,
    onCambiarClave: () -> Unit,
    onAdminClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    TarjetaPerfil(titulo = "Cuenta", icono = Icons.Rounded.Settings) {
        FilaOpcion(icono = Icons.Rounded.Email, titulo = "Correo", subtitulo = correo, color = AzulPerfil)
        Divisor()
        FilaOpcion(
            icono = Icons.Rounded.LockReset,
            titulo = "Cambiar contraseña",
            color = DoradoBorde,
            onClick = onCambiarClave
        )
        if (esAdmin) {
            Divisor()
            FilaOpcion(
                icono = Icons.Rounded.AdminPanelSettings,
                titulo = "Panel Admin",
                subtitulo = "Gestionar la PokeDex",
                color = VerdePerfil,
                onClick = onAdminClick
            )
        }
        Divisor()
        FilaOpcion(
            icono = Icons.AutoMirrored.Rounded.Logout,
            titulo = "Cerrar sesión",
            color = RojoPerfil,
            peligro = true,
            onClick = onCerrarSesion
        )
    }
}

// Fila con ícono en círculo, título, subtítulo opcional y flecha si se puede tocar
@Composable
private fun FilaOpcion(
    icono: ImageVector,
    titulo: String,
    color: Color,
    subtitulo: String? = null,
    peligro: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 6.dp, vertical = 9.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                color = if (peligro) RojoPerfil else CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    color = GrisPerfil,
                    fontFamily = FuenteMadera,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (onClick != null) {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = CafeMadera.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun Divisor() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(1.dp)
            .background(CafeMadera.copy(alpha = 0.1f))
    )
}

// ===================== Ventanas =====================

// Ventana base: entra rebotando, ícono en círculo y título
@Composable
private fun VentanaPerfil(
    titulo: String,
    icono: ImageVector,
    onCerrar: () -> Unit,
    colorIcono: Color = DoradoBorde,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val escala = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
    val forma = RoundedCornerShape(24.dp)

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .shadow(10.dp, forma)
                .clip(forma)
                .background(Brush.verticalGradient(listOf(Color.White, CremaPerfil)))
                .border(2.dp, Brush.verticalGradient(listOf(DoradoClaro, DoradoBorde)), forma)
                .padding(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .shadow(4.dp, CircleShape, ambientColor = colorIcono, spotColor = colorIcono)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(lerp(colorIcono, Color.White, 0.45f), colorIcono)))
                    .border(3.dp, Color.White, CircleShape)
            ) {
                Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = titulo,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            contenido()
        }
    }
}

// Elegir foto: galería, cámara o quitarla
@Composable
private fun DialogoFoto(
    tieneFoto: Boolean,
    onGaleria: () -> Unit,
    onCamara: () -> Unit,
    onQuitar: () -> Unit,
    onCerrar: () -> Unit
) {
    VentanaPerfil(titulo = "Foto de perfil", icono = Icons.Rounded.PhotoCamera, onCerrar = onCerrar) {
        FilaOpcion(
            icono = Icons.Rounded.PhotoLibrary,
            titulo = "Elegir de la galería",
            color = AzulPerfil,
            onClick = onGaleria
        )
        Divisor()
        FilaOpcion(
            icono = Icons.Rounded.PhotoCamera,
            titulo = "Tomar una foto",
            color = VerdePerfil,
            onClick = onCamara
        )
        if (tieneFoto) {
            Divisor()
            FilaOpcion(
                icono = Icons.Rounded.Delete,
                titulo = "Quitar foto",
                color = RojoPerfil,
                peligro = true,
                onClick = onQuitar
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Cancelar",
            color = CafeMadera.copy(alpha = 0.8f),
            fontFamily = FuenteMadera,
            fontSize = 15.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onCerrar)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

// Editar el nombre de entrenador
@Composable
private fun DialogoNombre(
    actual: String,
    guardando: Boolean,
    onGuardar: (String) -> Unit,
    onCerrar: () -> Unit
) {
    var texto by remember { mutableStateOf(actual) }

    VentanaPerfil(titulo = "Tu nombre", icono = Icons.Rounded.Edit, onCerrar = onCerrar) {
        CampoPerfil(
            valor = texto,
            onCambio = { if (it.length <= LARGO_MAXIMO_NOMBRE) texto = it },
            etiqueta = "Nombre de entrenador",
            icono = Icons.Rounded.Person
        )
        Text(
            text = "${texto.length}/$LARGO_MAXIMO_NOMBRE",
            color = GrisPerfil,
            fontFamily = FuenteMadera,
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, end = 6.dp)
        )
        Spacer(Modifier.height(14.dp))
        BotonesDialogo(
            textoConfirmar = "Guardar",
            cargando = guardando,
            onConfirmar = { onGuardar(texto) },
            onCancelar = onCerrar
        )
    }
}

// Cambiar contraseña: actual, nueva (con medidor) y confirmación
@Composable
private fun DialogoClave(
    estado: ClaveUiState,
    onActualChange: (String) -> Unit,
    onNuevaChange: (String) -> Unit,
    onConfirmarChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCerrar: () -> Unit
) {
    VentanaPerfil(titulo = "Cambiar contraseña", icono = Icons.Rounded.LockReset, onCerrar = onCerrar) {
        CampoPerfil(
            valor = estado.actual,
            onCambio = onActualChange,
            etiqueta = "Contraseña actual",
            icono = Icons.Rounded.Lock,
            esClave = true
        )
        Spacer(Modifier.height(8.dp))
        CampoPerfil(
            valor = estado.nueva,
            onCambio = onNuevaChange,
            etiqueta = "Contraseña nueva",
            icono = Icons.Rounded.Key,
            esClave = true
        )
        if (estado.nueva.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            MedidorFuerza(fuerza = estado.fuerza)
        }
        Spacer(Modifier.height(8.dp))
        CampoPerfil(
            valor = estado.confirmar,
            onCambio = onConfirmarChange,
            etiqueta = "Confirmar contraseña",
            icono = Icons.Rounded.CheckCircle,
            esClave = true
        )
        estado.error?.let { error ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = error,
                color = RojoPerfil,
                fontFamily = FuenteMadera,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(14.dp))
        BotonesDialogo(
            textoConfirmar = "Guardar",
            cargando = estado.cargando,
            onConfirmar = onGuardar,
            onCancelar = onCerrar
        )
    }
}

// Confirmación para cerrar sesión
@Composable
private fun DialogoCerrarSesion(
    nombre: String?,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    VentanaPerfil(
        titulo = "¿Cerrar sesión?",
        icono = Icons.AutoMirrored.Rounded.Logout,
        colorIcono = RojoPerfil,
        onCerrar = onCancelar
    ) {
        Text(
            text = if (nombre != null) {
                "¿Seguro que quieres salir, $nombre? ¡Tus Pokémon te estarán esperando!"
            } else {
                "¿Seguro que quieres salir? ¡Tus Pokémon te estarán esperando!"
            },
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        BotonesDialogo(
            textoConfirmar = "Salir",
            fondoConfirmar = Brush.verticalGradient(listOf(RojoClaroPerfil, RojoPerfil)),
            cargando = false,
            onConfirmar = onConfirmar,
            onCancelar = onCancelar
        )
    }
}

// ===================== Piezas pequeñas =====================

// Campo de texto blanco con borde dorado; si es clave, con botón para ver/ocultar
@Composable
private fun CampoPerfil(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    esClave: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        singleLine = true,
        label = { Text(etiqueta, fontFamily = FuenteMadera) },
        leadingIcon = { Icon(icono, contentDescription = null, tint = CafeMadera.copy(alpha = 0.8f)) },
        trailingIcon = if (esClave) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (visible) "Ocultar" else "Mostrar",
                        tint = CafeMadera.copy(alpha = 0.7f)
                    )
                }
            }
        } else null,
        visualTransformation = if (esClave && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else KeyboardType.Text),
        textStyle = TextStyle(fontFamily = FuenteMadera, color = CafeMadera, fontSize = 16.sp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DoradoBorde,
            unfocusedBorderColor = CafeMadera.copy(alpha = 0.3f),
            focusedLabelColor = CafeMadera,
            unfocusedLabelColor = CafeMadera.copy(alpha = 0.6f),
            cursorColor = CafeMadera,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// Tres barritas: débil (rojo), media (naranja), fuerte (verde)
@Composable
private fun MedidorFuerza(fuerza: Int) {
    val (color, texto) = when (fuerza) {
        1 -> RojoPerfil to "Débil"
        2 -> NaranjaPerfil to "Media"
        else -> VerdePerfil to "Fuerte"
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        repeat(3) { indice ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (indice < fuerza) color else FondoBarraPerfil)
            )
            if (indice < 2) Spacer(Modifier.width(4.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = texto,
            color = color,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

// Botones Cancelar (dorado) y confirmar (verde por defecto)
@Composable
private fun BotonesDialogo(
    textoConfirmar: String,
    cargando: Boolean,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    fondoConfirmar: Brush = Brush.verticalGradient(listOf(VerdeClaroPerfil, VerdePerfil))
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        BotonPildora(
            texto = "Cancelar",
            fondo = Brush.verticalGradient(listOf(DoradoClaro, Dorado)),
            colorTexto = CafeMadera,
            habilitado = !cargando,
            cargando = false,
            onClick = onCancelar,
            modifier = Modifier.weight(1f)
        )
        BotonPildora(
            texto = textoConfirmar,
            fondo = fondoConfirmar,
            colorTexto = Color.White,
            habilitado = !cargando,
            cargando = cargando,
            onClick = onConfirmar,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BotonPildora(
    texto: String,
    fondo: Brush,
    colorTexto: Color,
    habilitado: Boolean,
    cargando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(50)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(46.dp)
            .shadow(3.dp, forma)
            .clip(forma)
            .background(fondo)
            .border(1.5.dp, Color.White.copy(alpha = 0.6f), forma)
            .clickable(enabled = habilitado, onClick = onClick)
    ) {
        if (cargando) {
            CircularProgressIndicator(color = colorTexto, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Text(
                text = texto,
                color = colorTexto,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

// Archivo temporal donde la cámara guarda la foto
private fun crearUriCamara(context: Context): Uri {
    val carpeta = File(context.cacheDir, "fotos").apply { mkdirs() }
    val archivo = File(carpeta, "perfil_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.archivos", archivo)
}

// "12 de noviembre de 2026"
private fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-PE")).format(Date(milisegundos))