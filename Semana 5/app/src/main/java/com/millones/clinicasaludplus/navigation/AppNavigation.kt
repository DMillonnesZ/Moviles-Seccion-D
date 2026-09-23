package com.millones.clinicasaludplus.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.millones.clinicasaludplus.screens.AgendarCitaScreen
import com.millones.clinicasaludplus.screens.ConfirmacionScreen
import com.millones.clinicasaludplus.screens.InicioScreen
import com.millones.clinicasaludplus.screens.PerfilMedicoScreen
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Inicio.route
    ) {

        composable(Screen.Inicio.route) {

            val drawerState = rememberDrawerState(
                initialValue = DrawerValue.Closed
            )

            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {

                        Text(
                            text = "Clínica Salud+",
                            modifier = Modifier.padding(16.dp)
                        )

                        NavigationDrawerItem(
                            label = {
                                Text("Inicio")
                            },
                            selected = true,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )

                        NavigationDrawerItem(
                            label = {
                                Text("Mis citas")
                            },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(
                                        Screen.MisCitas.route
                                    )
                                }
                            }
                        )

                        NavigationDrawerItem(
                            label = {
                                Text("Historial médico")
                            },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(
                                        Screen.HistorialMedico.route
                                    )
                                }
                            }
                        )
                    }
                }
            ) {

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Clínica Salud+")
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Abrir menú"
                                    )
                                }
                            }
                        )
                    }
                ) { paddingValues ->

                    InicioScreen(
                        modifier = Modifier.padding(paddingValues),
                        onMedicoClick = { medico ->

                            val nombre = Uri.encode(medico.nombre)
                            val especialidad =
                                Uri.encode(medico.especialidad)

                            navController.navigate(
                                "perfil_medico/$nombre/$especialidad/${medico.calificacion}"
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.PerfilMedico.route,
            arguments = listOf(
                navArgument("nombre") {
                    type = NavType.StringType
                },
                navArgument("especialidad") {
                    type = NavType.StringType
                },
                navArgument("calificacion") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val nombre = Uri.decode(
                backStackEntry.arguments?.getString("nombre") ?: ""
            )

            val especialidad = Uri.decode(
                backStackEntry.arguments?.getString("especialidad") ?: ""
            )

            val calificacion =
                backStackEntry.arguments?.getString("calificacion") ?: ""

            PerfilMedicoScreen(
                nombre = nombre,
                especialidad = especialidad,
                calificacion = calificacion,
                onAgendarClick = {

                    val nombreEncoded = Uri.encode(nombre)
                    val especialidadEncoded =
                        Uri.encode(especialidad)

                    navController.navigate(
                        "agendar_cita/$nombreEncoded/$especialidadEncoded"
                    )
                }
            )
        }

        composable(
            route = Screen.AgendarCita.route,
            arguments = listOf(
                navArgument("nombre") {
                    type = NavType.StringType
                },
                navArgument("especialidad") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val nombre = Uri.decode(
                backStackEntry.arguments?.getString("nombre") ?: ""
            )

            val especialidad = Uri.decode(
                backStackEntry.arguments?.getString("especialidad") ?: ""
            )

            AgendarCitaScreen(
                nombre = nombre,
                especialidad = especialidad,
                onConfirmarClick = { fecha, horario ->

                    val nombreEncoded = Uri.encode(nombre)
                    val especialidadEncoded =
                        Uri.encode(especialidad)
                    val fechaEncoded = Uri.encode(fecha)
                    val horarioEncoded = Uri.encode(horario)

                    navController.navigate(
                        "confirmacion/" +
                                "$nombreEncoded/" +
                                "$especialidadEncoded/" +
                                "$fechaEncoded/" +
                                horarioEncoded
                    )
                }
            )
        }

        composable(
            route = Screen.Confirmacion.route,
            arguments = listOf(
                navArgument("nombre") {
                    type = NavType.StringType
                },
                navArgument("especialidad") {
                    type = NavType.StringType
                },
                navArgument("fecha") {
                    type = NavType.StringType
                },
                navArgument("horario") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val nombre = Uri.decode(
                backStackEntry.arguments?.getString("nombre") ?: ""
            )

            val especialidad = Uri.decode(
                backStackEntry.arguments?.getString("especialidad") ?: ""
            )

            val fecha = Uri.decode(
                backStackEntry.arguments?.getString("fecha") ?: ""
            )

            val horario = Uri.decode(
                backStackEntry.arguments?.getString("horario") ?: ""
            )

            ConfirmacionScreen(
                nombre = nombre,
                especialidad = especialidad,
                fecha = fecha,
                horario = horario,
                onFinalizarClick = {
                    navController.navigate(
                        Screen.Inicio.route
                    ) {
                        popUpTo(Screen.Inicio.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.MisCitas.route) {
            Text(
                text = "Mis citas"
            )
        }

        composable(Screen.HistorialMedico.route) {
            Text(
                text = "Historial médico"
            )
        }
    }
}