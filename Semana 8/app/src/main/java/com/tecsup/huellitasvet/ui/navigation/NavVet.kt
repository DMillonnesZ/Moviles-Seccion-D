package com.tecsup.huellitasvet.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.huellitasvet.VetApp
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.dueno.*
import com.tecsup.huellitasvet.ui.theme.VetTheme
import com.tecsup.huellitasvet.ui.veterinario.*
import com.tecsup.huellitasvet.viewmodel.*

@Composable
fun NavVet() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as VetApp
    val factory = remember { VetViewModelFactory(app.repository, app.prefs) }

    // Sesión compartida por toda la app
    val sesionVm: SesionViewModel = viewModel(factory = factory)
    val rolActivo by sesionVm.rolActivo.collectAsState()
    val oscuro by sesionVm.modoOscuro.collectAsState()
    val usuario by sesionVm.usuarioActual.collectAsState()

    val esVet = rolActivo == Rol.ADMIN
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    // Barra inferior según el rol (4 ítems dueño · 5 ítems veterinario)
    val itemsBarra = if (esVet) itemsVet else itemsDueno
    val rutaBarra = when (rutaActual) {
        Rutas.FICHA_MASCOTA -> if (esVet) Rutas.PACIENTES else Rutas.MASCOTAS
        else -> rutaActual
    }
    val mostrarBarra = itemsBarra.any { it.ruta == rutaBarra }

    /** Navega a la pantalla principal del rol y limpia la pila */
    val irAPrincipal: (Rol) -> Unit = { rol ->
        nav.navigate(if (rol == Rol.CLIENTE) Rutas.INICIO else Rutas.PANEL) {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }
    val cerrarSesion: () -> Unit = {
        sesionVm.cerrarSesion {
            nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
        }
    }
    val cambiarPerfil: () -> Unit = { sesionVm.cambiarPerfil(irAPrincipal) }
    val irAPestana: (String) -> Unit = { ruta ->
        nav.navigate(ruta) {
            popUpTo(if (esVet) Rutas.PANEL else Rutas.INICIO) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    VetTheme(rol = rolActivo ?: Rol.CLIENTE, oscuro = oscuro) {
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                if (mostrarBarra) BarraInferior(itemsBarra, rutaBarra, irAPestana)
            }
        ) { inner ->
            NavHost(
                navController = nav,
                startDestination = Rutas.SPLASH,
                modifier = Modifier.padding(inner)
            ) {

                composable(Rutas.SPLASH) {
                    LaunchedEffect(Unit) {
                        sesionVm.restaurarSesion()?.let(irAPrincipal)
                    }
                    SplashScreen(
                        onComenzar = { nav.navigate(Rutas.LOGIN) },
                        onYaTengoCuenta = { nav.navigate(Rutas.LOGIN) }
                    )
                }

                composable(Rutas.LOGIN) {
                    LoginScreen(vm = sesionVm, onIngresar = irAPrincipal)
                }

                // ================= DUEÑO =================
                composable(Rutas.INICIO) {
                    val vm: InicioViewModel = viewModel(factory = factory)
                    LaunchedEffect(usuario?.id) { usuario?.let { vm.cargar(it.id) } }
                    InicioScreen(
                        vm = vm,
                        nombreDueno = usuario?.nombre ?: "",
                        onVerMascota = { nav.navigate(Rutas.fichaMascota(it)) },
                        onAgregarMascota = { nav.navigate(Rutas.formMascota()) },
                        onVerTodas = { irAPestana(Rutas.MASCOTAS) },
                        onPedirCita = { nav.navigate(Rutas.pedirCita()) }
                    )
                }

                composable(Rutas.MASCOTAS) {
                    val vm: InicioViewModel = viewModel(factory = factory)
                    LaunchedEffect(usuario?.id) { usuario?.let { vm.cargar(it.id) } }
                    MascotasScreen(
                        vm = vm,
                        onVerMascota = { nav.navigate(Rutas.fichaMascota(it)) },
                        onAgregar = { nav.navigate(Rutas.formMascota()) }
                    )
                }

                composable(
                    Rutas.FORM_MASCOTA,
                    arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                ) { back ->
                    val id = back.arguments?.getInt("id")?.takeIf { it > 0 }
                    val vm: MascotaViewModel = viewModel(factory = factory)
                    FormularioMascotaScreen(
                        vm = vm,
                        duenoId = usuario?.id ?: 0,
                        mascotaId = id,
                        onGuardado = { nav.popBackStack() },
                        onEliminada = { nav.navigate(Rutas.MASCOTAS) { popUpTo(Rutas.INICIO) } },
                        onCancelar = { nav.popBackStack() }
                    )
                }

                composable(
                    Rutas.FICHA_MASCOTA,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { back ->
                    val id = back.arguments?.getInt("id") ?: return@composable
                    val vm: MascotaViewModel = viewModel(factory = factory)
                    FichaMascotaScreen(
                        vm = vm,
                        mascotaId = id,
                        onPedirCita = if (esVet) null else { mid -> nav.navigate(Rutas.pedirCita(mid)) },
                        onEditar = if (esVet) null else { mid -> nav.navigate(Rutas.formMascota(mid)) },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(
                    Rutas.PEDIR_CITA,
                    arguments = listOf(navArgument("mascotaId") { type = NavType.IntType; defaultValue = -1 })
                ) { back ->
                    val mascotaId = back.arguments?.getInt("mascotaId")?.takeIf { it > 0 }
                    val vm: PedirCitaViewModel = viewModel(factory = factory)
                    LaunchedEffect(usuario?.id) { usuario?.let { vm.cargar(it.id, mascotaId) } }
                    PedirCitaScreen(
                        vm = vm,
                        onConfirmada = {
                            nav.popBackStack()
                            irAPestana(Rutas.MIS_CITAS)
                        },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(Rutas.MIS_CITAS) {
                    val vm: MisCitasViewModel = viewModel(factory = factory)
                    LaunchedEffect(usuario?.id) { usuario?.let { vm.cargar(it.id) } }
                    MisCitasScreen(
                        vm = vm,
                        onNuevaCita = { nav.navigate(Rutas.pedirCita()) },
                        onReprogramar = { nav.navigate(Rutas.reprogramarCita(it)) },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(
                    Rutas.REPROGRAMAR_CITA,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { back ->
                    val id = back.arguments?.getInt("id") ?: return@composable
                    val vm: ReprogramarCitaViewModel = viewModel(factory = factory)
                    ReprogramarCitaScreen(
                        vm = vm,
                        citaId = id,
                        onConfirmado = { nav.popBackStack() },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(Rutas.PERFIL) {
                    val inicioVm: InicioViewModel = viewModel(factory = factory)
                    val citasVm: MisCitasViewModel = viewModel(factory = factory)
                    LaunchedEffect(usuario?.id) {
                        usuario?.let { inicioVm.cargar(it.id); citasVm.cargar(it.id) }
                    }
                    PerfilScreen(
                        sesionVm = sesionVm,
                        inicioVm = inicioVm,
                        citasVm = citasVm,
                        onCambiarPerfil = cambiarPerfil,
                        onCerrarSesion = cerrarSesion,
                        onAtras = { nav.popBackStack() }
                    )
                }

                // ================= VETERINARIO =================
                composable(Rutas.PANEL) {
                    val vm: PanelViewModel = viewModel(factory = factory)
                    PanelScreen(vm = vm, onVerPendientes = { irAPestana(Rutas.AGENDA) })
                }

                composable(Rutas.AGENDA) {
                    val vm: AgendaViewModel = viewModel(factory = factory)
                    AgendaScreen(
                        vm = vm,
                        onAtender = { nav.navigate(Rutas.atenderCita(it)) },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(
                    Rutas.ATENDER_CITA,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { back ->
                    val id = back.arguments?.getInt("id") ?: return@composable
                    val vm: AtenderCitaViewModel = viewModel(factory = factory)
                    LaunchedEffect(id) { vm.cargar(id) }
                    AtenderCitaScreen(
                        vm = vm,
                        onListo = { nav.popBackStack() },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(Rutas.PACIENTES) {
                    val vm: PacientesViewModel = viewModel(factory = factory)
                    PacientesScreen(
                        vm = vm,
                        onVerPaciente = { nav.navigate(Rutas.fichaMascota(it)) },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(Rutas.SERVICIOS) {
                    val vm: ServiciosViewModel = viewModel(factory = factory)
                    ServiciosScreen(
                        vm = vm,
                        onEditar = { nav.navigate(Rutas.formServicio(it)) },
                        onNuevo = { nav.navigate(Rutas.formServicio()) },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(
                    Rutas.FORM_SERVICIO,
                    arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                ) { back ->
                    val id = back.arguments?.getInt("id")?.takeIf { it > 0 }
                    val vm: ServiciosViewModel = viewModel(factory = factory)
                    FormularioServicioScreen(
                        vm = vm,
                        servicioId = id,
                        onGuardado = { nav.popBackStack() },
                        onCancelar = { nav.popBackStack() }
                    )
                }

                composable(Rutas.AJUSTES) {
                    AjustesScreen(
                        sesionVm = sesionVm,
                        onCambiarPerfil = cambiarPerfil,
                        onCerrarSesion = cerrarSesion,
                        onAtras = { nav.popBackStack() }
                    )
                }
            }
        }
    }
}