package com.tecsup.pokemon.ui.evolucion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.EvolucionRepository
import com.tecsup.pokemon.domain.model.CadenaEvolutiva
import com.tecsup.pokemon.domain.model.MetodoEvolucion
import com.tecsup.pokemon.ui.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.Normalizer

// Opciones del menú Filtrar
enum class OrdenCadenas(val etiqueta: String) {
    NUM_ASC("Número ↑"),
    NUM_DESC("Número ↓"),
    NOMBRE("Nombre A → Z"),
    ETAPAS("Más etapas")
}

// Avance de la descarga
data class ProgresoDescarga(val hechas: Int, val total: Int)

@OptIn(FlowPreview::class)
class EvolucionViewModel(
    private val repo: EvolucionRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // null = "Todos"
    private val _metodo = MutableStateFlow<MetodoEvolucion?>(null)
    val metodo: StateFlow<MetodoEvolucion?> = _metodo.asStateFlow()

    private val _orden = MutableStateFlow(OrdenCadenas.NUM_ASC)
    val orden: StateFlow<OrdenCadenas> = _orden.asStateFlow()

    // null cuando no se está descargando
    private val _progreso = MutableStateFlow<ProgresoDescarga?>(null)
    val progreso: StateFlow<ProgresoDescarga?> = _progreso.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    val tiposPorPokemon: StateFlow<Map<Int, List<String>>> = repo.tiposPorPokemon
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    // Todas las cadenas de Room (null hasta que Room responda)
    private val todas: StateFlow<List<CadenaEvolutiva>?> = repo.cadenas
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val totalGuardadas: StateFlow<Int> = todas
        .map { it?.size ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // Cadenas filtradas por búsqueda (con debounce), método y orden
    val estado: StateFlow<UiState<List<CadenaEvolutiva>>> = combine(
        todas,
        _busqueda.debounce(300).map { normalizar(it) }.distinctUntilChanged(),
        _metodo,
        _orden
    ) { lista, texto, metodo, orden ->
        if (lista == null) UiState.Cargando
        else UiState.Exito(filtrarYOrdenar(lista, texto, metodo, orden))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    init {
        // La primera vez (sin cadenas guardadas) se descargan solas
        viewModelScope.launch {
            if (repo.cantidadGuardada() == 0) descargar()
        }
    }

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarMetodo(nuevo: MetodoEvolucion?) {
        _metodo.value = nuevo
    }

    fun cambiarOrden(nuevo: OrdenCadenas) {
        _orden.value = nuevo
    }

    fun descargar() {
        // Evita dos descargas a la vez
        if (_progreso.value != null) return

        viewModelScope.launch {
            _aviso.value = null
            _progreso.value = ProgresoDescarga(0, 0)
            repo.descargar { hechas, total -> _progreso.value = ProgresoDescarga(hechas, total) }
                .onFailure { _aviso.value = mensajeDe(it) }
            _progreso.value = null
        }
    }

    private fun filtrarYOrdenar(
        lista: List<CadenaEvolutiva>,
        texto: String,
        metodo: MetodoEvolucion?,
        orden: OrdenCadenas
    ): List<CadenaEvolutiva> {
        val porMetodo = if (metodo == null) lista else lista.filter { metodo in it.metodos }
        val filtradas = if (texto.isEmpty()) porMetodo else porMetodo.filter { cadena ->
            cadena.etapas.any { normalizar(it.nombre).contains(texto) || it.id.toString() == texto }
        }
        return when (orden) {
            OrdenCadenas.NUM_ASC -> filtradas.sortedBy { it.etapas.firstOrNull()?.id ?: 0 }
            OrdenCadenas.NUM_DESC -> filtradas.sortedByDescending { it.etapas.firstOrNull()?.id ?: 0 }
            OrdenCadenas.NOMBRE -> filtradas.sortedBy { it.etapas.firstOrNull()?.nombre ?: "" }
            OrdenCadenas.ETAPAS -> filtradas.sortedByDescending { it.etapas.size }
        }
    }

    // Minúsculas y sin tildes
    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")

    private fun mensajeDe(e: Throwable): String = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}