package com.millones.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.millones.campusgps.data.AjustesRepositoryLocal
import com.millones.campusgps.data.AlmacenFotos
import com.millones.campusgps.data.ComentariosRepositoryFirestore
import com.millones.campusgps.data.EventosRepositoryFirestore
import com.millones.campusgps.data.LugaresRepositoryNube
import com.millones.campusgps.data.PerfilRepositoryFirestore
import com.millones.campusgps.data.UbicacionRepositoryGoogle
import com.millones.campusgps.data.UbicacionesRepositoryFirestore
import com.millones.campusgps.data.local.CampusDatabase

// Crea los ViewModels de las pestañas (Mapa, Eventos, Chat de opiniones y Perfil)
class CampusViewModelFactory(context: Context) : ViewModelProvider.Factory {

    // applicationContext evita fugas de memoria: la fábrica no debe retener una Activity
    private val appContext = context.applicationContext

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val fotos by lazy { AlmacenFotos(appContext) }
    private val ajustes by lazy { AjustesRepositoryLocal.obtener(appContext) }
    private val lugares by lazy {
        LugaresRepositoryNube(db, auth, CampusDatabase.obtener(appContext).lugarDao(), fotos)
    }
    private val eventos by lazy { EventosRepositoryFirestore(db, auth, fotos) }
    private val comentarios by lazy { ComentariosRepositoryFirestore(db, auth) }
    private val perfiles by lazy { PerfilRepositoryFirestore(db, auth, fotos) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MapaViewModel::class.java) -> MapaViewModel(
            ubicacionRepo = UbicacionRepositoryGoogle(appContext),
            lugaresRepo = lugares,
            ubicacionesRepo = UbicacionesRepositoryFirestore(db, auth),
            eventosRepo = eventos,
            ajustes = ajustes
        ) as T

        modelClass.isAssignableFrom(EventosViewModel::class.java) -> EventosViewModel(
            eventosRepo = eventos,
            lugaresRepo = lugares,
            ubicacionRepo = UbicacionRepositoryGoogle(appContext)
        ) as T

        modelClass.isAssignableFrom(OpinionesViewModel::class.java) -> OpinionesViewModel(
            comentariosRepo = comentarios,
            perfilRepo = perfiles,
            lugaresRepo = lugares,
            eventosRepo = eventos,
            ajustes = ajustes
        ) as T

        modelClass.isAssignableFrom(PerfilViewModel::class.java) -> PerfilViewModel(
            authRepo = repositorioAuthCompartido,
            perfilRepo = perfiles,
            lugaresRepo = lugares,
            eventosRepo = eventos,
            comentariosRepo = comentarios,
            ajustes = ajustes
        ) as T

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
