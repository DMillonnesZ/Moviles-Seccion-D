package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saludplus.citas.ui.screens.agendamiento.CitaExitosaScreen
import com.saludplus.citas.ui.screens.agendamiento.ConfirmarCitaScreen
import com.saludplus.citas.ui.screens.agendamiento.EspecialidadesScreen
import com.saludplus.citas.ui.screens.agendamiento.FechaHoraScreen
import com.saludplus.citas.ui.screens.agendamiento.MedicosScreen
import com.saludplus.citas.ui.screens.auth.LoginScreen
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen
import com.saludplus.citas.ui.screens.citas.DetalleCitaScreen
import com.saludplus.citas.ui.screens.citas.MisCitasScreen
import com.saludplus.citas.ui.screens.home.HomeScreen
import com.saludplus.citas.ui.screens.perfil.PerfilScreen
import com.saludplus.citas.ui.screens.resultados.ResultadosScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Cambio de pestaña en la barra inferior: no apila pantallas repetidas
    val irA: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.HOME)
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {
        composable(Rutas.SPLASH) {
            SplashScreen(
                onComenzar = { navController.navigate(Rutas.REGISTRO) },
                onYaTengoCuenta = { navController.navigate(Rutas.LOGIN) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                },
                onIrLogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                },
                onTerminos = { /* se conecta con Términos en el commit 21 */ }
            )
        }
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                },
                onIrRegistro = {
                    navController.navigate(Rutas.REGISTRO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onAtras = { navController.popBackStack() }
            )
        }
        composable(Rutas.HOME) {
            HomeScreen(
                onNotificaciones = { /* se conecta con Notificaciones en el commit 20 */ },
                onAgendar = { navController.navigate(Rutas.ESPECIALIDADES) },
                onMisCitas = { irA(Rutas.MIS_CITAS) },
                onMisDatos = { irA(Rutas.PERFIL) },
                onResultados = { irA(Rutas.RESULTADOS) },
                onEspecialidad = { id -> navController.navigate(Rutas.medicos(id)) },
                onVerEspecialidades = { navController.navigate(Rutas.ESPECIALIDADES) },
                onNavegar = irA
            )
        }
        composable(Rutas.ESPECIALIDADES) {
            EspecialidadesScreen(
                onAtras = { navController.popBackStack() },
                onEspecialidad = { id -> navController.navigate(Rutas.medicos(id)) }
            )
        }
        composable(
            route = Rutas.MEDICOS,
            arguments = listOf(navArgument("especialidadId") { type = NavType.IntType })
        ) { entrada ->
            val especialidadId = entrada.arguments?.getInt("especialidadId") ?: 0
            MedicosScreen(
                especialidadId = especialidadId,
                onAtras = { navController.popBackStack() },
                onMedico = { medicoId -> navController.navigate(Rutas.fechaHora(medicoId)) }
            )
        }
        composable(
            route = Rutas.FECHA_HORA,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { entrada ->
            val medicoId = entrada.arguments?.getInt("medicoId") ?: 0
            FechaHoraScreen(
                medicoId = medicoId,
                onAtras = { navController.popBackStack() },
                onContinuar = { fecha, hora ->
                    navController.navigate(Rutas.confirmarCita(medicoId, fecha, hora))
                }
            )
        }
        composable(
            route = Rutas.CONFIRMAR_CITA,
            arguments = listOf(
                navArgument("medicoId") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType }
            )
        ) { entrada ->
            val medicoId = entrada.arguments?.getInt("medicoId") ?: 0
            val fecha = entrada.arguments?.getString("fecha") ?: ""
            val hora = entrada.arguments?.getString("hora") ?: ""
            ConfirmarCitaScreen(
                medicoId = medicoId,
                fecha = fecha,
                hora = hora,
                onAtras = { navController.popBackStack() },
                onConfirmada = {
                    // popUpTo borra Especialidades, Médicos, Fecha y hora y Confirmar del historial
                    navController.navigate(Rutas.CITA_EXITOSA) {
                        popUpTo(Rutas.HOME)
                    }
                }
            )
        }
        composable(Rutas.CITA_EXITOSA) {
            CitaExitosaScreen(
                onVerMisCitas = { irA(Rutas.MIS_CITAS) },
                onIrInicio = { navController.popBackStack(Rutas.HOME, false) }
            )
        }
        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(
                onNavegar = irA,
                onAgendar = { navController.navigate(Rutas.ESPECIALIDADES) },
                onCita = { citaId -> navController.navigate(Rutas.detalleCita(citaId)) }
            )
        }
        composable(
            route = Rutas.DETALLE_CITA,
            arguments = listOf(navArgument("citaId") { type = NavType.IntType })
        ) { entrada ->
            val citaId = entrada.arguments?.getInt("citaId") ?: 0
            DetalleCitaScreen(
                citaId = citaId,
                onAtras = { navController.popBackStack() },
                onCancelada = { navController.popBackStack() }
            )
        }
        composable(Rutas.RESULTADOS) { ResultadosScreen(onNavegar = irA) }
        composable(Rutas.PERFIL) {
            PerfilScreen(
                onNavegar = irA,
                onCerrarSesion = {
                    // popUpTo borra Inicio y las pestañas del historial: Atrás ya no vuelve a la app
                    navController.navigate(Rutas.SPLASH) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}