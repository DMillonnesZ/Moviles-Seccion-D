package com.millones.clinicasaludplus.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.millones.clinicasaludplus.data.CitasRepository
import com.millones.clinicasaludplus.screens.AgendarCitaScreen
import com.millones.clinicasaludplus.screens.ConfirmacionScreen
import com.millones.clinicasaludplus.screens.HistorialMedicoScreen
import com.millones.clinicasaludplus.screens.InicioScreen
import com.millones.clinicasaludplus.screens.MisCitasScreen
import com.millones.clinicasaludplus.screens.PerfilMedicoScreen
import com.millones.clinicasaludplus.screens.PerfilScreen
import com.millones.clinicasaludplus.ui.theme.PurpleContainer
import com.millones.clinicasaludplus.ui.theme.PurpleLight
import com.millones.clinicasaludplus.ui.theme.PurplePrimary
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 24.dp)
                ) {
                    // Header: User avatar + info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "JP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = PurplePrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Juan Pérez",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF222222)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Paciente",
                                fontSize = 14.sp,
                                color = Color(0xFF777777)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = 1.dp,
                        color = Color(0xFFEEEEEE)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Items
                    DrawerMenuItem(
                        label = "Inicio",
                        isSelected = currentRoute == Screen.Inicio.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Inicio.route) {
                                popUpTo(Screen.Inicio.route) { inclusive = true }
                            }
                        }
                    )

                    DrawerMenuItem(
                        label = "Mis citas",
                        isSelected = currentRoute == Screen.MisCitas.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.MisCitas.route) {
                                navController.navigate(Screen.MisCitas.route)
                            }
                        }
                    )

                    DrawerMenuItem(
                        label = "Historial médico",
                        isSelected = currentRoute == Screen.HistorialMedico.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.HistorialMedico.route) {
                                navController.navigate(Screen.HistorialMedico.route)
                            }
                        }
                    )

                    DrawerMenuItem(
                        label = "Perfil",
                        isSelected = currentRoute == Screen.Perfil.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.Perfil.route) {
                                navController.navigate(Screen.Perfil.route)
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Inicio.route
        ) {
            composable(Screen.Inicio.route) {
                InicioScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onMedicoClick = { medico ->
                        val nombre = Uri.encode(medico.nombre)
                        val especialidad = Uri.encode(medico.especialidad)
                        val calificacion = medico.calificacion.toString()

                        navController.navigate(
                            "perfil_medico/$nombre/$especialidad/$calificacion"
                        )
                    }
                )
            }

            composable(
                route = Screen.PerfilMedico.route,
                arguments = listOf(
                    navArgument("nombre") { type = NavType.StringType },
                    navArgument("especialidad") { type = NavType.StringType },
                    navArgument("calificacion") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
                val especialidad = Uri.decode(backStackEntry.arguments?.getString("especialidad") ?: "")
                val calificacion = backStackEntry.arguments?.getString("calificacion") ?: ""

                PerfilMedicoScreen(
                    nombre = nombre,
                    especialidad = especialidad,
                    calificacion = calificacion,
                    onBackClick = { navController.popBackStack() },
                    onAgendarClick = {
                        val nombreEncoded = Uri.encode(nombre)
                        val especialidadEncoded = Uri.encode(especialidad)

                        navController.navigate("agendar_cita/$nombreEncoded/$especialidadEncoded")
                    }
                )
            }

            composable(
                route = Screen.AgendarCita.route,
                arguments = listOf(
                    navArgument("nombre") { type = NavType.StringType },
                    navArgument("especialidad") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
                val especialidad = Uri.decode(backStackEntry.arguments?.getString("especialidad") ?: "")

                AgendarCitaScreen(
                    nombre = nombre,
                    especialidad = especialidad,
                    onBackClick = { navController.popBackStack() },
                    onConfirmarClick = { fecha, horario ->
                        val fechaHoraStr = "$fecha, $horario"
                        CitasRepository.agregarCita(
                            medicoNombre = if (nombre.isNotEmpty()) nombre else "Dra. Ana Torres",
                            fechaHora = fechaHoraStr
                        )

                        val nombreEncoded = Uri.encode(nombre)
                        val especialidadEncoded = Uri.encode(especialidad)
                        val fechaEncoded = Uri.encode(fecha)
                        val horarioEncoded = Uri.encode(horario)

                        navController.navigate("confirmacion/$nombreEncoded/$especialidadEncoded/$fechaEncoded/$horarioEncoded")
                    }
                )
            }

            composable(
                route = Screen.Confirmacion.route,
                arguments = listOf(
                    navArgument("nombre") { type = NavType.StringType },
                    navArgument("especialidad") { type = NavType.StringType },
                    navArgument("fecha") { type = NavType.StringType },
                    navArgument("horario") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
                val especialidad = Uri.decode(backStackEntry.arguments?.getString("especialidad") ?: "")
                val fecha = Uri.decode(backStackEntry.arguments?.getString("fecha") ?: "")
                val horario = Uri.decode(backStackEntry.arguments?.getString("horario") ?: "")

                ConfirmacionScreen(
                    nombre = nombre,
                    especialidad = especialidad,
                    fecha = fecha,
                    horario = horario,
                    onFinalizarClick = {
                        navController.navigate(Screen.MisCitas.route) {
                            popUpTo(Screen.Inicio.route)
                        }
                    }
                )
            }

            composable(Screen.MisCitas.route) {
                MisCitasScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }

            composable(Screen.HistorialMedico.route) {
                HistorialMedicoScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }

            composable(Screen.Perfil.route) {
                PerfilScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PurpleContainer else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = null,
                tint = if (isSelected) PurplePrimary else Color(0xFF444444),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PurplePrimary else Color(0xFF333333)
            )
        }
    }
}
