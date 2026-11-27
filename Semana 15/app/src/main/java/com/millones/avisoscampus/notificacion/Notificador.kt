package com.millones.avisoscampus.notificacion

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
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.millones.avisoscampus.MainActivity
import com.millones.avisoscampus.R
import com.millones.avisoscampus.model.Aviso
import com.millones.avisoscampus.ui.theme.Azul

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

    // El permiso ya se comprueba con tienePermiso(); por eso se silencia el aviso de Lint
    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, aviso: Aviso): Boolean {
        // 1. Sin permiso no se muestra nada (y no se lanza error)
        if (!tienePermiso(context)) return false

        // Paso 4: al tocar la notificación se abre MainActivity
        val intent = Intent(context, MainActivity::class.java)
        val pendiente = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Construir la notificación en el canal Avisos
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setColor(Azul.toArgb())                 // círculo del ícono en azul, como en la captura
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendiente)
            .setAutoCancel(true)                     // se quita al tocarla
            .build()

        // 3. Publicarla con el id del aviso: cada aviso es una notificación distinta
        NotificationManagerCompat.from(context).notify(aviso.id, notificacion)
        return true
    }
}
