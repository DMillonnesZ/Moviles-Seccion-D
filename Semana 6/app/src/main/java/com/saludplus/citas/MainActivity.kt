package com.saludplus.citas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.saludplus.citas.navigation.AppNavigation
import com.saludplus.citas.ui.theme.SaludPlusTheme
import com.saludplus.citas.util.NotificacionesSistema

class MainActivity : ComponentActivity() {

    private var citaIdNavegacion by mutableStateOf<Int?>(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Manejo implícito de la respuesta del usuario para notificaciones
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Registrar canales de notificación (API 26+)
        NotificacionesSistema.crearCanales(this)

        // Solicitar permiso POST_NOTIFICATIONS en Android 13+ (API 33+)
        solicitarPermisoNotificaciones()

        // Procesar intent inicial si fue lanzado desde una notificación
        procesarIntentNotificacion(intent)

        setContent {
            SaludPlusTheme {
                AppNavigation(citaIdInicial = citaIdNavegacion)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procesarIntentNotificacion(intent)
    }

    private fun procesarIntentNotificacion(intent: Intent?) {
        val citaId = intent?.getIntExtra("citaId", -1) ?: -1
        if (citaId != -1) {
            citaIdNavegacion = citaId
            intent?.removeExtra("citaId")
        }
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
