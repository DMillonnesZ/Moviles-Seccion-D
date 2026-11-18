package com.millones.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.millones.campusgps.data.AlmacenFotos
import com.millones.campusgps.data.LugaresRepositoryRoom
import com.millones.campusgps.data.UbicacionRepositoryGoogle
import com.millones.campusgps.data.local.CampusDatabase

class CampusViewModelFactory(context: Context) : ViewModelProvider.Factory {

    // applicationContext evita fugas de memoria: la fábrica no debe retener una Activity
    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            "ViewModel desconocido: ${modelClass.name}"
        }
        @Suppress("UNCHECKED_CAST")
        return MapaViewModel(
            UbicacionRepositoryGoogle(appContext),
            LugaresRepositoryRoom(
                dao = CampusDatabase.obtener(appContext).lugarDao(),
                fotos = AlmacenFotos(appContext)
            )
        ) as T
    }
}
