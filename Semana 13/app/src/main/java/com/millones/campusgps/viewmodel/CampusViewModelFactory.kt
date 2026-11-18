package com.millones.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.millones.campusgps.data.AlmacenFotos
import com.millones.campusgps.data.EventosRepositoryFirestore
import com.millones.campusgps.data.LugaresRepositoryNube
import com.millones.campusgps.data.UbicacionRepositoryGoogle
import com.millones.campusgps.data.UbicacionesRepositoryFirestore
import com.millones.campusgps.data.local.CampusDatabase

// Crea los ViewModels del mapa y de eventos con sus repositorios
class CampusViewModelFactory(context: Context) : ViewModelProvider.Factory {

    // applicationContext evita fugas de memoria: la fábrica no debe retener una Activity
    private val appContext = context.applicationContext

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val fotos by lazy { AlmacenFotos(appContext) }
    private val lugares by lazy {
        LugaresRepositoryNube(db, auth, CampusDatabase.obtener(appContext).lugarDao(), fotos)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MapaViewModel::class.java) -> MapaViewModel(
            ubicacionRepo = UbicacionRepositoryGoogle(appContext),
            lugaresRepo = lugares,
            ubicacionesRepo = UbicacionesRepositoryFirestore(db, auth)
        ) as T

        modelClass.isAssignableFrom(EventosViewModel::class.java) -> EventosViewModel(
            eventosRepo = EventosRepositoryFirestore(db, auth, fotos),
            lugaresRepo = lugares
        ) as T

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
