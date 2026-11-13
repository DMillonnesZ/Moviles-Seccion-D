package com.tecsup.pokemon.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.admin.UsuariosRepository
import com.tecsup.pokemon.data.catalogo.CatalogoRepository
import com.tecsup.pokemon.data.repository.EvolucionRepository
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.AccionRegistro
import com.tecsup.pokemon.domain.model.Anuncio
import com.tecsup.pokemon.domain.model.Deshabilitados
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.domain.model.UsuarioAdmin
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.util.Traductor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException
import java.io.IOException

// Pokémon que se muestran a la vez en el catálogo (se afina con el buscador)
private const val MAXIMO_CATALOGO = 30

// Totales de la base local (pestaña Sistema)
data class ResumenAdmin(
    val totalGuardado: Int,
    val totalFavoritos: Int,
    val totalTipos: Int
)

// Resultado de la última acción (verde si salió bien, rojo si falló)
data class AvisoAdmin(
    val texto: String,
    val esError: Boolean
)

// Acción de la pestaña Sistema que se está ejecutando
enum class AccionAdmin { SINCRONIZAR, LIMPIAR }

// Filtros de la lista de usuarios
enum class FiltroUsuarios(val etiqueta: String) {
    TODOS("Todos"),
    ACTIVOS("Activos"),
    SUSPENDIDOS("Suspendidos"),
    ADMINS("Admins");

    fun incluye(usuario: UsuarioAdmin): Boolean = when (this) {
        TODOS -> true
        ACTIVOS -> usuario.activo
        SUSPENDIDOS -> !usuario.activo
        ADMINS -> usuario.rol == Rol.ADMIN
    }
}

// Cantidad de usuarios por filtro (para los chips)
data class ConteoUsuarios(
    val total: Int = 0,
    val activos: Int = 0,
    val suspendidos: Int = 0,
    val admins: Int = 0
) {
    fun de(filtro: FiltroUsuarios): Int = when (filtro) {
        FiltroUsuarios.TODOS -> total
        FiltroUsuarios.ACTIVOS -> activos
        FiltroUsuarios.SUSPENDIDOS -> suspendidos
        FiltroUsuarios.ADMINS -> admins
    }
}

// Tipo con su estado en el catálogo
data class TipoCatalogo(val tipo: Tipo, val disponible: Boolean)

// Pokémon con su estado. tipoBloqueo = tipo descontinuado que lo oculta (en español);
// mientras lo tenga, solo vuelve reactivando ese tipo
data class PokemonCatalogo(
    val item: Item,
    val disponible: Boolean,
    val tipoBloqueo: String?
)

// Pokémon que coinciden con la búsqueda (máximo 30) y totales
data class ResultadoCatalogo(
    val lista: List<PokemonCatalogo> = emptyList(),
    val total: Int = 0,
    val descontinuados: Int = 0
)

