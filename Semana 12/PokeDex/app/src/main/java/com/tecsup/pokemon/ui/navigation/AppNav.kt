package com.tecsup.pokemon.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.tecsup.pokemon.ui.detalle.DetalleScreen
import com.tecsup.pokemon.ui.favoritos.FavoritosScreen
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.registro.RegistroScreen
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

    when (val sesion = estadoSesion) {
        // Mientras se revisa si había sesión guardada
        EstadoSesion.Verificando -> Cargando("Verificando sesión...")

        else -> {
            val navController = rememberNavController()
            // Pantalla inicial: Tipos si ya había sesión, Login si no
            val inicio = remember { if (sesion is EstadoSesion.Activa) Rutas.TIPOS else Rutas.LOGIN }

            NavHost(navController = navController, startDestination = inicio) {

                // El login entra con fundido y un acercamiento suave
                composable(
                    route = Rutas.LOGIN,
                    enterTransition = {
                        fadeIn(tween(700)) + scaleIn(initialScale = 1.1f, animationSpec = tween(700))
                    }
                ) {
                    LoginScreen(
                        viewModel = viewModel(factory = AppViewModelProvider.Factory),
                        onLoginExitoso = { nuevaSesion ->
                            sesionViewModel.sesionIniciada(nuevaSesion)
                            navController.navigate(Rutas.TIPOS) {
                                popUpTo(Rutas.LOGIN) { inclusive = true }
                            }
                        },
                        onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
                    )
                }

                // El registro sube desde abajo y al volver baja
                composable(
                    route = Rutas.REGISTRO,
                    enterTransition = {
                        fadeIn(tween(500)) + slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            tween(500)
                        )
                    },
                    popExitTransition = {
                        fadeOut(tween(400)) + slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            tween(400)
                        )
                    }
                ) {
                    RegistroScreen(
                        viewModel = viewModel(factory = AppViewModelProvider.Factory),
                        onCuentaCreada = {
                            // Login nuevo con su animación; se borran Login y Registro del historial
                            navController.navigate(Rutas.LOGIN) {
                                popUpTo(Rutas.LOGIN) { inclusive = true }
                            }
                        },
                        onIrALogin = { navController.popBackStack() }
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