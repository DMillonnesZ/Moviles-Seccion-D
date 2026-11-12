package com.tecsup.pokemon.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.ui.AppViewModelProvider
import com.tecsup.pokemon.ui.admin.AdminScreen
import com.tecsup.pokemon.ui.components.BarraNavegacion
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.CortinaCuadros
import com.tecsup.pokemon.ui.components.ItemBarra
import com.tecsup.pokemon.ui.components.LocalRellenoBarra
import com.tecsup.pokemon.ui.detalle.DetalleScreen
import com.tecsup.pokemon.ui.evolucion.EvolucionDetalleScreen
import com.tecsup.pokemon.ui.evolucion.EvolucionScreen
import com.tecsup.pokemon.ui.favoritos.FavoritosScreen
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.perfil.PerfilScreen
import com.tecsup.pokemon.ui.registro.RegistroScreen
import com.tecsup.pokemon.ui.splash.SplashPokedex
import com.tecsup.pokemon.ui.tipos.TiposScreen

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val TIPOS = "tipos"
    const val LISTA = "lista/{tipo}"
    const val DETALLE = "detalle/{id}?tipo={tipo}"
    const val EVOLUCION = "evolucion"
    const val EVOLUCION_DETALLE = "evolucion/{cadenaId}"
    const val FAVORITOS = "favoritos"
    const val PERFIL = "perfil"
    const val ADMIN = "admin"

    // Grafos de cada pestaña (cada una recuerda su propio historial)
    const val PESTANA_INICIO = "pestana_inicio"
    const val PESTANA_EVOLUCION = "pestana_evolucion"
    const val PESTANA_FAVORITOS = "pestana_favoritos"
    const val PESTANA_PERFIL = "pestana_perfil"

    fun lista(tipo: String) = "lista/$tipo"

    // El tipo es opcional: indica con qué tipo se abre el Detalle (para su color)
    fun detalle(id: Int, tipo: String? = null) =
        if (tipo != null) "detalle/$id?tipo=$tipo" else "detalle/$id"

    fun evolucionDetalle(cadenaId: Int) = "evolucion/$cadenaId"
}

// Pestaña de la barra: su grafo, su pantalla de inicio y cómo se ve
private data class Pestana(
    val grafo: String,
    val inicio: String,
    val item: ItemBarra
)

private val PESTANAS = listOf(
    Pestana(Rutas.PESTANA_INICIO, Rutas.TIPOS, ItemBarra("Inicio", Icons.Rounded.Home)),
    Pestana(Rutas.PESTANA_EVOLUCION, Rutas.EVOLUCION, ItemBarra("Evolución", Icons.Rounded.Timeline)),
    Pestana(Rutas.PESTANA_FAVORITOS, Rutas.FAVORITOS, ItemBarra("Favoritos", Icons.Rounded.Favorite)),
    Pestana(Rutas.PESTANA_PERFIL, Rutas.PERFIL, ItemBarra("Perfil", Icons.Rounded.Person))
)

// Posición de la pestaña Favoritos (para su contador)
private const val INDICE_FAVORITOS = 2

// Pantallas donde se ve la barra (se oculta en Detalle y Admin)
private val RUTAS_CON_BARRA = setOf(
    Rutas.TIPOS, Rutas.LISTA, Rutas.EVOLUCION, Rutas.EVOLUCION_DETALLE, Rutas.FAVORITOS, Rutas.PERFIL
)

