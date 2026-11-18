package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.millones.campusgps.data.AuthRepository
import com.millones.campusgps.data.FirebaseAuthRepository
import kotlinx.coroutines.flow.StateFlow

// Sesión de toda la app (SesionViewModel de la PokeDex): quién está conectado y cerrar sesión.
// Los formularios tienen su propio ViewModel: Login, Registro y Recuperar.
class AuthViewModel(private val authRepo: AuthRepository) : ViewModel() {

    // Cuando pasa a null, NavegacionApp vuelve al login
    val usuarioActivo: StateFlow<String?> = authRepo.usuarioActivo
    val nombreActivo: StateFlow<String?> = authRepo.nombreActivo

    fun cerrarSesion() {
        authRepo.cerrarSesion()
    }
}

// Un solo repositorio para toda la app: un solo listener de Firebase
internal val repositorioAuthCompartido: AuthRepository by lazy { FirebaseAuthRepository() }

// Fábrica de los ViewModel de acceso (AppViewModelProvider de la PokeDex)
class AuthViewModelFactory(
    private val authRepo: AuthRepository = repositorioAuthCompartido
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(authRepo)
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(authRepo)
            modelClass.isAssignableFrom(RegistroViewModel::class.java) -> RegistroViewModel(authRepo)
            modelClass.isAssignableFrom(RecuperarViewModel::class.java) -> RecuperarViewModel(authRepo)
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        @Suppress("UNCHECKED_CAST")
        return vm as T
    }
}
