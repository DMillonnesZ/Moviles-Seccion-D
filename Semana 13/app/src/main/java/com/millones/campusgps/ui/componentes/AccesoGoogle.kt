package com.millones.campusgps.ui.componentes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.millones.campusgps.BuildConfig
import com.millones.campusgps.R
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Inicio con Google. Credential Manager necesita la Activity, por eso vive en la UI;
// la pantalla solo obtiene el token y se lo pasa al ViewModel (que no conoce Context ni Firebase).
// ---------------------------------------------------------------------------

// Devuelve la acción que abre la hoja "Elige una cuenta" de Google.
// onToken recibe el idToken; onError recibe un mensaje en español. Si el usuario cancela, no pasa nada.
@Composable
fun rememberAccesoGoogle(
    onToken: (String) -> Unit,
    onError: (String) -> Unit
): () -> Unit {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()
    val onTokenActual by rememberUpdatedState(onToken)
    val onErrorActual by rememberUpdatedState(onError)

    return remember(contexto) {
        {
            val idCliente = BuildConfig.GOOGLE_WEB_CLIENT_ID
            if (idCliente.isBlank()) {
                onErrorActual("Falta configurar el inicio con Google")
            } else {
                alcance.launch {
                    try {
                        val opcion = GetSignInWithGoogleOption.Builder(idCliente).build()
                        val solicitud = GetCredentialRequest.Builder()
                            .addCredentialOption(opcion)
                            .build()
                        val resultado = CredentialManager.create(contexto).getCredential(contexto, solicitud)
                        val credencial = resultado.credential
                        if (credencial is CustomCredential &&
                            credencial.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        ) {
                            onTokenActual(GoogleIdTokenCredential.createFrom(credencial.data).idToken)
                        } else {
                            onErrorActual("No se pudo leer la cuenta de Google")
                        }
                    } catch (e: GetCredentialCancellationException) {
                        // El usuario cerró la hoja: no es un error
                    } catch (e: NoCredentialException) {
                        onErrorActual("No hay cuentas de Google en este dispositivo")
                    } catch (e: GetCredentialException) {
                        onErrorActual("No se pudo iniciar con Google")
                    } catch (e: Exception) {
                        onErrorActual("No se pudo iniciar con Google")
                    }
                }
            }
        }
    }
}

// Devuelve la acción de cerrar sesión que también olvida la cuenta de Google elegida,
// para que la próxima vez vuelva a preguntar qué cuenta usar
@Composable
fun rememberCerrarSesionCompleta(onCerrarSesion: () -> Unit): () -> Unit {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()
    val onCerrarActual by rememberUpdatedState(onCerrarSesion)

    return remember(contexto) {
        {
            alcance.launch {
                try {
                    CredentialManager.create(contexto).clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                    // Si falla, igual se cierra la sesión de Firebase
                }
                onCerrarActual()
            }
        }
    }
}

// Línea con "o" en el centro, entre el botón principal y el de Google
@Composable
fun SeparadorO() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(CampusColores.Borde)
        )
        Text(
            text = "o",
            color = CampusColores.TextoSecundario,
            fontFamily = FuenteCampus,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(CampusColores.Borde)
        )
    }
}

// Botón blanco "Continuar con Google" con el logo oficial (res/drawable/ic_google)
@Composable
fun BotonGoogle(
    onClick: () -> Unit,
    cargando: Boolean = false,
    habilitado: Boolean = true,
    alto: Dp = 56.dp
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "escalaGoogle"
    )
    val forma = RoundedCornerShape(20.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(alto)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .alpha(if (habilitado || cargando) 1f else 0.5f)
            .clip(forma)
            .background(Color.White)
            .border(1.5.dp, CampusColores.Borde, forma)
            .clickable(
                interactionSource = interaccion,
                indication = null,
                enabled = habilitado && !cargando,
                onClick = onClick
            )
    ) {
        if (cargando) {
            CircularProgressIndicator(
                color = CampusColores.AzulPrimario,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Continuar con Google",
                    color = CampusColores.TextoOscuro,
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
