package com.millones.campusgps.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.campusgps.ui.componentes.AvisoTemporal
import com.millones.campusgps.ui.componentes.BotonGoogle
import com.millones.campusgps.ui.componentes.SeparadorO
import com.millones.campusgps.ui.componentes.rememberAccesoGoogle
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.CampoCampus
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.EncabezadoTarjeta
import com.millones.campusgps.ui.componentes.EnlaceCampus
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FondoAuth
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LogoCampus
import com.millones.campusgps.ui.componentes.TarjetaAuth
import com.millones.campusgps.ui.componentes.sacudida
import com.millones.campusgps.viewmodel.LoginViewModel

// Login: nombre de la app, tarjeta blanca con el formulario y enlace transparente abajo.
// Cada pieza entra con su retraso (como en la PokeDex). El Scaffold deja libre la cámara.
@Composable
fun PantallaLogin(
    vm: LoginViewModel,
    onLoginExitoso: () -> Unit,
    onRegistro: () -> Unit,
    onOlvido: () -> Unit
) {
    val estado by vm.estado.collectAsState()

    // Ingreso correcto (correo o Google): la navegación lleva al mapa con la cortina de cuadros
    LaunchedEffect(estado.exito) {
        if (estado.exito) onLoginExitoso()
    }

    // Abre la hoja de cuentas de Google; el token va al ViewModel
    val entrarConGoogle = rememberAccesoGoogle(
        onToken = vm::entrarConGoogle,
        onError = vm::errorGoogle
    )
    val ocupado = estado.cargando || estado.cargandoGoogle

    FondoAuth {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = InsetsCampus
        ) { espacio ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(espacio)
                    .consumeWindowInsets(espacio)
                    .imePadding()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight)   // centrado; hace scroll si no entra
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    EntradaSuave(retrasoMs = 0L) {
                        LogoCampus()
                    }

                    EntradaSuave(retrasoMs = 150L) {
                        // Se sacude y vibra cuando hay un error
                        TarjetaAuth(modifier = Modifier.sacudida(estado.error)) {
                            EncabezadoTarjeta(
                                titulo = "Bienvenido de nuevo",
                                descripcion = "Inicia sesión para acceder a tus ubicaciones y rutas."
                            )

                            EntradaSuave(retrasoMs = 300L) {
                                CampoCampus(
                                    valor = estado.correo,
                                    onValorChange = vm::onCorreoChange,
                                    placeholder = "Correo institucional",
                                    icono = Icons.Rounded.Email,
                                    tipoTeclado = KeyboardType.Email,
                                    habilitado = !estado.cargando,
                                    valido = estado.correoValido
                                )
                            }

                            EntradaSuave(retrasoMs = 400L) {
                                CampoCampus(
                                    valor = estado.clave,
                                    onValorChange = vm::onClaveChange,
                                    placeholder = "Contraseña",
                                    icono = Icons.Rounded.Lock,
                                    esClave = true,
                                    habilitado = !estado.cargando
                                )
                            }

                            // Aviso que aparece y se oculta solo
                            AvisoTemporal(
                                mensaje = estado.error,
                                onOcultar = vm::limpiarError
                            )

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "¿Olvidaste tu contraseña?",
                                    color = CampusColores.AzulPrimario,
                                    fontFamily = FuenteCampus,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .clickable(onClick = onOlvido)
                                        .padding(4.dp)
                                )
                            }

                            EntradaSuave(retrasoMs = 500L) {
                                BotonCampus(
                                    texto = "Iniciar sesión",
                                    onClick = vm::iniciarSesion,
                                    cargando = estado.cargando
                                )
                            }

                            EntradaSuave(retrasoMs = 560L) {
                                SeparadorO()
                            }

                            EntradaSuave(retrasoMs = 600L) {
                                BotonGoogle(
                                    onClick = entrarConGoogle,
                                    cargando = estado.cargandoGoogle,
                                    habilitado = !ocupado
                                )
                            }
                        }
                    }

                    EntradaSuave(retrasoMs = 650L) {
                        EnlaceCampus(
                            pregunta = "¿No tienes cuenta?",
                            accion = "Regístrate",
                            onClick = onRegistro
                        )
                    }
                }
            }
        }

    }
}
