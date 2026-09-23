package com.millones.clinicasaludplus.navigation

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.millones.clinicasaludplus.screens.InicioScreen

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

                        Text("Clínica Salud+")

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
                                    navController.navigate(Screen.MisCitas.route)
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
                                    navController.navigate(Screen.HistorialMedico.route)
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
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }

        composable(Screen.PerfilMedico.route) {
            Text("Perfil del médico")
        }

        composable(Screen.AgendarCita.route) {
            Text("Agendar cita")
        }

        composable(Screen.Confirmacion.route) {
            Text("Confirmación")
        }

        composable(Screen.MisCitas.route) {
            Text("Mis citas")
        }

        composable(Screen.HistorialMedico.route) {
            Text("Historial médico")
        }
    }
}