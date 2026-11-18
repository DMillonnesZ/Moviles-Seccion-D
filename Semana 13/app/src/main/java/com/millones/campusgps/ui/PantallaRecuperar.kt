package com.millones.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.millones.campusgps.ui.componentes.AvisoTemporal
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.CampoCampus
import com.millones.campusgps.ui.componentes.EncabezadoTarjeta
import com.millones.campusgps.ui.componentes.EnlaceCampus
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FondoAuth
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LogoCampus
import com.millones.campusgps.ui.componentes.TarjetaAuth
import com.millones.campusgps.ui.componentes.sacudida
import com.millones.campusgps.viewmodel.RecuperarViewModel

// Recuperar contraseña con la misma tarjeta del login
@Composable
fun PantallaOlvidoClave(
    vm: RecuperarViewModel,
    onIrLogin: () -> Unit
) {
    val estado by vm.estado.collectAsState()

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
                        .heightIn(min = maxHeight)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    EntradaSuave(retrasoMs = 0L) {
                        LogoCampus()
                    }

                    EntradaSuave(retrasoMs = 150L) {
                        // Se sacude y vibra cuando hay un error
                        TarjetaAuth(modifier = Modifier.sacudida(estado.error)) {
                            EncabezadoTarjeta(
                                titulo = "Recupera tu acceso",
                                descripcion = "Escribe tu correo y te enviaremos un enlace para crear una contraseña nueva."
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

                            AvisoTemporal(
                                mensaje = estado.error,
                                onOcultar = vm::limpiarError
                            )
                            // El aviso de éxito dura más para que se alcance a leer
                            AvisoTemporal(
                                mensaje = estado.mensaje,
                                onOcultar = vm::limpiarMensaje,
                                exito = true,
                                duracionMs = 4500
                            )

                            EntradaSuave(retrasoMs = 400L) {
                                BotonCampus(
                                    texto = "Enviar enlace",
                                    onClick = vm::enviar,
                                    cargando = estado.cargando
                                )
                            }
                        }
                    }

                    EntradaSuave(retrasoMs = 550L) {
                        EnlaceCampus(
                            pregunta = "¿Recordaste tu contraseña?",
                            accion = "Inicia sesión",
                            onClick = onIrLogin
                        )
                    }
                }
            }
        }
    }
}