class AdminViewModel(
    private val repo: ItemRepository,
    evolucionRepo: EvolucionRepository,
    private val usuariosRepo: UsuariosRepository,
    private val catalogoRepo: CatalogoRepository,
    private val notificador: Notificador
) : ViewModel() {

    // uid del admin actual (no puede cambiar su propia cuenta)
    val uidActual: String? = usuariosRepo.uidActual

    // ===================== Usuarios =====================

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _filtro = MutableStateFlow(FiltroUsuarios.TODOS)
    val filtro: StateFlow<FiltroUsuarios> = _filtro.asStateFlow()

    // Todos los usuarios en vivo desde Firestore
    private val usuariosTodos: StateFlow<UiState<List<UsuarioAdmin>>> = usuariosRepo.usuarios()
        .map<List<UsuarioAdmin>, UiState<List<UsuarioAdmin>>> { UiState.Exito(it) }
        .catch { e -> emit(UiState.Error(e.message ?: "No se pudieron cargar los usuarios")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    // Usuarios con búsqueda (nombre o correo) y filtro aplicados
    val usuarios: StateFlow<UiState<List<UsuarioAdmin>>> =
        combine(usuariosTodos, _busqueda, _filtro) { estado, texto, filtro ->
            if (estado is UiState.Exito) {
                UiState.Exito(estado.datos.filter { filtro.incluye(it) && it.coincide(texto) })
            } else {
                estado
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    val conteo: StateFlow<ConteoUsuarios> = usuariosTodos
        .map { estado ->
            val lista = (estado as? UiState.Exito)?.datos.orEmpty()
            ConteoUsuarios(
                total = lista.size,
                activos = lista.count { it.activo },
                suspendidos = lista.count { !it.activo },
                admins = lista.count { it.rol == Rol.ADMIN }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConteoUsuarios())

    // uid del usuario que se está actualizando (muestra la carga en su tarjeta)
    private val _procesando = MutableStateFlow<String?>(null)
    val procesando: StateFlow<String?> = _procesando.asStateFlow()

    // ===================== Catálogo =====================

    // Anuncio actual para todos
    val anuncio: StateFlow<Anuncio?> = catalogoRepo.anuncio
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val deshabilitados: StateFlow<Deshabilitados> = catalogoRepo.deshabilitados
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Deshabilitados())

    // Todos los tipos con su interruptor
    val tiposCatalogo: StateFlow<List<TipoCatalogo>> =
        combine(repo.tipos, deshabilitados) { tipos, fuera ->
            tipos.map { TipoCatalogo(it, disponible = it.nombre !in fuera.tipos) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _busquedaCatalogo = MutableStateFlow("")
    val busquedaCatalogo: StateFlow<String> = _busquedaCatalogo.asStateFlow()

    private val _soloDescontinuados = MutableStateFlow(false)
    val soloDescontinuados: StateFlow<Boolean> = _soloDescontinuados.asStateFlow()

    // Pokémon con sus tipos (la relación Pokémon - tipo está en Room)
    private val pokemonConTipos: Flow<List<Item>> =
        combine(repo.items, evolucionRepo.tiposPorPokemon) { items, tipos ->
            items.map { it.copy(tipos = tipos[it.id].orEmpty()) }
        }

    // Pokémon que coinciden con la búsqueda (o solo los descontinuados, sueltos o por tipo)
    val pokemonCatalogo: StateFlow<ResultadoCatalogo> = combine(
        pokemonConTipos,
        deshabilitados,
        _busquedaCatalogo,
        _soloDescontinuados,
        tiposCatalogo
    ) { items, fuera, texto, solo, tipos ->
        val nombresEs = tipos.associate { it.tipo.nombre to it.tipo.nombreEs }
        val todos = items.map { item ->
            val bloqueo = item.tipos.firstOrNull { it in fuera.tipos }
            PokemonCatalogo(
                item = item,
                disponible = item.id !in fuera.pokemon && bloqueo == null,
                tipoBloqueo = bloqueo?.let { nombresEs[it] ?: Traductor.tipoEs(it) }
            )
        }
        val filtrados = todos.filter { (!solo || !it.disponible) && it.item.coincideCatalogo(texto) }
        ResultadoCatalogo(
            lista = filtrados.take(MAXIMO_CATALOGO),
            total = filtrados.size,
            descontinuados = todos.count { !it.disponible }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResultadoCatalogo())

    // Elemento del catálogo que se está guardando ("p25", "tfire" o "anuncio")
    private val _procesandoCatalogo = MutableStateFlow<String?>(null)
    val procesandoCatalogo: StateFlow<String?> = _procesandoCatalogo.asStateFlow()

    // ===================== Registro =====================

    val registro: StateFlow<UiState<List<AccionRegistro>>> = usuariosRepo.registro()
        .map<List<AccionRegistro>, UiState<List<AccionRegistro>>> { UiState.Exito(it) }
        .catch { e -> emit(UiState.Error(e.message ?: "No se pudo cargar el registro")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    // ===================== Sistema =====================

    private val _resumen = MutableStateFlow<UiState<ResumenAdmin>>(UiState.Cargando)
    val resumen: StateFlow<UiState<ResumenAdmin>> = _resumen.asStateFlow()

    private val _ocupado = MutableStateFlow<AccionAdmin?>(null)
    val ocupado: StateFlow<AccionAdmin?> = _ocupado.asStateFlow()

    // Aviso común a todas las pestañas
    private val _aviso = MutableStateFlow<AvisoAdmin?>(null)
    val aviso: StateFlow<AvisoAdmin?> = _aviso.asStateFlow()

    init {
        cargarTotales()
    }

    // ----- Acciones con usuarios -----

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarFiltro(filtro: FiltroUsuarios) {
        _filtro.value = filtro
    }

    fun suspender(usuario: UsuarioAdmin, motivo: String) =
        cambiarUsuario(usuario, "Cuenta de ${usuario.nombre} suspendida") {
            usuariosRepo.cambiarEstado(usuario, activo = false, motivo = motivo)
        }

    fun activar(usuario: UsuarioAdmin) =
        cambiarUsuario(usuario, "Cuenta de ${usuario.nombre} activada") {
            usuariosRepo.cambiarEstado(usuario, activo = true)
        }

    fun cambiarRol(usuario: UsuarioAdmin, rol: Rol) =
        cambiarUsuario(
            usuario,
            if (rol == Rol.ADMIN) "${usuario.nombre} ahora es administrador"
            else "${usuario.nombre} ya no es administrador"
        ) {
            usuariosRepo.cambiarRol(usuario, rol)
        }

    fun restablecerClave(usuario: UsuarioAdmin) =
        cambiarUsuario(usuario, "Se envió un correo a ${usuario.correo} para crear otra contraseña") {
            usuariosRepo.restablecerClave(usuario)
        }

    // Ejecuta una acción sobre un usuario mostrando la carga y el aviso del resultado
    private fun cambiarUsuario(
        usuario: UsuarioAdmin,
        textoExito: String,
        accion: suspend () -> Result<Unit>
    ) {
        if (_procesando.value != null) return
        viewModelScope.launch {
            _procesando.value = usuario.uid
            _aviso.value = null
            accion()
                .onSuccess { _aviso.value = AvisoAdmin(textoExito, esError = false) }
                .onFailure { e ->
                    _aviso.value = AvisoAdmin(e.message ?: "No se pudo completar la acción", esError = true)
                }
            _procesando.value = null
        }
    }

    // ----- Acciones del catálogo -----

    fun onBusquedaCatalogoChange(texto: String) {
        _busquedaCatalogo.value = texto
    }

    fun alternarSoloDescontinuados() {
        _soloDescontinuados.value = !_soloDescontinuados.value
    }

    fun cambiarPokemon(item: Item, disponible: Boolean) {
        val nombre = item.nombre.replaceFirstChar { it.uppercase() }
        cambiarCatalogo(
            clave = "p${item.id}",
            textoExito = if (disponible) "$nombre vuelve a estar disponible" else "$nombre fue descontinuado"
        ) {
            catalogoRepo.cambiarPokemon(item.id, item.nombre, disponible)
        }
    }

    // Descontinuar un tipo también oculta todos sus Pokémon
    fun cambiarTipo(tipo: Tipo, disponible: Boolean) =
        cambiarCatalogo(
            clave = "t${tipo.nombre}",
            textoExito = if (disponible) "El tipo ${tipo.nombreEs} y sus Pokémon vuelven a estar disponibles"
            else "El tipo ${tipo.nombreEs} y sus Pokémon fueron descontinuados"
        ) {
            catalogoRepo.cambiarTipo(tipo.nombre, tipo.nombreEs, disponible)
        }

    fun publicarAnuncio(texto: String) =
        cambiarCatalogo(clave = "anuncio", textoExito = "Anuncio publicado para todos") {
            catalogoRepo.publicarAnuncio(texto)
        }

    fun quitarAnuncio() =
        cambiarCatalogo(clave = "anuncio", textoExito = "Anuncio quitado") {
            catalogoRepo.quitarAnuncio()
        }

    // Ejecuta un cambio del catálogo mostrando la carga y el aviso del resultado
    private fun cambiarCatalogo(
        clave: String,
        textoExito: String,
        accion: suspend () -> Result<Unit>
    ) {
        if (_procesandoCatalogo.value != null) return
        viewModelScope.launch {
            _procesandoCatalogo.value = clave
            _aviso.value = null
            accion()
                .onSuccess { _aviso.value = AvisoAdmin(textoExito, esError = false) }
                .onFailure { e ->
                    _aviso.value = AvisoAdmin(e.message ?: "No se pudo guardar el cambio", esError = true)
                }
            _procesandoCatalogo.value = null
        }
    }

    // ----- Acciones del sistema -----

    fun cargarTotales() {
        viewModelScope.launch { leerTotales() }
    }

    // Descarga los Pokémon de PokeAPI y avisa cuántos se guardaron y cuánto tardó
    fun sincronizar() {
        if (_ocupado.value != null) return
        viewModelScope.launch {
            _ocupado.value = AccionAdmin.SINCRONIZAR
            _aviso.value = null
            val inicio = System.currentTimeMillis()
            repo.refrescar()
                .onSuccess { n ->
                    val segundos = (System.currentTimeMillis() - inicio) / 1000.0
                    _aviso.value = AvisoAdmin(
                        texto = "Se guardaron $n Pokémon en ${"%.1f".format(segundos)} s",
                        esError = false
                    )
                    notificador.mostrar("Datos actualizados", "Se guardaron $n Pokémon")
                }
                .onFailure { e -> _aviso.value = AvisoAdmin(mensajeDe(e), esError = true) }
            leerTotales()
            _ocupado.value = null
        }
    }

    // Borra los Pokémon y favoritos guardados en el teléfono
    fun limpiarCache() {
        if (_ocupado.value != null) return
        viewModelScope.launch {
            _ocupado.value = AccionAdmin.LIMPIAR
            _aviso.value = null
            try {
                repo.limpiarCache()
                _aviso.value = AvisoAdmin("Caché limpiada", esError = false)
            } catch (e: Exception) {
                _aviso.value = AvisoAdmin("No se pudo limpiar la caché", esError = true)
            }
            leerTotales()
            _ocupado.value = null
        }
    }

    fun ocultarAviso() {
        _aviso.value = null
    }

    private suspend fun leerTotales() {
        _resumen.value = try {
            UiState.Exito(
                ResumenAdmin(
                    totalGuardado = repo.cantidadGuardada(),
                    totalFavoritos = repo.cantidadFavoritos(),
                    // Si los tipos tardan, no bloquea el panel
                    totalTipos = withTimeoutOrNull(3_000) { repo.tipos.first().size } ?: 0
                )
            )
        } catch (e: Exception) {
            UiState.Error("No se pudieron leer los datos")
        }
    }

    private fun mensajeDe(e: Throwable): String = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}

// Búsqueda de usuarios por nombre o correo (sin distinguir mayúsculas)
private fun UsuarioAdmin.coincide(texto: String): Boolean {
    val buscado = texto.trim()
    if (buscado.isEmpty()) return true
    return nombre.contains(buscado, ignoreCase = true) ||
            (correo?.contains(buscado, ignoreCase = true) == true)
}

// Búsqueda de Pokémon por nombre o número ("25", "#025")
private fun Item.coincideCatalogo(texto: String): Boolean {
    val buscado = texto.trim().lowercase().removePrefix("#")
    if (buscado.isEmpty()) return true
    if (buscado.all { it.isDigit() }) return id == buscado.toIntOrNull()
    return nombre.contains(buscado, ignoreCase = true)
}