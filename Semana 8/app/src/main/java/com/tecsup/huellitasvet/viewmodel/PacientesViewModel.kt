package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.MascotaConDueno
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Especie
import kotlinx.coroutines.flow.*

/** Mascota + dueño + fecha de su última atención (null = sin visitas) */
data class Paciente(val datos: MascotaConDueno, val ultimaVisita: String?)

class PacientesViewModel(repo: VetRepository) : ViewModel() {

    private val _texto = MutableStateFlow("")
    val texto: StateFlow<String> = _texto.asStateFlow()

    private val _especie = MutableStateFlow<Especie?>(null)        // null = Todos
    val especie: StateFlow<Especie?> = _especie.asStateFlow()

    /** Filtro por texto (mascota o dueño) y por especie, con combine */
    val pacientes: StateFlow<List<Paciente>> = combine(
        repo.observarTodasLasMascotas(),
        repo.observarUltimasVisitas(),
        _texto,
        _especie
    ) { lista, visitas, t, e ->
        val ultimas = visitas.associate { it.mascotaId to it.fecha }
        lista
            .filter { e == null || it.mascota.especie == e.name }
            .filter {
                t.isBlank() ||
                        it.mascota.nombre.contains(t.trim(), ignoreCase = true) ||
                        it.dueno.nombre.contains(t.trim(), ignoreCase = true)
            }
            .map { Paciente(it, ultimas[it.mascota.id]) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun buscar(t: String) { _texto.value = t }
    fun filtrar(e: Especie?) { _especie.value = e }
}