@Composable
fun AppNav() {
    // Vive mientras la app esté abierta: sabe quién está logueado y su rol
    val sesionViewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val estadoSesion by sesionViewModel.estado.collectAsStateWithLifecycle()

    // Cantidad de favoritos para el contador de la barra
    val barraViewModel: BarraViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val cantidadFavoritos by barraViewModel.cantidadFavoritos.collectAsStateWithLifecycle()

    val sesionActiva = (estadoSesion as? EstadoSesion.Activa)?.sesion
    val esAdmin = sesionActiva?.rol == Rol.ADMIN

    // El splash se muestra una sola vez (también se recuerda al girar la pantalla)
    var splashTerminado by rememberSaveable { mutableStateOf(false) }

    // Acción que se ejecuta cuando la cortina de cuadros cubre la pantalla
    var accionCortina by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Inicia la cortina solo si no hay otra corriendo
    fun conCortina(accion: () -> Unit) {
        if (accionCortina == null) accionCortina = accion
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (splashTerminado) {
            when (val sesion = estadoSesion) {
                // Respaldo: el splash ya espera la sesión, normalmente no se ve
                EstadoSesion.Verificando -> Cargando("Verificando sesión...")

                else -> {
                    val navController = rememberNavController()
                    // Pantalla inicial: la pestaña Inicio si ya había sesión, Login si no
                    val inicio = remember {
                        if (sesion is EstadoSesion.Activa) Rutas.PESTANA_INICIO else Rutas.LOGIN
                    }

                    // Pantalla y pestaña actuales
                    val entradaActual by navController.currentBackStackEntryAsState()
                    val destinoActual = entradaActual?.destination
                    val mostrarBarra = destinoActual?.route in RUTAS_CON_BARRA
                    val pestanaActual = PESTANAS.indexOfFirst { p ->
                        destinoActual?.hierarchy?.any { it.route == p.grafo } == true
                    }.coerceAtLeast(0)

                    // Cambiar de pestaña conservando el historial de cada una
                    fun irAPestana(indice: Int) {
                        val pestana = PESTANAS[indice]
                        if (indice == pestanaActual) {
                            // Tocar la pestaña actual regresa a su inicio
                            navController.popBackStack(pestana.inicio, inclusive = false)
                        } else {
                            navController.navigate(pestana.grafo) {
                                popUpTo(Rutas.TIPOS) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }

                    CompositionLocalProvider(LocalRellenoBarra provides if (mostrarBarra) 96.dp else 0.dp) {
                        Box(modifier = Modifier.fillMaxSize()) {

                            NavHost(navController = navController, startDestination = inicio) {

                                composable(
                                    route = Rutas.LOGIN,
                                    // Entra con fundido y acercamiento (después de crear la cuenta)
                                    enterTransition = {
                                        fadeIn(tween(700)) + scaleIn(initialScale = 1.1f, animationSpec = tween(700))
                                    },
                                    // Hacia Registro el cambio lo tapa la cortina; hacia otras pantallas, fundido
                                    exitTransition = {
                                        if (targetState.destination.route == Rutas.REGISTRO) ExitTransition.None
                                        else fadeOut(tween(300))
                                    },
                                    // Al volver desde Registro también lo tapa la cortina
                                    popEnterTransition = { EnterTransition.None }
                                ) {
                                    LoginScreen(
                                        viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                        onLoginExitoso = { nuevaSesion ->
                                            sesionViewModel.sesionIniciada(nuevaSesion)
                                            navController.navigate(Rutas.PESTANA_INICIO) {
                                                popUpTo(Rutas.LOGIN) { inclusive = true }
                                            }
                                        },
                                        // Login -> Registro con cortina
                                        onIrARegistro = {
                                            conCortina { navController.navigate(Rutas.REGISTRO) }
                                        }
                                    )
                                }

                                composable(
                                    route = Rutas.REGISTRO,
                                    // Aparece y desaparece debajo de la cortina
                                    enterTransition = { EnterTransition.None },
                                    popExitTransition = { ExitTransition.None },
                                    // Al ir al Login después de crear la cuenta
                                    exitTransition = { fadeOut(tween(400)) }
                                ) {
                                    // Botón atrás del celular: también vuelve con cortina
                                    BackHandler {
                                        conCortina { navController.popBackStack() }
                                    }

                                    RegistroScreen(
                                        viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                        onCuentaCreada = {
                                            // Login nuevo con su animación; se borran Login y Registro del historial
                                            navController.navigate(Rutas.LOGIN) {
                                                popUpTo(Rutas.LOGIN) { inclusive = true }
                                            }
                                        },
                                        // Registro -> Login con cortina
                                        onIrALogin = {
                                            conCortina { navController.popBackStack() }
                                        }
                                    )
                                }

                                // ----- Pestaña Inicio: Tipos -> Lista -> Detalle -----
                                navigation(route = Rutas.PESTANA_INICIO, startDestination = Rutas.TIPOS) {

                                    composable(Rutas.TIPOS) {
                                        TiposScreen(
                                            viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                            onTipoClick = { tipo -> navController.navigate(Rutas.lista(tipo)) }
                                        )
                                    }

                                    composable(
                                        route = Rutas.LISTA,
                                        arguments = listOf(navArgument("tipo") { type = NavType.StringType })
                                    ) { entrada ->
                                        val tipoLista = entrada.arguments?.getString("tipo")
                                        ListaScreen(
                                            viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                            // Se pasa el tipo de la lista para el color del Detalle
                                            onItemClick = { id -> navController.navigate(Rutas.detalle(id, tipoLista)) },
                                            onVolver = { navController.popBackStack() }
                                        )
                                    }

                                    composable(
                                        route = Rutas.DETALLE,
                                        arguments = listOf(
                                            navArgument("id") { type = NavType.IntType },
                                            navArgument("tipo") {
                                                type = NavType.StringType
                                                nullable = true
                                                defaultValue = null
                                            }
                                        )
                                    ) { entrada ->
                                        val tipoOrigen = entrada.arguments?.getString("tipo")
                                        DetalleScreen(
                                            viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                            onVolver = { navController.popBackStack() },
                                            // Tocar una etapa de la evolución abre su detalle, con el mismo tipo de origen
                                            onEtapaClick = { id -> navController.navigate(Rutas.detalle(id, tipoOrigen)) }
                                        )
                                    }
                                }

                                // ----- Pestaña Evolución: Lista de cadenas -> Detalle de la evolución -----
                                navigation(route = Rutas.PESTANA_EVOLUCION, startDestination = Rutas.EVOLUCION) {
                                    composable(Rutas.EVOLUCION) {
                                        EvolucionScreen(
                                            viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                            onCadenaClick = { id -> navController.navigate(Rutas.evolucionDetalle(id)) }
                                        )
                                    }

                                    composable(
                                        route = Rutas.EVOLUCION_DETALLE,
                                        arguments = listOf(navArgument("cadenaId") { type = NavType.IntType })
                                    ) {
                                        EvolucionDetalleScreen(
                                            viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                            onVolver = { navController.popBackStack() }
                                        )
                                    }
                                }

                                // ----- Pestaña Favoritos -----
                                navigation(route = Rutas.PESTANA_FAVORITOS, startDestination = Rutas.FAVORITOS) {
                                    composable(Rutas.FAVORITOS) {
                                        FavoritosScreen(
                                            viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                            // El Detalle se abre con el tipo de la tarjeta (el filtrado, si hay)
                                            onItemClick = { id, tipo -> navController.navigate(Rutas.detalle(id, tipo)) },
                                            onVolver = { irAPestana(0) }
                                        )
                                    }
                                }

                                // ----- Pestaña Perfil -----
                                navigation(route = Rutas.PESTANA_PERFIL, startDestination = Rutas.PERFIL) {
                                    composable(Rutas.PERFIL) {
                                        PerfilScreen(
                                            sesion = sesionActiva,
                                            onAdminClick = { navController.navigate(Rutas.ADMIN) },
                                            onCerrarSesion = {
                                                sesionViewModel.cerrarSesion()
                                                navController.navigate(Rutas.LOGIN) {
                                                    popUpTo(navController.graph.id) { inclusive = true }
                                                }
                                            }
                                        )
                                    }
                                }

                                composable(Rutas.ADMIN) {
                                    if (esAdmin) {
                                        AdminScreen(
                                            viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                            onVolver = { navController.popBackStack() }
                                        )
                                    } else {
                                        // Protección: si no es admin, regresa
                                        LaunchedEffect(Unit) { navController.popBackStack() }
                                    }
                                }
                            }

                            // Barra de navegación abajo; se esconde deslizándose en Detalle y Admin
                            AnimatedVisibility(
                                visible = mostrarBarra,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = slideOutVertically { it } + fadeOut(),
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                BarraNavegacion(
                                    // Favoritos lleva el contador
                                    items = PESTANAS.mapIndexed { indice, pestana ->
                                        if (indice == INDICE_FAVORITOS) pestana.item.copy(insignia = cantidadFavoritos)
                                        else pestana.item
                                    },
                                    seleccionada = pestanaActual,
                                    onSeleccionar = { irAPestana(it) },
                                    modifier = Modifier
                                        .navigationBarsPadding()
                                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Splash encima de todo; al terminar se desvanece suavemente y deja ver Login o Inicio
        AnimatedVisibility(
            visible = !splashTerminado,
            enter = EnterTransition.None,
            exit = fadeOut(tween(700))
        ) {
            SplashPokedex(
                sesionLista = estadoSesion !is EstadoSesion.Verificando,
                onTerminar = { splashTerminado = true }
            )
        }

        // Cortina de cuadros encima de todas las pantallas
        CortinaCuadros(
            accion = accionCortina,
            onTerminar = { accionCortina = null }
        )
    }
}