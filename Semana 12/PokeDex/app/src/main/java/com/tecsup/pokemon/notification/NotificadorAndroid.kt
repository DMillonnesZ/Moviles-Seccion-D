package com.tecsup.pokemon.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.pokemon.MainActivity

// Implementación real del contrato con NotificationCompat
class NotificadorAndroid(
    private val context: Context
) : Notificador {

    private val canalId = "sincronizacion"
    private val notificacionId = 1

    init {
        crearCanal()
    }

    // Desde Android 8 toda notificación necesita un canal
    private fun crearCanal() {
        val canal = NotificationChannel(
            canalId,
            "Sincronización",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos al terminar de descargar los Pokémon"
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission")   // el permiso se revisa justo abajo
    override fun mostrar(titulo: String, texto: String) {
        // Android 13+: sin permiso no se muestra nada y la app sigue normal (PA-14)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        // Al tocar la notificación se abre la app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(notificacionId, notificacion)
    }
}