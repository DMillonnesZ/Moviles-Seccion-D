package com.tecsup.pokemon

import androidx.paging.PagingData
import com.tecsup.pokemon.data.catalogo.CatalogoRepository
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Anuncio
import com.tecsup.pokemon.domain.model.Deshabilitados
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.domain.model.OrdenPokemon
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.tipos.TiposViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

// Pruebas del ViewModel de la lista (Inicio: tipos) con repositorio y notificador falsos
@OptIn(ExperimentalCoroutinesApi::class)
class TiposViewModelTest {

    private val despachador = StandardTestDispatcher()

    private val fuego = Tipo(nombre = "fire", nombreEs = "Fuego", iconoUrl = "", cantidad = 20)
    private val agua = Tipo(nombre = "water", nombreEs = "Agua", iconoUrl = "", cantidad = 30)

    // viewModelScope usa Dispatchers.Main: en pruebas se reemplaza por uno controlable
    @Before
    fun preparar() {
        Dispatchers.setMain(despachador)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun sincronizarConExito_muestraTiposYNotifica() = runTest(despachador) {
        val repo = ItemRepositoryFalso(listOf(fuego, agua)).apply { resultadoRefrescar = Result.success(25) }
        val notificador = NotificadorFalso()

        val vm = TiposViewModel(repo, CatalogoRepositoryFalso(), notificador)
        advanceUntilIdle()

        // Orden A → Z por nombre en español
        assertEquals(UiState.Exito(listOf(agua, fuego)), vm.estado.value)
        assertEquals(listOf("Se guardaron 25 elementos"), notificador.textos)
        assertNull(vm.aviso.value)
    }

    @Test
    fun sinInternetYSinDatos_muestraErrorConMensaje() = runTest(despachador) {
        val repo = ItemRepositoryFalso(emptyList()).apply {
            resultadoRefrescar = Result.failure(IOException())
            guardados = 0
        }
        val notificador = NotificadorFalso()

        val vm = TiposViewModel(repo, CatalogoRepositoryFalso(), notificador)
        advanceUntilIdle()

        assertEquals(UiState.Error("No hay conexión a internet"), vm.estado.value)
        assertTrue(notificador.textos.isEmpty())
    }

    @Test
    fun sinInternetConDatos_muestraGuardadosYAviso() = runTest(despachador) {
        val repo = ItemRepositoryFalso(listOf(fuego, agua)).apply {
            resultadoRefrescar = Result.failure(IOException())
            guardados = 150
        }

        val vm = TiposViewModel(repo, CatalogoRepositoryFalso(), NotificadorFalso())
        advanceUntilIdle()

        assertEquals(UiState.Exito(listOf(agua, fuego)), vm.estado.value)
        assertEquals("Sin conexión: mostrando datos guardados", vm.aviso.value)
    }

    @Test
    fun tipoDescontinuado_noAparece() = runTest(despachador) {
        val repo = ItemRepositoryFalso(listOf(fuego, agua)).apply { resultadoRefrescar = Result.success(50) }
        val catalogo = CatalogoRepositoryFalso(Deshabilitados(tipos = setOf("fire")))

        val vm = TiposViewModel(repo, catalogo, NotificadorFalso())
        advanceUntilIdle()

        assertEquals(UiState.Exito(listOf(agua)), vm.estado.value)
    }

    @Test
    fun buscar_filtraSinImportarTildesNiMayusculas() = runTest(despachador) {
        val repo = ItemRepositoryFalso(listOf(fuego, agua)).apply { resultadoRefrescar = Result.success(50) }

        val vm = TiposViewModel(repo, CatalogoRepositoryFalso(), NotificadorFalso())
        advanceUntilIdle()
        vm.onBusquedaChange("FUÉ")
        advanceUntilIdle()

        assertEquals(UiState.Exito(listOf(fuego)), vm.estado.value)
    }
}

// ===================== Falsos =====================

// Repositorio en memoria: se elige qué devuelve refrescar() y cuántos hay guardados
private class ItemRepositoryFalso(tiposIniciales: List<Tipo>) : ItemRepository {
    var resultadoRefrescar: Result<Int> = Result.success(0)
    var guardados = 0

    override val tipos: Flow<List<Tipo>> = MutableStateFlow(tiposIniciales)
    override val items: Flow<List<Item>> = flowOf(emptyList())
    override val favoritos: Flow<List<Item>> = flowOf(emptyList())

    override suspend fun refrescar(): Result<Int> = resultadoRefrescar
    override suspend fun obtenerDetalle(id: Int): Result<ItemDetalle> = Result.failure(IOException())
    override suspend fun alternarFavorito(id: Int) = Unit
    override suspend fun cantidadGuardada(): Int = guardados
    override suspend fun cantidadFavoritos(): Int = 0
    override suspend fun limpiarCache() = Unit
    override suspend fun cargarFavoritosCuenta(adoptarLocales: Boolean) = Unit
    override suspend fun limpiarFavoritosLocales() = Unit
    override fun itemsPorTipo(tipo: String): Flow<List<Item>> = flowOf(emptyList())
    override fun paginarPorTipo(
        tipo: String,
        texto: String,
        soloFavoritos: Boolean,
        orden: OrdenPokemon,
        excluidos: Set<Int>,
        tiposExcluidos: Set<String>
    ): Flow<PagingData<Item>> = flowOf(PagingData.empty())
}

// Catálogo en memoria (sin Firestore)
private class CatalogoRepositoryFalso(
    fuera: Deshabilitados = Deshabilitados()
) : CatalogoRepository {
    override val deshabilitados: Flow<Deshabilitados> = MutableStateFlow(fuera)
    override val anuncio: Flow<Anuncio?> = MutableStateFlow(null)
    override suspend fun leerDeshabilitados(): Deshabilitados = Deshabilitados()
    override suspend fun cambiarPokemon(id: Int, nombre: String, disponible: Boolean) = Result.success(Unit)
    override suspend fun cambiarTipo(nombre: String, nombreEs: String, disponible: Boolean) = Result.success(Unit)
    override suspend fun publicarAnuncio(texto: String) = Result.success(Unit)
    override suspend fun quitarAnuncio() = Result.success(Unit)
}

// Guarda los textos en vez de mostrar notificaciones
private class NotificadorFalso : Notificador {
    val textos = mutableListOf<String>()
    override fun mostrar(titulo: String, texto: String) {
        textos += texto
    }
}