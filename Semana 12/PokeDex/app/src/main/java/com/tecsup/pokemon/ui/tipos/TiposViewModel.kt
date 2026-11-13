package com.tecsup.pokemon.ui.tipos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.catalogo.CatalogoRepository
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Anuncio
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.notification.Notificador
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
enum class OrdenTipos(val etiqueta: String) {
    AZ("A → Z"),
    ZA("Z → A"),
    MAS("Más Pokémon"),
    MENOS("Menos Pokémon")
}

@OptIn(FlowPreview::class)
class TiposViewModel(
    private val repo: ItemRepository,
    catalogoRepo: CatalogoRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Tipo>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Tipo>>> = _estado.asStateFlow()

    // Mensaje de "sin conexión" cuando hay datos guardados
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    // true mientras se descarga de la API
    private val _sincronizando = MutableStateFlow(false)
    val sincronizando: StateFlow<Boolean> = _sincronizando.asStateFlow()

    // Texto del buscador: se muestra al instante, pero se filtra con debounce
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _orden = MutableStateFlow(OrdenTipos.AZ)
    val orden: StateFlow<OrdenTipos> = _orden.asStateFlow()

    private var refrescoTerminado = false

    // Anuncio que el usuario cerró (vuelve a salir si el admin publica otro)
    private val _anuncioCerrado = MutableStateFlow<Anuncio?>(null)

    // Anuncio del admin para todos (null = no hay o se cerró)
    val anuncio: StateFlow<Anuncio?> = combine(catalogoRepo.anuncio, _anuncioCerrado) { actual, cerrado ->
        actual?.takeIf { it != cerrado }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Tipos de Room sin los descontinuados por el admin
    private val tiposDisponibles = combine(repo.tipos, catalogoRepo.deshabilitados) { lista, fuera ->
        lista.filter { it.nombre !in fuera.tipos }
    }

    init {
        // Tipos disponibles + búsqueda (esperando 300 ms sin escribir) + orden elegido
        viewModelScope.launch {
            combine(
                tiposDisponibles,
                _busqueda.debounce(300).map { normalizar(it) }.distinctUntilChanged(),
                _orden
            ) { lista, texto, orden -> Triple(lista, texto, orden) }
                .collect { (lista, texto, orden) ->
                    if (lista.isNotEmpty() || refrescoTerminado) {
                        _estado.value = UiState.Exito(filtrarYOrdenar(lista, texto, orden))
                    }
                }
        }
        refrescar()
    }

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarOrden(nuevo: OrdenTipos) {
        _orden.value = nuevo
    }

    fun cerrarAnuncio() {
        _anuncioCerrado.value = anuncio.value
    }

    fun refrescar() {
        // Evita dos sincronizaciones a la vez
        if (_sincronizando.value) return

        viewModelScope.launch {
            _sincronizando.value = true
            if (_estado.value is UiState.Error) _estado.value = UiState.Cargando

            val resultado = repo.refrescar()
            refrescoTerminado = true

            resultado
                .onSuccess { n ->
                    if (_estado.value is UiState.Cargando) _estado.value = UiState.Exito(emptyList())
                    _aviso.value = null
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { e ->
                    if (repo.cantidadGuardada() == 0) _estado.value = UiState.Error(mensajeDe(e))
                    else _aviso.value = "Sin conexión: mostrando datos guardados"
                }

            _sincronizando.value = false
        }
    }

    // Filtra por nombre (en español o en inglés) y ordena según la opción elegida
    private fun filtrarYOrdenar(lista: List<Tipo>, texto: String, orden: OrdenTipos): List<Tipo> {
        val filtrados = if (texto.isEmpty()) lista else lista.filter {
            normalizar(it.nombreEs).contains(texto) || it.nombre.contains(texto)
        }
        return when (orden) {
            OrdenTipos.AZ -> filtrados.sortedBy { normalizar(it.nombreEs) }
            OrdenTipos.ZA -> filtrados.sortedByDescending { normalizar(it.nombreEs) }
            OrdenTipos.MAS -> filtrados.sortedByDescending { it.cantidad }
            OrdenTipos.MENOS -> filtrados.sortedBy { it.cantidad }
        }
    }

    // Minúsculas y sin tildes: "Eléctrico" -> "electrico"
    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")

    private fun mensajeDe(e: Throwable): String = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}