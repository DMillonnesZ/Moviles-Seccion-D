package com.tecsup.pokemon.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material.icons.rounded.AddModerator
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.RemoveModerator
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.pokemon.domain.model.AccionRegistro
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.TipoAccion
import com.tecsup.pokemon.domain.model.UsuarioAdmin
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.AparecerSuave
import com.tecsup.pokemon.ui.components.BotonPildora
import com.tecsup.pokemon.ui.components.BotonVolver
import com.tecsup.pokemon.ui.components.BotonesVentana
import com.tecsup.pokemon.ui.components.BuscadorMadera
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.CampoCrema
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.DivisorSuave
import com.tecsup.pokemon.ui.components.ErrorView
import com.tecsup.pokemon.ui.components.FilaOpcion
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FondoPildora
import com.tecsup.pokemon.ui.components.FotoUsuario
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LetreroSeccion
import com.tecsup.pokemon.ui.components.TarjetaPestana
import com.tecsup.pokemon.ui.components.VacioView
import com.tecsup.pokemon.ui.components.VentanaMadera
import com.tecsup.pokemon.ui.components.desvanecerBordes
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CremaAdmin = Color(0xFFFFF4DF)
private val RosaSuave = Color(0xFFFFEBEE)
private val DoradoClaro = Color(0xFFFFE7A0)
private val Dorado = Color(0xFFF5C451)
private val DoradoBorde = Color(0xFFD9A43A)
private val RojoAdmin = Color(0xFFE53935)
private val RosaAdmin = Color(0xFFE91E63)
private val VerdeAdmin = Color(0xFF35A82C)
private val AzulAdmin = Color(0xFF3D8FD6)
private val NaranjaAdmin = Color(0xFFF57C00)
private val GrisAdmin = Color(0xFF7A6A5A)

// Largo máximo del motivo de suspensión
private const val LARGO_MOTIVO = 120

// Pestañas del panel (Catálogo se agrega en la Fase B)
private enum class PestanaAdmin(val etiqueta: String, val icono: ImageVector) {
    USUARIOS("Usuarios", Icons.Rounded.Group),
    REGISTRO("Registro", Icons.Rounded.History),
    SISTEMA("Sistema", Icons.Rounded.Settings)
}

// Ventana abierta en el panel
private sealed interface VentanaAdmin {
    data class Usuario(val usuario: UsuarioAdmin) : VentanaAdmin
    data class Suspender(val usuario: UsuarioAdmin) : VentanaAdmin
    data class CambiarRol(val usuario: UsuarioAdmin, val haciaAdmin: Boolean) : VentanaAdmin
    data class Restablecer(val usuario: UsuarioAdmin) : VentanaAdmin
    data object LimpiarCache : VentanaAdmin
}

