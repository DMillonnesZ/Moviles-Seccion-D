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
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.millones.campusgps.ui.componentes.AvisoTemporal
import com.millones.campusgps.ui.componentes.BarraFuerzaClave
import com.millones.campusgps.ui.componentes.BotonGoogle
import com.millones.campusgps.ui.componentes.SeparadorO
import com.millones.campusgps.ui.componentes.rememberAccesoGoogle
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.CampoCampus
import com.millones.campusgps.ui.componentes.EncabezadoTarjeta
import com.millones.campusgps.ui.componentes.EnlaceCampus
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FondoAuth
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LogoCampus
import com.millones.campusgps.ui.componentes.TarjetaAuth
import com.millones.campusgps.ui.componentes.TransicionExito
import com.millones.campusgps.ui.componentes.sacudida
import com.millones.campusgps.viewmodel.RegistroViewModel

// Alto de los campos en el registro (en el login miden 60 dp)
private val ALTO_CAMPO_REGISTRO = 52.dp

// Registro: misma tarjeta del login, 4 campos, barra de fuerza de la clave
// y celebración al crear la cuenta; después se va al login.
@Composable
fun PantallaRegistro(
    vm: RegistroViewModel,
    onCuentaCreada: () -> Unit,
    onEntroConGoogle: () -> Unit,
    onIrLogin: () -> Unit
) {
    val estado by vm.estado.collectAsState()

    // Con Google la cuenta queda creada y con sesión: se va directo al mapa
    LaunchedEffect(estado.entroConGoogle) {
        if (estado.entroConGoogle) onEntroConGoogle()
    }

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
                // Registro compacto: tiene 4 campos y el botón de Google, así que todo es un poco
                // más bajo para que entre en la pantalla sin hacer scroll
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight)
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    EntradaSuave(retrasoMs = 0L) {
                        LogoCampus(compacto = true)
                    }

                    EntradaSuave(retrasoMs = 150L) {
                        // Se sacude y vibra cuando hay un error
                        TarjetaAuth(
                            modifier = Modifier.sacudida(estado.error),
                            compacta = true
                        ) {
                            EncabezadoTarjeta(
                                titulo = "Crea tu cuenta",
                                descripcion = "Regístrate para guardar tus ubicaciones y rutas.",
                                compacto = true
                            )

                            EntradaSuave(retrasoMs = 280L) {
                                CampoCampus(
                                    valor = estado.nombre,
                                    onValorChange = vm::onNombreChange,
                                    placeholder = "Nombre",
                                    icono = Icons.Rounded.Person,
                                    habilitado = !estado.cargando,
                                    valido = estado.nombreValido,
                                    alto = ALTO_CAMPO_REGISTRO
                                )
                            }

                            EntradaSuave(retrasoMs = 360L) {
                                CampoCampus(
                                    valor = estado.correo,
                                    onValorChange = vm::onCorreoChange,
                                    placeholder = "Correo institucional",
                                    icono = Icons.Rounded.Email,
                                    tipoTeclado = KeyboardType.Email,
                                    habilitado = !estado.cargando,
                                    valido = estado.correoValido,
                                    alto = ALTO_CAMPO_REGISTRO
                                )
                            }

                            EntradaSuave(retrasoMs = 440L) {
                                Column {
                                    CampoCampus(
                                        valor = estado.clave,
                                        onValorChange = vm::onClaveChange,
                                        placeholder = "Crea una contraseña",
                                        icono = Icons.Rounded.Lock,
                                        esClave = true,
                                        habilitado = !estado.cargando,
                                        valido = estado.claveValida,
                                        alto = ALTO_CAMPO_REGISTRO
                                    )
                                    // Aparece al escribir la clave
                                    BarraFuerzaClave(nivel = estado.fuerzaClave)
                                }
                            }

                            EntradaSuave(retrasoMs = 520L) {
                                CampoCampus(
                                    valor = estado.confirmar,
                                    onValorChange = vm::onConfirmarChange,
                                    placeholder = "Confirmar contraseña",
                                    icono = Icons.Rounded.LockReset,
                                    esClave = true,
                                    habilitado = !estado.cargando,
                                    valido = estado.clavesCoinciden,
                                    alto = ALTO_CAMPO_REGISTRO
                                )
                            }

                            AvisoTemporal(
                                mensaje = estado.error,
                                onOcultar = vm::limpiarError
                            )

                            EntradaSuave(retrasoMs = 600L) {
                                BotonCampus(
                                    texto = "Crear cuenta",
                                    onClick = vm::registrar,
                                    cargando = estado.cargando,
                                    alto = 54.dp
                                )
                            }

                            EntradaSuave(retrasoMs = 660L) {
                                SeparadorO()
                            }

                            EntradaSuave(retrasoMs = 700L) {
                                BotonGoogle(
                                    onClick = entrarConGoogle,
                                    cargando = estado.cargandoGoogle,
                                    habilitado = !ocupado,
                                    alto = 50.dp
                                )
                            }
                        }
                    }

                    EntradaSuave(retrasoMs = 720L) {
                        EnlaceCampus(
                            pregunta = "¿Ya tienes cuenta?",
                            accion = "Inicia sesión",
                            onClick = onIrLogin
                        )
                    }
                }
            }
        }

        // Cuenta creada: check verde con confeti; el círculo crece y lleva al login
        if (estado.cuentaCreada) {
            TransicionExito(
                conConfeti = true,
                onTerminar = onCuentaCreada
            )
        }
    }
}
