package com.tecsup.huellitasvet.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.huellitasvet.VetApp
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.components.VetButton
import com.tecsup.huellitasvet.ui.dueno.*
import com.tecsup.huellitasvet.ui.theme.VetTheme
import com.tecsup.huellitasvet.ui.veterinario.ServiciosScreen
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

    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    // La ficha pertenece a la pestaña Mascotas
    val rutaBarra = if (rutaActual == Rutas.FICHA_MASCOTA) Rutas.MASCOTAS else rutaActual
    val mostrarBarra = itemsDueno.any { it.ruta == rutaBarra }

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
    val irAPestana: (String) -> Unit = { ruta ->
        nav.navigate(ruta) {
            popUpTo(Rutas.INICIO) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    VetTheme(rol = rolActivo ?: Rol.CLIENTE, oscuro = oscuro) {
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                if (mostrarBarra) BarraInferior(itemsDueno, rutaBarra, irAPestana)
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

                // ===== Dueño =====
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
                        onEliminada = {
                            nav.navigate(Rutas.MASCOTAS) { popUpTo(Rutas.INICIO) }
                        },
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
                        onPedirCita = { nav.navigate(Rutas.pedirCita(it)) },
                        onEditar = { nav.navigate(Rutas.formMascota(it)) },
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
                            irAPestana(Rutas.MIS_CITAS)      // vuelve a Mis citas
                        },
                        onAtras = { nav.popBackStack() }
                    )
                }
                // TODO Paso 10: reemplazar por MisCitasScreen
                composable(Rutas.MIS_CITAS) {
                    PantallaTemporal("Mis citas", usuario?.nombre, cerrarSesion)
                }

                // TODO Paso 13: reemplazar por PerfilScreen
                composable(Rutas.PERFIL) {
                    PantallaTemporal("Perfil", usuario?.nombre, cerrarSesion)
                }

                // ===== Veterinario =====
                // TODO Paso 11: reemplazar por PanelScreen
                composable(Rutas.PANEL) {
                    PantallaTemporal("Panel del veterinario", usuario?.nombre, cerrarSesion) {
                        nav.navigate(Rutas.SERVICIOS)
                    }
                }

                composable(Rutas.SERVICIOS) {
                    val vm: ServiciosViewModel = viewModel(factory = factory)
                    ServiciosScreen(
                        vm = vm,
                        onEditar = { /* TODO Paso 13 */ },
                        onAtras = { nav.popBackStack() }
                    )
                }
            }
        }
    }
}

/** Pantalla provisional (se reemplaza en los siguientes pasos) */
@Composable
private fun PantallaTemporal(
    titulo: String,
    nombre: String?,
    onCerrarSesion: () -> Unit,
    onServicios: (() -> Unit)? = null
) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        Text("Hola, ${nombre ?: ""}", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        if (onServicios != null) {
            VetButton("Ver servicios", onServicios)
            Spacer(Modifier.height(12.dp))
        }
        OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
    }
}