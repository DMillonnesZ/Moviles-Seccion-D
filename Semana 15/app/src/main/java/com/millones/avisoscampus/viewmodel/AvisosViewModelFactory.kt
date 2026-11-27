package com.millones.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.millones.avisoscampus.data.AvisosRepositoryMemoria

class AvisosViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AvisosViewModel(AvisosRepositoryMemoria()) as T
    }
}