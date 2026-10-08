package com.saludplus.citas.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.MensajeHost
import com.saludplus.citas.ui.screens.agendamiento.CitaExitosaScreen
import com.saludplus.citas.ui.screens.agendamiento.ConfirmarCitaScreen
import com.saludplus.citas.ui.screens.agendamiento.EspecialidadesScreen
import com.saludplus.citas.ui.screens.agendamiento.FechaHoraScreen
import com.saludplus.citas.ui.screens.agendamiento.MedicosScreen
import com.saludplus.citas.ui.screens.auth.LoginScreen
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen
import com.saludplus.citas.ui.screens.auth.TerminosScreen
import com.saludplus.citas.ui.screens.citas.DetalleCitaScreen
import com.saludplus.citas.ui.screens.citas.MisCitasScreen
import com.saludplus.citas.ui.screens.home.HomeScreen
import com.saludplus.citas.ui.screens.notificaciones.NotificacionesScreen
import com.saludplus.citas.ui.screens.perfil.PerfilScreen
import com.saludplus.citas.ui.screens.resultados.ResultadoDetalleScreen
import com.saludplus.citas.ui.screens.resultados.ResultadosScreen
import androidx.compose.runtime.LaunchedEffect
import com.saludplus.citas.ui.theme.Degradados
import com.saludplus.citas.ui.theme.Duraciones
import com.saludplus.citas.ui.theme.RojoError
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(citaIdInicial: Int? = null) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Navegar directamente al detalle si la app fue abierta desde una notificación
    LaunchedEffect(citaIdInicial) {
        if (citaIdInicial != null && citaIdInicial != -1) {
            if (Repositorio.usuarioActual != null) {
                navController.navigate(Rutas.detalleCita(citaIdInicial)) {
                    launchSingleTop = true
                }
            }
        }
    }

    // Manejar el botón Atrás cuando el drawer está abierto
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    // Rutas de la barra inferior (pestañas principales)
    val rutasPestanias = listOf(Rutas.HOME, Rutas.MIS_CITAS, Rutas.RESULTADOS, Rutas.PERFIL)

    val irA: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.HOME)
            launchSingleTop = true
        }
    }

    val abrirMenu = {
        scope.launch { drawerState.open() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || Repositorio.usuarioActual != null,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                val usuario = Repositorio.usuarioActual
                val nombre = usuario?.nombre ?: "Paciente SaludPlus"
                val correo = usuario?.correo ?: ""
                val inicial = nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "S"

                // Cabecera del Drawer con degradado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Degradados.Principal)
                        .padding(20.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = inicial,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = correo,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                val itemClick: (() -> Unit) -> Unit = { accion ->
                    scope.launch { drawerState.close() }
                    accion()
                }

                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    selected = false,
                    onClick = { itemClick { irA(Rutas.HOME) } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Agendar cita") },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    selected = false,
                    onClick = { itemClick { navController.navigate(Rutas.ESPECIALIDADES) } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Mis citas") },
                    icon = { Icon(Icons.Default.EventAvailable, contentDescription = null) },
                    selected = false,
                    onClick = { itemClick { irA(Rutas.MIS_CITAS) } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Resultados") },
                    icon = { Icon(Icons.Default.Description, contentDescription = null) },
                    selected = false,
                    onClick = { itemClick { irA(Rutas.RESULTADOS) } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Notificaciones") },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    selected = false,
                    onClick = { itemClick { navController.navigate(Rutas.NOTIFICACIONES) } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Mi perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    selected = false,
                    onClick = { itemClick { irA(Rutas.PERFIL) } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Términos y condiciones") },
                    icon = { Icon(Icons.Default.Gavel, contentDescription = null) },
                    selected = false,
                    onClick = { itemClick { navController.navigate(Rutas.TERMINOS) } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                Spacer(Modifier.weight(1f))

                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = RojoError) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = RojoError) },
                    selected = false,
                    onClick = {
                        itemClick {
                            Repositorio.cerrarSesion()
                            navController.navigate(Rutas.SPLASH) {
                                popUpTo(Rutas.HOME) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Rutas.SPLASH,
                enterTransition = {
                    val esPestania = initialState.destination.route in rutasPestanias &&
                            targetState.destination.route in rutasPestanias
                    if (esPestania) {
                        fadeIn(animationSpec = tween(Duraciones.media))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(Duraciones.media)
                        ) + fadeIn(animationSpec = tween(Duraciones.media))
                    }
                },
                exitTransition = {
                    val esPestania = initialState.destination.route in rutasPestanias &&
                            targetState.destination.route in rutasPestanias
                    if (esPestania) {
                        fadeOut(animationSpec = tween(Duraciones.media))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 },
                            animationSpec = tween(Duraciones.media)
                        ) + fadeOut(animationSpec = tween(Duraciones.media))
                    }
                },
                popEnterTransition = {
                    val esPestania = initialState.destination.route in rutasPestanias &&
                            targetState.destination.route in rutasPestanias
                    if (esPestania) {
                        fadeIn(animationSpec = tween(Duraciones.media))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { -it / 3 },
                            animationSpec = tween(Duraciones.media)
                        ) + fadeIn(animationSpec = tween(Duraciones.media))
                    }
                },
                popExitTransition = {
                    val esPestania = initialState.destination.route in rutasPestanias &&
                            targetState.destination.route in rutasPestanias
                    if (esPestania) {
                        fadeOut(animationSpec = tween(Duraciones.media))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(Duraciones.media)
                        ) + fadeOut(animationSpec = tween(Duraciones.media))
                    }
                }
            ) {
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
                        onTerminos = { navController.navigate(Rutas.TERMINOS) }
                    )
                }
                composable(Rutas.TERMINOS) {
                    TerminosScreen(onAtras = { navController.popBackStack() })
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
                        onNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) },
                        onAgendar = { navController.navigate(Rutas.ESPECIALIDADES) },
                        onMisCitas = { irA(Rutas.MIS_CITAS) },
                        onMisDatos = { irA(Rutas.PERFIL) },
                        onResultados = { irA(Rutas.RESULTADOS) },
                        onEspecialidad = { id -> navController.navigate(Rutas.medicos(id)) },
                        onVerEspecialidades = { navController.navigate(Rutas.ESPECIALIDADES) },
                        onNavegar = irA,
                        onMenu = { abrirMenu() },
                        onCita = { citaId -> navController.navigate(Rutas.detalleCita(citaId)) }
                    )
                }
                composable(Rutas.NOTIFICACIONES) {
                    NotificacionesScreen(
                        onAtras = { navController.popBackStack() },
                        onCita = { citaId -> navController.navigate(Rutas.detalleCita(citaId)) }
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
                composable(Rutas.RESULTADOS) {
                    ResultadosScreen(
                        onNavegar = irA,
                        onResultado = { id -> navController.navigate(Rutas.resultadoDetalle(id)) }
                    )
                }
                composable(
                    route = Rutas.RESULTADO_DETALLE,
                    arguments = listOf(navArgument("resultadoId") { type = NavType.IntType })
                ) { entrada ->
                    val resultadoId = entrada.arguments?.getInt("resultadoId") ?: 0
                    ResultadoDetalleScreen(
                        resultadoId = resultadoId,
                        onAtras = { navController.popBackStack() }
                    )
                }
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

            MensajeHost(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .zIndex(99f)
            )
        }
    }
}
