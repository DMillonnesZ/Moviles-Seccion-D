package com.millones.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Escucha el anuncio del sistema cuando cambia el modo avión.
 * onReceive debe ser corto: solo avisa con alCambiar y no notifica por su cuenta.
 */
class ModoAvionReceiver(private val alCambiar: (Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            alCambiar(intent.getBooleanExtra("state", false))
        }
    }
}
