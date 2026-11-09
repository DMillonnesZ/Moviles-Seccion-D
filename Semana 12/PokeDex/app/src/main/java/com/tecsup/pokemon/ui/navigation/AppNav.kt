package com.tecsup.pokemon.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.ui.AppViewModelProvider
import com.tecsup.pokemon.ui.admin.AdminScreen
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.CortinaCuadros
import com.tecsup.pokemon.ui.detalle.DetalleScreen
import com.tecsup.pokemon.ui.favoritos.FavoritosScreen
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.registro.RegistroScreen
import com.tecsup.pokemon.ui.splash.SplashPokedex
import com.tecsup.pokemon.ui.tipos.TiposScreen

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val TIPOS = "tipos"
    const val LISTA = "lista/{tipo}"
    const val DETALLE = "detalle/{id}"
    const val FAVORITOS = "favoritos"
    const val ADMIN = "admin"
    fun lista(tipo: String) = "lista/$tipo"
    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun AppNav() {
    // Vive mientras la app esté abierta: sabe quién está logueado y su rol
    val sesionViewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val estadoSesion by sesionViewModel.estado.collectAsStateWithLifecycle()

    // El rol viene de Firestore a través de la sesión
    val esAdmin = (estadoSesion as? EstadoSesion.Activa)?.sesion?.rol == Rol.ADMIN

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
                    // Pantalla inicial: Tipos si ya había sesión, Login si no
                    val inicio = remember { if (sesion is EstadoSesion.Activa) Rutas.TIPOS else Rutas.LOGIN }

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
                                    navController.navigate(Rutas.TIPOS) {
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

                        composable(Rutas.TIPOS) {
                            TiposScreen(
                                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                esAdmin = esAdmin,
                                onTipoClick = { tipo -> navController.navigate(Rutas.lista(tipo)) },
                                onFavoritosClick = { navController.navigate(Rutas.FAVORITOS) },
                                onAdminClick = { navController.navigate(Rutas.ADMIN) },
                                onCerrarSesion = {
                                    sesionViewModel.cerrarSesion()
                                    navController.navigate(Rutas.LOGIN) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = Rutas.LISTA,
                            arguments = listOf(navArgument("tipo") { type = NavType.StringType })
                        ) {
                            ListaScreen(
                                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                onItemClick = { id -> navController.navigate(Rutas.detalle(id)) },
                                onVolver = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Rutas.DETALLE,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            DetalleScreen(
                                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                onVolver = { navController.popBackStack() }
                            )
                        }

                        composable(Rutas.FAVORITOS) {
                            FavoritosScreen(
                                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                                onItemClick = { id -> navController.navigate(Rutas.detalle(id)) },
                                onVolver = { navController.popBackStack() }
                            )
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
                }
            }
        }

        // Splash encima de todo; al terminar se desvanece suavemente y deja ver Login o Tipos
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