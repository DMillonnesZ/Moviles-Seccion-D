package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.huellitasvet.data.prefs.PreferenciasStore
import com.tecsup.huellitasvet.data.repository.VetRepository

class VetViewModelFactory(
    private val repo: VetRepository,
    private val prefs: PreferenciasStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(ServiciosViewModel::class.java) -> ServiciosViewModel(repo)
        modelClass.isAssignableFrom(SesionViewModel::class.java) -> SesionViewModel(repo, prefs)
        modelClass.isAssignableFrom(InicioViewModel::class.java) -> InicioViewModel(repo)
        modelClass.isAssignableFrom(MascotaViewModel::class.java) -> MascotaViewModel(repo)
        modelClass.isAssignableFrom(PedirCitaViewModel::class.java) -> PedirCitaViewModel(repo, prefs)
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}