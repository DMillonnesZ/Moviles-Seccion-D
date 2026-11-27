package com.millones.avisoscampus.notificacion

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.millones.avisoscampus.model.Aviso

object Notificador {
    const val CANAL = "avisos"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL,
                "Avisos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de la app"   // subtítulo que se ve en Ajustes
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)      // si ya existe, no lo duplica
        }
    }

    fun tienePermiso(context: Context): Boolean {
        // Antes de Android 13 (API 33) el permiso no existe: se concede al instalar
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun mostrar(context: Context, aviso: Aviso): Boolean {
        // TODO 1: si no tienePermiso(context) -> return false
        // TODO 2: construir la notificación con NotificationCompat.Builder
        // TODO 3: NotificationManagerCompat.from(context).notify(aviso.id, ...)
        return true
    }
}