// Panel Admin: cuentas (suspender, activar, roles, restablecer contraseña),
// registro de acciones y sistema (base local y mantenimiento).
// Título y pestañas fijos; el contenido se desliza con desvanecido.
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onVolver: () -> Unit
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val conteo by viewModel.conteo.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()
    val procesando by viewModel.procesando.collectAsStateWithLifecycle()
    val registro by viewModel.registro.collectAsStateWithLifecycle()
    val resumen by viewModel.resumen.collectAsStateWithLifecycle()
    val ocupado by viewModel.ocupado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()

    var pestana by rememberSaveable { mutableStateOf(PestanaAdmin.USUARIOS) }
    var ventana by remember { mutableStateOf<VentanaAdmin?>(null) }

    // El aviso se oculta solo
    LaunchedEffect(aviso) {
        if (aviso != null) {
            delay(4500)
            viewModel.ocultarAviso()
        }
    }

    FondoInicio {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // ----- Cabecera fija: volver + letrero -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 36.dp, bottom = 4.dp)
            ) {
                BotonVolver(onClick = onVolver)
                Spacer(Modifier.width(8.dp))
                LetreroSeccion(
                    texto = "Panel Admin",
                    alto = 62.dp,
                    tamanoTexto = 18.sp,
                    icono = {
                        Icon(Icons.Rounded.AdminPanelSettings, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(22.dp))
                    },
                    modifier = Modifier.weight(1f)
                )
                // Mismo ancho que el botón, para que el letrero quede centrado
                Spacer(Modifier.width(48.dp))
            }

            // ----- Pestañas -----
            SelectorPestanas(
                seleccionada = pestana,
                suspendidos = conteo.suspendidos,
                onSeleccionar = { pestana = it }
            )

            // ----- Aviso del resultado -----
            AvisoResultado(
                aviso = aviso,
                onCerrar = viewModel::ocultarAviso,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // ----- Contenido de la pestaña -----
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Crossfade(targetState = pestana, label = "pestanaAdmin") { actual ->
                    when (actual) {
                        PestanaAdmin.USUARIOS -> PestanaUsuarios(
                            estado = usuarios,
                            conteo = conteo,
                            busqueda = busqueda,
                            filtro = filtro,
                            uidActual = viewModel.uidActual,
                            procesando = procesando,
                            onBusqueda = viewModel::onBusquedaChange,
                            onFiltro = viewModel::cambiarFiltro,
                            onUsuarioClick = { ventana = VentanaAdmin.Usuario(it) }
                        )

                        PestanaAdmin.REGISTRO -> PestanaRegistro(estado = registro)

                        PestanaAdmin.SISTEMA -> PestanaSistema(
                            estado = resumen,
                            ocupado = ocupado,
                            onReintentar = viewModel::cargarTotales,
                            onSincronizar = viewModel::sincronizar,
                            onLimpiar = { ventana = VentanaAdmin.LimpiarCache }
                        )
                    }
                }
            }
        }
    }

    // ----- Ventanas -----
    when (val v = ventana) {
        is VentanaAdmin.Usuario -> DialogoUsuario(
            usuario = v.usuario,
            esYo = v.usuario.uid == viewModel.uidActual,
            onSuspender = { ventana = VentanaAdmin.Suspender(v.usuario) },
            onActivar = {
                ventana = null
                viewModel.activar(v.usuario)
            },
            onCambiarRol = { ventana = VentanaAdmin.CambiarRol(v.usuario, haciaAdmin = v.usuario.rol != Rol.ADMIN) },
            onRestablecer = { ventana = VentanaAdmin.Restablecer(v.usuario) },
            onCerrar = { ventana = null }
        )

        is VentanaAdmin.Suspender -> DialogoSuspender(
            usuario = v.usuario,
            onConfirmar = { motivo ->
                ventana = null
                viewModel.suspender(v.usuario, motivo)
            },
            onCancelar = { ventana = null }
        )

        is VentanaAdmin.CambiarRol -> DialogoConfirmar(
            titulo = if (v.haciaAdmin) "¿Hacer admin a ${v.usuario.nombre}?" else "¿Quitar admin a ${v.usuario.nombre}?",
            texto = if (v.haciaAdmin) {
                "Podrá entrar al Panel Admin, suspender cuentas y cambiar roles."
            } else {
                "Dejará de ver el Panel Admin y volverá a ser entrenador."
            },
            icono = if (v.haciaAdmin) Icons.Rounded.AddModerator else Icons.Rounded.RemoveModerator,
            color = if (v.haciaAdmin) DoradoBorde else NaranjaAdmin,
            textoConfirmar = if (v.haciaAdmin) "Hacer admin" else "Quitar admin",
            fondoConfirmar = FondoPildora.Verde,
            onConfirmar = {
                ventana = null
                viewModel.cambiarRol(v.usuario, if (v.haciaAdmin) Rol.ADMIN else Rol.USUARIO)
            },
            onCancelar = { ventana = null }
        )

        is VentanaAdmin.Restablecer -> DialogoConfirmar(
            titulo = "¿Restablecer contraseña?",
            texto = "Se enviará un correo a ${v.usuario.correo} con un enlace para crear una nueva contraseña.",
            icono = Icons.Rounded.LockReset,
            color = AzulAdmin,
            textoConfirmar = "Enviar",
            fondoConfirmar = FondoPildora.Verde,
            onConfirmar = {
                ventana = null
                viewModel.restablecerClave(v.usuario)
            },
            onCancelar = { ventana = null }
        )

        VentanaAdmin.LimpiarCache -> {
            val datos = (resumen as? UiState.Exito)?.datos
            DialogoConfirmar(
                titulo = "¿Limpiar caché?",
                texto = if (datos != null) {
                    "Se borrarán ${datos.totalGuardado} Pokémon y ${datos.totalFavoritos} favoritos de este teléfono. " +
                            "Podrás volver a descargarlos con Sincronizar."
                } else {
                    "Se borrarán los Pokémon y favoritos de este teléfono. Podrás volver a descargarlos con Sincronizar."
                },
                icono = Icons.Rounded.Warning,
                color = RojoAdmin,
                textoConfirmar = "Limpiar",
                fondoConfirmar = FondoPildora.Rojo,
                onConfirmar = {
                    ventana = null
                    viewModel.limpiarCache()
                },
                onCancelar = { ventana = null }
            )
        }

        null -> Unit
    }
}

