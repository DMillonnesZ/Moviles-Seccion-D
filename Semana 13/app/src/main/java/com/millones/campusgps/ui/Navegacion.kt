package com.millones.campusgps.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.millones.campusgps.ui.componentes.BarraNavegacion
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.CortinaCuadros
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.ItemBarra
import com.millones.campusgps.ui.componentes.LocalRellenoBarra
import com.millones.campusgps.ui.componentes.PantallaCompleta
import com.millones.campusgps.viewmodel.AuthViewModel
import com.millones.campusgps.viewmodel.AuthViewModelFactory

private object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val OLVIDO = "olvido"

    // Grafo principal (con barra de navegación)
    const val PRINCIPAL = "principal"
    const val MAPA = "mapa"
    const val EVENTOS = "eventos"
    const val CHAT = "chat"
    const val PERFIL = "perfil"
}

private val RUTAS_ACCESO = setOf(Rutas.LOGIN, Rutas.REGISTRO, Rutas.OLVIDO)

private data class Pestana(val ruta: String, val item: ItemBarra)

private val PESTANAS = listOf(
    Pestana(Rutas.MAPA, ItemBarra("Mapa", Icons.Rounded.Map)),
    Pestana(Rutas.EVENTOS, ItemBarra("Eventos", Icons.Rounded.Event)),
    Pestana(Rutas.CHAT, ItemBarra("Chat", Icons.Rounded.Forum)),
    Pestana(Rutas.PERFIL, ItemBarra("Perfil", Icons.Rounded.Person))
)

private fun esPestana(ruta: String?): Boolean = PESTANAS.any { it.ruta == ruta }

