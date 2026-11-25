package com.millones.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.millones.murocampus.data.UsuarioRepository
import com.millones.murocampus.data.UsuarioRepositoryRemote
import com.millones.murocampus.data.remote.RetrofitClient
import com.millones.murocampus.model.Usuario
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel(private val repo: UsuarioRepository) : ViewModel() {

    private val _usuarios = MutableStateFlow<UiState<List<Usuario>>>(UiState.Cargando)
    val usuarios: StateFlow<UiState<List<Usuario>>> = _usuarios.asStateFlow()

    private var job: Job? = null

    init { cargar() }

    fun cargar() {
        job?.cancel()
        job = viewModelScope.launch {
            _usuarios.value = UiState.Cargando
            repo.listar()
                .onSuccess { _usuarios.value = UiState.Exito(it) }
                .onFailure { _usuarios.value = UiState.Error(mensajeDe(it)) }
        }
    }
}

class UsuariosViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UsuariosViewModel(UsuarioRepositoryRemote(RetrofitClient.usuarioApi)) as T
    }
}