// ===================== Pestañas =====================

// Tres píldoras: Usuarios (con burbuja de suspendidos), Registro y Sistema
@Composable
private fun SelectorPestanas(
    seleccionada: PestanaAdmin,
    suspendidos: Int,
    onSeleccionar: (PestanaAdmin) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        PestanaAdmin.entries.forEach { opcion ->
            val activa = opcion == seleccionada
            val forma = RoundedCornerShape(50)
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .shadow(if (activa) 3.dp else 1.dp, forma)
                        .clip(forma)
                        .background(
                            if (activa) Brush.verticalGradient(listOf(DoradoClaro, Dorado))
                            else Brush.verticalGradient(listOf(Color.White, CremaAdmin))
                        )
                        .border(
                            if (activa) 1.5.dp else 1.dp,
                            CafeMadera.copy(alpha = if (activa) 0.7f else 0.3f),
                            forma
                        )
                        .clickable { onSeleccionar(opcion) }
                        .padding(horizontal = 6.dp)
                ) {
                    Icon(opcion.icono, contentDescription = null, tint = CafeMadera, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = opcion.etiqueta,
                        color = CafeMadera,
                        fontFamily = FuenteMadera,
                        fontWeight = if (activa) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
                // Burbuja roja con la cantidad de suspendidos
                if (opcion == PestanaAdmin.USUARIOS && suspendidos > 0) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(RojoAdmin)
                            .border(1.5.dp, Color.White, CircleShape)
                    ) {
                        Text(
                            text = if (suspendidos > 9) "9+" else "$suspendidos",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ----- Usuarios: buscador, filtros y lista -----
@Composable
private fun PestanaUsuarios(
    estado: UiState<List<UsuarioAdmin>>,
    conteo: ConteoUsuarios,
    busqueda: String,
    filtro: FiltroUsuarios,
    uidActual: String?,
    procesando: String?,
    onBusqueda: (String) -> Unit,
    onFiltro: (FiltroUsuarios) -> Unit,
    onUsuarioClick: (UsuarioAdmin) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BuscadorMadera(
            valor = busqueda,
            onValorChange = onBusqueda,
            placeholder = "Nombre o correo",
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth()
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            items(FiltroUsuarios.entries) { opcion ->
                ChipFiltroUsuarios(
                    texto = opcion.etiqueta,
                    cantidad = conteo.de(opcion),
                    color = colorFiltro(opcion),
                    seleccionado = filtro == opcion,
                    onClick = { onFiltro(opcion) }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .desvanecerBordes(arriba = 20.dp, abajo = 20.dp)
        ) {
            when (estado) {
                is UiState.Cargando -> Cargando("Cargando usuarios...")
                is UiState.Error -> VacioView(estado.mensaje)
                is UiState.Exito -> {
                    if (estado.datos.isEmpty()) {
                        VacioView("No hay usuarios que coincidan")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(estado.datos, key = { it.uid }) { usuario ->
                                TarjetaUsuario(
                                    usuario = usuario,
                                    esYo = usuario.uid == uidActual,
                                    procesando = procesando == usuario.uid,
                                    onClick = { onUsuarioClick(usuario) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----- Registro: últimas acciones de los admins -----
@Composable
private fun PestanaRegistro(estado: UiState<List<AccionRegistro>>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .desvanecerBordes(arriba = 20.dp, abajo = 20.dp)
    ) {
        when (estado) {
            is UiState.Cargando -> Cargando("Cargando registro...")
            is UiState.Error -> VacioView(estado.mensaje)
            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    VacioView("Aún no hay acciones registradas")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(estado.datos, key = { it.id }) { accion ->
                            FilaRegistro(accion)
                        }
                    }
                }
            }
        }
    }
}

// ----- Sistema: base local, mantenimiento y fuentes de datos -----
@Composable
private fun PestanaSistema(
    estado: UiState<ResumenAdmin>,
    ocupado: AccionAdmin?,
    onReintentar: () -> Unit,
    onSincronizar: () -> Unit,
    onLimpiar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .desvanecerBordes(arriba = 20.dp, abajo = 20.dp)
    ) {
        when (estado) {
            is UiState.Cargando -> Cargando("Cargando datos...")
            is UiState.Error -> ErrorView(estado.mensaje, onReintentar = onReintentar)
            is UiState.Exito -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
            ) {
                AparecerSuave {
                    TarjetaTotales(datos = estado.datos)
                }
                Spacer(Modifier.height(20.dp))
                AparecerSuave(retrasoMs = 120) {
                    TarjetaMantenimiento(
                        ocupado = ocupado,
                        onSincronizar = onSincronizar,
                        onLimpiar = onLimpiar
                    )
                }
                Spacer(Modifier.height(20.dp))
                AparecerSuave(retrasoMs = 240) {
                    TarjetaFuentes()
                }
            }
        }
    }
}

// ===================== Piezas de Usuarios =====================

// Chip de filtro con su cantidad
@Composable
private fun ChipFiltroUsuarios(
    texto: String,
    cantidad: Int,
    color: Color,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(if (seleccionado) 3.dp else 1.dp, forma)
            .clip(forma)
            .background(
                if (seleccionado) Brush.verticalGradient(listOf(lerp(color, Color.White, 0.3f), color))
                else Brush.verticalGradient(listOf(Color.White, CremaAdmin))
            )
            .border(
                width = if (seleccionado) 1.5.dp else 1.dp,
                color = if (seleccionado) lerp(color, Color.Black, 0.2f) else CafeMadera.copy(alpha = 0.3f),
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(start = 12.dp, end = 6.dp, top = 5.dp, bottom = 5.dp)
    ) {
        Text(
            text = texto,
            color = if (seleccionado) Color.White else CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
        Spacer(Modifier.width(6.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(if (seleccionado) Color.White.copy(alpha = 0.3f) else color.copy(alpha = 0.15f))
                .padding(horizontal = 7.dp, vertical = 1.dp)
        ) {
            Text(
                text = "$cantidad",
                color = if (seleccionado) Color.White else lerp(color, Color.Black, 0.25f),
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

private fun colorFiltro(filtro: FiltroUsuarios): Color = when (filtro) {
    FiltroUsuarios.TODOS -> DoradoBorde
    FiltroUsuarios.ACTIVOS -> VerdeAdmin
    FiltroUsuarios.SUSPENDIDOS -> RojoAdmin
    FiltroUsuarios.ADMINS -> AzulAdmin
}

// Tarjeta de un usuario: foto con punto de estado, nombre, correo, último acceso y etiquetas
@Composable
private fun TarjetaUsuario(
    usuario: UsuarioAdmin,
    esYo: Boolean,
    procesando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(18.dp)
    val colorBorde = if (usuario.activo) DoradoBorde else RojoAdmin

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, forma)
            .clip(forma)
            .background(Brush.verticalGradient(listOf(Color.White, if (usuario.activo) CremaAdmin else RosaSuave)))
            .border(1.5.dp, colorBorde.copy(alpha = 0.6f), forma)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        FotoConEstado(foto = usuario.foto, activo = usuario.activo, tamano = 52)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = usuario.nombre,
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (esYo) {
                    Spacer(Modifier.width(6.dp))
                    EtiquetaMini(texto = "Tú", color = AzulAdmin)
                }
            }
            Text(
                text = usuario.correo ?: "Sin correo registrado",
                color = GrisAdmin,
                fontFamily = FuenteMadera,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Último acceso: ${haceCuanto(usuario.ultimoAcceso).replaceFirstChar { it.lowercase() }}",
                color = GrisAdmin.copy(alpha = 0.8f),
                fontFamily = FuenteMadera,
                fontSize = 12.sp,
                maxLines = 1
            )
            if (usuario.rol == Rol.ADMIN || !usuario.activo) {
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (usuario.rol == Rol.ADMIN) EtiquetaMini(texto = "Admin", color = DoradoBorde)
                    if (!usuario.activo) EtiquetaMini(texto = "Suspendida", color = RojoAdmin)
                }
            }
        }
        Spacer(Modifier.width(6.dp))
        if (procesando) {
            CircularProgressIndicator(color = DoradoBorde, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = CafeMadera.copy(alpha = 0.5f)
            )
        }
    }
}

// Foto del usuario con punto verde (activa) o rojo (suspendida)
@Composable
private fun FotoConEstado(foto: String?, activo: Boolean, tamano: Int) {
    Box(modifier = Modifier.size(tamano.dp)) {
        FotoUsuario(
            foto = foto,
            tamano = tamano.dp,
            modifier = Modifier.border(2.dp, Color.White, CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size((tamano / 4).dp)
                .clip(CircleShape)
                .background(if (activo) VerdeAdmin else RojoAdmin)
                .border(2.dp, Color.White, CircleShape)
        )
    }
}

// Etiqueta pequeña de color ("Tú", "Admin", "Suspendida")
@Composable
private fun EtiquetaMini(texto: String, color: Color) {
    Text(
        text = texto,
        color = lerp(color, Color.Black, 0.25f),
        fontFamily = FuenteMadera,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 1.dp)
    )
}

// ===================== Piezas de Registro =====================

// Ícono, color y verbo de cada acción
private data class EstiloAccion(val icono: ImageVector, val color: Color, val verbo: String)

private fun estiloDe(tipo: TipoAccion): EstiloAccion = when (tipo) {
    TipoAccion.SUSPENDER -> EstiloAccion(Icons.Rounded.Block, RojoAdmin, "suspendió a")
    TipoAccion.ACTIVAR -> EstiloAccion(Icons.Rounded.CheckCircle, VerdeAdmin, "activó a")
    TipoAccion.HACER_ADMIN -> EstiloAccion(Icons.Rounded.AddModerator, DoradoBorde, "hizo admin a")
    TipoAccion.QUITAR_ADMIN -> EstiloAccion(Icons.Rounded.RemoveModerator, NaranjaAdmin, "quitó el admin a")
    TipoAccion.RESTABLECER_CLAVE -> EstiloAccion(Icons.Rounded.LockReset, AzulAdmin, "envió un cambio de contraseña a")
}

// "Ana suspendió a Luis · Motivo: ... · Hace 2 h"
@Composable
private fun FilaRegistro(accion: AccionRegistro) {
    val estilo = estiloDe(accion.tipo)
    val forma = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, forma)
            .clip(forma)
            .background(Brush.verticalGradient(listOf(Color.White, CremaAdmin)))
            .border(1.dp, estilo.color.copy(alpha = 0.35f), forma)
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(estilo.color.copy(alpha = 0.15f))
        ) {
            Icon(estilo.icono, contentDescription = null, tint = estilo.color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(accion.adminNombre) }
                    append(" ${estilo.verbo} ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = lerp(estilo.color, Color.Black, 0.25f))) {
                        append(accion.usuarioNombre)
                    }
                },
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 14.sp
            )
            if (!accion.detalle.isNullOrBlank()) {
                Text(
                    text = if (accion.tipo == TipoAccion.SUSPENDER) "Motivo: ${accion.detalle}" else accion.detalle,
                    color = GrisAdmin,
                    fontFamily = FuenteMadera,
                    fontSize = 13.sp
                )
            }
            Text(
                text = haceCuanto(accion.fecha),
                color = GrisAdmin.copy(alpha = 0.8f),
                fontFamily = FuenteMadera,
                fontSize = 12.sp
            )
        }
    }
}

// ===================== Piezas de Sistema =====================

// Pokémon, favoritos y tipos guardados en el teléfono
@Composable
private fun TarjetaTotales(datos: ResumenAdmin) {
    TarjetaPestana(titulo = "Base local", icono = Icons.Rounded.Storage) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TotalAdmin(
                valor = datos.totalGuardado,
                etiqueta = "Pokémon",
                icono = Icons.Outlined.CatchingPokemon,
                color = RojoAdmin,
                modifier = Modifier.weight(1f)
            )
            TotalAdmin(
                valor = datos.totalFavoritos,
                etiqueta = "Favoritos",
                icono = Icons.Rounded.Favorite,
                color = RosaAdmin,
                modifier = Modifier.weight(1f)
            )
            TotalAdmin(
                valor = datos.totalTipos,
                etiqueta = "Tipos",
                icono = Icons.Rounded.Category,
                color = AzulAdmin,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Cuadro con ícono, número que sube animado y etiqueta
@Composable
private fun TotalAdmin(
    valor: Int,
    etiqueta: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    var listo by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { listo = true }
    val mostrado by animateIntAsState(
        targetValue = if (listo) valor else 0,
        animationSpec = tween(durationMillis = 900),
        label = "total"
    )
    val forma = RoundedCornerShape(16.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(forma)
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.25f), forma)
            .padding(horizontal = 4.dp, vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .shadow(3.dp, CircleShape, ambientColor = color, spotColor = color)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(lerp(color, Color.White, 0.45f), color)))
                .border(2.dp, Color.White, CircleShape)
        ) {
            Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$mostrado",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            maxLines = 1
        )
        Text(
            text = etiqueta,
            color = GrisAdmin,
            fontFamily = FuenteMadera,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

// Sincronizar y limpiar caché
@Composable
private fun TarjetaMantenimiento(
    ocupado: AccionAdmin?,
    onSincronizar: () -> Unit,
    onLimpiar: () -> Unit
) {
    TarjetaPestana(titulo = "Mantenimiento", icono = Icons.Rounded.Build) {
        FilaOpcion(
            icono = Icons.Rounded.Sync,
            titulo = "Sincronizar",
            subtitulo = "Descarga los Pokémon desde PokeAPI",
            color = VerdeAdmin,
            cargando = ocupado == AccionAdmin.SINCRONIZAR,
            habilitado = ocupado == null,
            onClick = onSincronizar
        )
        DivisorSuave()
        FilaOpcion(
            icono = Icons.Rounded.DeleteSweep,
            titulo = "Limpiar caché",
            subtitulo = "Borra los Pokémon y favoritos de este teléfono",
            color = RojoAdmin,
            peligro = true,
            cargando = ocupado == AccionAdmin.LIMPIAR,
            habilitado = ocupado == null,
            onClick = onLimpiar
        )
    }
}

// De dónde sale cada dato de la app
@Composable
private fun TarjetaFuentes() {
    TarjetaPestana(titulo = "Fuentes de datos", icono = Icons.Rounded.Info) {
        FilaOpcion(icono = Icons.Rounded.Cloud, titulo = "PokeAPI", subtitulo = "Pokémon, tipos y evoluciones", color = AzulAdmin)
        DivisorSuave()
        FilaOpcion(icono = Icons.Rounded.Style, titulo = "TCGdex", subtitulo = "Cartas del juego de cartas", color = DoradoBorde)
        DivisorSuave()
        FilaOpcion(icono = Icons.Rounded.PhoneAndroid, titulo = "Room", subtitulo = "Copia en el teléfono para usar sin internet", color = VerdeAdmin)
        DivisorSuave()
        FilaOpcion(icono = Icons.Rounded.LocalFireDepartment, titulo = "Firebase", subtitulo = "Cuentas, roles, perfiles y registro de acciones", color = NaranjaAdmin)
    }
}

// ===================== Aviso =====================

// Aviso verde (éxito) o rojo (error) que aparece y se oculta solo
@Composable
private fun AvisoResultado(
    aviso: AvisoAdmin?,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Recuerda el último para que el texto no desaparezca mientras se oculta
    var recordado by remember { mutableStateOf<AvisoAdmin?>(null) }
    SideEffect { if (aviso != null) recordado = aviso }
    val mostrado = aviso ?: recordado
    val color = if (mostrado?.esError == true) RojoAdmin else VerdeAdmin
    val forma = RoundedCornerShape(16.dp)

    AnimatedVisibility(
        visible = aviso != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth()
                .shadow(3.dp, forma)
                .clip(forma)
                .background(CremaAdmin)
                .border(1.5.dp, color.copy(alpha = 0.6f), forma)
                .padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Icon(
                imageVector = if (mostrado?.esError == true) Icons.Rounded.ErrorOutline else Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = mostrado?.texto.orEmpty(),
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onCerrar)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Cerrar", tint = CafeMadera.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ===================== Ventanas =====================

// Ficha del usuario con sus acciones
@Composable
private fun DialogoUsuario(
    usuario: UsuarioAdmin,
    esYo: Boolean,
    onSuspender: () -> Unit,
    onActivar: () -> Unit,
    onCambiarRol: () -> Unit,
    onRestablecer: () -> Unit,
    onCerrar: () -> Unit
) {
    VentanaMadera(
        titulo = usuario.nombre,
        icono = Icons.Rounded.Person,
        onCerrar = onCerrar,
        cabecera = { FotoConEstado(foto = usuario.foto, activo = usuario.activo, tamano = 84) }
    ) {
        Text(
            text = usuario.correo ?: "Sin correo registrado",
            color = GrisAdmin,
            fontFamily = FuenteMadera,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            EtiquetaMini(
                texto = if (usuario.rol == Rol.ADMIN) "Administrador" else "Entrenador",
                color = if (usuario.rol == Rol.ADMIN) DoradoBorde else AzulAdmin
            )
            EtiquetaMini(
                texto = if (usuario.activo) "Activa" else "Suspendida",
                color = if (usuario.activo) VerdeAdmin else RojoAdmin
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Último acceso: ${haceCuanto(usuario.ultimoAcceso).replaceFirstChar { it.lowercase() }}",
            color = GrisAdmin,
            fontFamily = FuenteMadera,
            fontSize = 13.sp
        )

        // Motivo de la suspensión actual
        if (!usuario.activo && !usuario.motivoSuspension.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(RosaSuave)
                    .border(1.dp, RojoAdmin.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text("Motivo", color = RojoAdmin, fontFamily = FuenteMadera, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(usuario.motivoSuspension, color = CafeMadera, fontFamily = FuenteMadera, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        if (esYo) {
            Text(
                text = "Esta es tu cuenta: no puedes suspenderte ni quitarte el rol de admin.",
                color = GrisAdmin,
                fontFamily = FuenteMadera,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        } else {
            if (usuario.activo) {
                FilaOpcion(
                    icono = Icons.Rounded.Block,
                    titulo = "Suspender cuenta",
                    subtitulo = "No podrá iniciar sesión",
                    color = RojoAdmin,
                    peligro = true,
                    onClick = onSuspender
                )
            } else {
                FilaOpcion(
                    icono = Icons.Rounded.CheckCircle,
                    titulo = "Activar cuenta",
                    subtitulo = "Podrá volver a iniciar sesión",
                    color = VerdeAdmin,
                    onClick = onActivar
                )
            }
            DivisorSuave()
            if (usuario.rol == Rol.ADMIN) {
                FilaOpcion(
                    icono = Icons.Rounded.RemoveModerator,
                    titulo = "Quitar admin",
                    subtitulo = "Ya no podrá entrar al Panel Admin",
                    color = NaranjaAdmin,
                    onClick = onCambiarRol
                )
            } else {
                FilaOpcion(
                    icono = Icons.Rounded.AddModerator,
                    titulo = "Hacer admin",
                    subtitulo = "Podrá gestionar usuarios y datos",
                    color = DoradoBorde,
                    onClick = onCambiarRol
                )
            }
            DivisorSuave()
        }
        FilaOpcion(
            icono = Icons.Rounded.LockReset,
            titulo = "Restablecer contraseña",
            subtitulo = if (usuario.correo != null) "Enviar un correo para crear otra clave" else "Sin correo registrado",
            color = AzulAdmin,
            habilitado = usuario.correo != null,
            onClick = onRestablecer
        )
        Spacer(Modifier.height(12.dp))
        BotonPildora(
            texto = "Cerrar",
            fondo = FondoPildora.Dorado,
            colorTexto = CafeMadera,
            onClick = onCerrar,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Suspender con motivo opcional
@Composable
private fun DialogoSuspender(
    usuario: UsuarioAdmin,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var motivo by remember { mutableStateOf("") }

    VentanaMadera(
        titulo = "¿Suspender a ${usuario.nombre}?",
        icono = Icons.Rounded.Block,
        colorIcono = RojoAdmin,
        onCerrar = onCancelar
    ) {
        Text(
            text = "No podrá iniciar sesión hasta que la actives de nuevo. Si tiene la app abierta, su sesión se cerrará.",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        CampoCrema(
            valor = motivo,
            onCambio = { if (it.length <= LARGO_MOTIVO) motivo = it },
            etiqueta = "Motivo (opcional)",
            icono = Icons.Rounded.EditNote,
            unaLinea = false
        )
        Text(
            text = "${motivo.length}/$LARGO_MOTIVO · El usuario verá este motivo",
            color = GrisAdmin,
            fontFamily = FuenteMadera,
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, end = 6.dp)
        )
        Spacer(Modifier.height(14.dp))
        BotonesVentana(
            textoConfirmar = "Suspender",
            fondoConfirmar = FondoPildora.Rojo,
            onConfirmar = { onConfirmar(motivo) },
            onCancelar = onCancelar
        )
    }
}

// Confirmación genérica (rol, restablecer contraseña, limpiar caché)
@Composable
private fun DialogoConfirmar(
    titulo: String,
    texto: String,
    icono: ImageVector,
    color: Color,
    textoConfirmar: String,
    fondoConfirmar: Brush,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    VentanaMadera(titulo = titulo, icono = icono, colorIcono = color, onCerrar = onCancelar) {
        Text(
            text = texto,
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        BotonesVentana(
            textoConfirmar = textoConfirmar,
            fondoConfirmar = fondoConfirmar,
            onConfirmar = onConfirmar,
            onCancelar = onCancelar
        )
    }
}

// ===================== Fechas =====================

// "Hace un momento", "Hace 5 min", "Hace 3 h", "Ayer", "Hace 4 días" o la fecha
private fun haceCuanto(milisegundos: Long?): String {
    if (milisegundos == null) return "Nunca"
    val minutos = (System.currentTimeMillis() - milisegundos) / 60_000
    return when {
        minutos < 1 -> "Hace un momento"
        minutos < 60 -> "Hace $minutos min"
        minutos < 60 * 24 -> "Hace ${minutos / 60} h"
        minutos < 60 * 24 * 2 -> "Ayer"
        minutos < 60 * 24 * 7 -> "Hace ${minutos / (60 * 24)} días"
        else -> SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es-PE")).format(Date(milisegundos))
    }
}