@Composable
fun NavegacionApp(authVm: AuthViewModel) {
    val nav = rememberNavController()
    val usuario by authVm.usuarioActivo.collectAsState()
    val nombre by authVm.nombreActivo.collectAsState()

    // Se decide una sola vez: NavHost no debe cambiar su inicio después de crearse
    val inicio = remember { if (usuario == null) Rutas.LOGIN else Rutas.PRINCIPAL }

    // El splash se muestra una sola vez (también se recuerda al girar la pantalla)
    var splashTerminado by rememberSaveable { mutableStateOf(false) }

    // Acción que se ejecuta cuando la cortina de cuadros cubre la pantalla
    var accionCortina by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Inicia la cortina solo si no hay otra corriendo
    fun conCortina(accion: () -> Unit) {
        if (accionCortina == null) accionCortina = accion
    }

    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val mostrarBarra = esPestana(rutaActual)
    val pestanaActual = PESTANAS.indexOfFirst { it.ruta == rutaActual }.coerceAtLeast(0)

    // Cambia de pestaña conservando el estado de cada una (el mapa no se recarga)
    fun irAPestana(indice: Int) {
        val ruta = PESTANAS[indice].ruta
        if (ruta == rutaActual) return
        nav.navigate(ruta) {
            popUpTo(Rutas.MAPA) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    PantallaCompleta()

    Box(modifier = Modifier.fillMaxSize()) {
        // Las pantallas se crean cuando termina el splash, así sus animaciones de entrada se ven
        if (splashTerminado) {
        // Scaffold principal: la barra va en bottomBar y flota sobre las pantallas.
        // Su alto (0 cuando está oculta) llega a cada pantalla con LocalRellenoBarra.
        Scaffold(
            containerColor = CampusColores.AzulProfundo,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                AnimatedVisibility(
                    visible = mostrarBarra,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    BarraNavegacion(
                        items = PESTANAS.map { it.item },
                        seleccionada = pestanaActual,
                        onSeleccionar = { irAPestana(it) },
                        modifier = Modifier
                            .windowInsetsPadding(InsetsCampus.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    )
                }
            }
        ) { espacio ->
        CompositionLocalProvider(LocalRellenoBarra provides espacio.calculateBottomPadding()) {
            NavHost(
                navController = nav,
                startDestination = inicio,
                // Entre pestañas: fundido. Entre pantallas de acceso: lo tapa la cortina.
                enterTransition = {
                    if (esPestana(initialState.destination.route) && esPestana(targetState.destination.route)) {
                        fadeIn(tween(250))
                    } else {
                        EnterTransition.None
                    }
                },
                exitTransition = {
                    if (esPestana(initialState.destination.route) && esPestana(targetState.destination.route)) {
                        fadeOut(tween(200))
                    } else {
                        ExitTransition.None
                    }
                },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                composable(
                    route = Rutas.LOGIN,
                    // Entra con fundido y acercamiento (después de crear la cuenta)
                    enterTransition = {
                        fadeIn(tween(700)) + scaleIn(initialScale = 1.1f, animationSpec = tween(700))
                    },
                    // Al volver desde Registro u Olvido lo tapa la cortina
                    popEnterTransition = { EnterTransition.None }
                ) {
                    PantallaLogin(
                        vm = viewModel(factory = AuthViewModelFactory()),
                        // Login correcto: cortina, mapa y se borra el historial de acceso
                        onLoginExitoso = {
                            conCortina {
                                nav.navigate(Rutas.PRINCIPAL) {
                                    popUpTo(nav.graph.id) { inclusive = true }
                                }
                            }
                        },
                        onRegistro = { conCortina { nav.navigate(Rutas.REGISTRO) } },
                        onOlvido = { conCortina { nav.navigate(Rutas.OLVIDO) } }
                    )
                }

                composable(
                    route = Rutas.REGISTRO,
                    // Al ir al login después de crear la cuenta
                    exitTransition = { fadeOut(tween(400)) }
                ) {
                    // Botón atrás del celular: también vuelve con cortina
                    BackHandler { conCortina { nav.popBackStack() } }
                    PantallaRegistro(
                        vm = viewModel(factory = AuthViewModelFactory()),
                        // Login nuevo con su animación; se borran Login y Registro del historial
                        onCuentaCreada = {
                            nav.navigate(Rutas.LOGIN) {
                                popUpTo(Rutas.LOGIN) { inclusive = true }
                            }
                        },
                        onIrLogin = { conCortina { nav.popBackStack() } }
                    )
                }

                composable(Rutas.OLVIDO) {
                    BackHandler { conCortina { nav.popBackStack() } }
                    PantallaOlvidoClave(
                        vm = viewModel(factory = AuthViewModelFactory()),
                        onIrLogin = { conCortina { nav.popBackStack() } }
                    )
                }

                navigation(route = Rutas.PRINCIPAL, startDestination = Rutas.MAPA) {
                    composable(Rutas.MAPA) {
                        PantallaMapa()
                    }
                    composable(Rutas.EVENTOS) {
                        PantallaProximamente(
                            titulo = "Eventos",
                            subtitulo = "Lo que pasa en el campus",
                            icono = Icons.Rounded.Event,
                            mensaje = "Aquí verás los eventos del campus con su imagen y descripción."
                        )
                    }
                    composable(Rutas.CHAT) {
                        PantallaProximamente(
                            titulo = "Chat",
                            subtitulo = "Sala general del campus",
                            icono = Icons.Rounded.Forum,
                            mensaje = "Aquí podrás conversar en tiempo real con otros estudiantes."
                        )
                    }
                    composable(Rutas.PERFIL) {
                        PantallaPerfil(
                            correo = usuario,
                            nombre = nombre,
                            onCerrarSesion = authVm::cerrarSesion
                        )
                    }
                }
            }

        }
        }
        }

        // Splash encima de todo; al terminar se desvanece y se agranda un poco
        AnimatedVisibility(
            visible = !splashTerminado,
            enter = EnterTransition.None,
            exit = fadeOut(tween(600)) + scaleOut(targetScale = 1.08f, animationSpec = tween(600))
        ) {
            PantallaSplash(onTerminar = { splashTerminado = true })
        }

        // Cortina de cuadros encima de todo
        CortinaCuadros(
            accion = accionCortina,
            onTerminar = { accionCortina = null }
        )
    }

    // Si la sesión se cierra (desde Perfil o porque Firebase la invalidó), vuelve al login.
    // La entrada al mapa NO se decide aquí: la avisa PantallaLogin, porque el registro
    // abre y cierra una sesión de Firebase y eso dispararía la navegación por error.
    LaunchedEffect(usuario) {
        val ruta = nav.currentDestination?.route
        if (usuario == null && ruta != null && ruta !in RUTAS_ACCESO) {
            conCortina {
                nav.navigate(Rutas.LOGIN) {
                    popUpTo(nav.graph.id) { inclusive = true }
                }
            }
        }
    }
}
