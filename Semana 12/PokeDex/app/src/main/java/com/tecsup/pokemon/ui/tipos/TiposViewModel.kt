package com.tecsup.pokemon.ui.tipos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class TiposViewModel(
    private val repo: ItemRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Tipo>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Tipo>>> = _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private var refrescoTerminado = false

    init {
        // La pantalla siempre muestra lo que tiene el Repository
        viewModelScope.launch {
            repo.tipos.collect { lista ->
                if (lista.isNotEmpty() || refrescoTerminado) {
                    _estado.value = UiState.Exito(lista)
                }
            }
        }
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            if (_estado.value is UiState.Error) _estado.value = UiState.Cargando

            val resultado = repo.refrescar()
            refrescoTerminado = true

            resultado
                .onSuccess {
                    if (_estado.value is UiState.Cargando) _estado.value = UiState.Exito(emptyList())
                    _aviso.value = null
                }
                .onFailure { e ->
                    if (repo.cantidadGuardada() == 0) _estado.value = UiState.Error(mensajeDe(e))
                    else _aviso.value = "Sin conexión: mostrando datos guardados"
                }
        }
    }

    private fun mensajeDe(e: Throwable): String = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}