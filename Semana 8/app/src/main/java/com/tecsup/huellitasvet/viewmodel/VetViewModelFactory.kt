package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.huellitasvet.data.repository.VetRepository

class VetViewModelFactory(private val repo: VetRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(ServiciosViewModel::class.java) -> ServiciosViewModel(repo)
        // TODO: agregar aquí cada ViewModel nuevo
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}