package com.tecsup.pokemon.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.pokemon.domain.model.Sesion
import com.tecsup.pokemon.ui.components.AparecerSuave
import com.tecsup.pokemon.ui.components.BotonMadera
import com.tecsup.pokemon.ui.components.CampoMadera
import com.tecsup.pokemon.ui.components.CartelTitulo
import com.tecsup.pokemon.ui.components.EnlaceMadera
import com.tecsup.pokemon.ui.components.ErrorTemporal
import com.tecsup.pokemon.ui.components.FondoPokedex
import com.tecsup.pokemon.ui.components.TablaSubtitulo

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginExitoso: (Sesion) -> Unit,
    onIrARegistro: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Cuando hay sesión, se avisa a la navegación
    LaunchedEffect(estado.sesion) {
        estado.sesion?.let(onLoginExitoso)
    }

    FondoPokedex {
        // Mide el alto disponible para repartir los bloques arriba y abajo
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)   // ocupa toda la pantalla, pero permite scroll si no entra
                    .padding(top = 72.dp, bottom = 120.dp)
            ) {
                // ----- Bloque superior: cartel, subtítulo y campos -----
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    AparecerSuave(retrasoMs = 0) {
                        CartelTitulo()
                    }

                    // La imagen del cartel tiene espacio transparente abajo: se sube el subtítulo
                    AparecerSuave(retrasoMs = 150) {
                        Box(modifier = Modifier.offset(y = (-66).dp)) {
                            TablaSubtitulo("Inicia sesión para continuar")
                        }
                    }

                    // Campos en el pasto libre, sin chocar con Snorlax
                    Spacer(Modifier.height(70.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 88.dp)
                    ) {
                        AparecerSuave(retrasoMs = 300) {
                            CampoMadera(
                                valor = estado.correo,
                                onValorChange = viewModel::onCorreoChange,
                                placeholder = "Correo electrónico",
                                icono = Icons.Outlined.Email,
                                tipoTeclado = KeyboardType.Email,
                                habilitado = !estado.cargando
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        AparecerSuave(retrasoMs = 420) {
                            CampoMadera(
                                valor = estado.clave,
                                onValorChange = viewModel::onClaveChange,
                                placeholder = "Contraseña",
                                icono = Icons.Outlined.Lock,
                                esClave = true,
                                habilitado = !estado.cargando
                            )
                        }

                        // Error que aparece y se oculta solo con animación (2 segundos)
                        ErrorTemporal(
                            mensaje = estado.error,
                            onOcultar = viewModel::limpiarError
                        )
                    }
                }

                // ----- Bloque inferior: botón y enlace -----
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    AparecerSuave(retrasoMs = 560) {
                        Box(modifier = Modifier.padding(horizontal = 88.dp)) {
                            BotonMadera(
                                texto = "Ingresar",
                                onClick = viewModel::iniciarSesion,
                                cargando = estado.cargando
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // El enlace es más ancho que el botón para que el texto entre completo
                    AparecerSuave(retrasoMs = 680) {
                        Box(modifier = Modifier.padding(horizontal = 60.dp)) {
                            EnlaceMadera(
                                pregunta = "¿No tienes cuenta?",
                                accion = "Regístrate",
                                onClick = onIrARegistro
                            )
                        }
                    }
                }
            }
        }
    }
}