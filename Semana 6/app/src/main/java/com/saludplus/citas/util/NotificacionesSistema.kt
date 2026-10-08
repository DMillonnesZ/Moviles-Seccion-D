package com.saludplus.citas.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.saludplus.citas.MainActivity
import com.saludplus.citas.data.repository.Repositorio

object NotificacionesSistema {

    const val CANAL_SONIDO_ID = "citas_sonido"
    const val CANAL_SILENCIOSO_ID = "citas_silencioso"

    // Se registran dos canales distintos porque en Android 8.0+ (API 26+)
    // la configuración de sonido de un NotificationChannel no se puede modificar
    // dinámicamente tras su creación por políticas de seguridad del SO.
    fun crearCanales(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal con sonido por defecto e importancia alta
            val canalSonido = NotificationChannel(
                CANAL_SONIDO_ID,
                "Citas Médicas (Con Sonido)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de confirmación de citas con alerta sonora."
                val sonidoUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                setSound(sonidoUri, null)
                enableVibration(true)
            }

            // Canal silencioso e importancia normal
            val canalSilencioso = NotificationChannel(
                CANAL_SILENCIOSO_ID,
                "Citas Médicas (Silencioso)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de confirmación de citas sin sonido."
                setSound(null, null)
                enableVibration(false)
            }

            notificationManager.createNotificationChannel(canalSonido)
            notificationManager.createNotificationChannel(canalSilencioso)
        }
    }

    fun mostrarNotificacionCita(
        context: Context,
        citaId: Int,
        titulo: String,
        mensaje: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val canalId = if (Repositorio.sonidoNotificacionesHabilitado) CANAL_SONIDO_ID else CANAL_SILENCIOSO_ID

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("citaId", citaId)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            citaId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(citaId, builder.build())
        } catch (e: SecurityException) {
            // Manejo de excepción en caso de restricción de permisos
        }
    }
}
