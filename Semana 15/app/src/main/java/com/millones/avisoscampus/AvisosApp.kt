package com.millones.avisoscampus

import android.app.Application
import com.millones.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Se ejecuta una sola vez al iniciar el proceso de la app, antes que cualquier Activity
        Notificador.crearCanal(this)
    }
}
