package com.tecsup.pokemon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.tecsup.pokemon.data.favoritos.FavoritosRepository
import com.tecsup.pokemon.data.local.ItemDao
import com.tecsup.pokemon.data.local.ItemTipoEntity
import com.tecsup.pokemon.data.mapper.aDetalle
import com.tecsup.pokemon.data.mapper.aEntity
import com.tecsup.pokemon.data.mapper.aModelo
import com.tecsup.pokemon.data.mapper.idDesdeUrl
import com.tecsup.pokemon.data.mapper.imagenPokemon
import com.tecsup.pokemon.data.remote.ApiService
import com.tecsup.pokemon.data.remote.dto.EslabonDto
import com.tecsup.pokemon.data.remote.dto.RelacionesDanoDto
import com.tecsup.pokemon.domain.model.Debilidad
import com.tecsup.pokemon.domain.model.EtapaEvolucion
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.domain.model.OrdenPokemon
import com.tecsup.pokemon.domain.model.Tipo
import com.tecsup.pokemon.util.Traductor
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

// Room es la fuente única de verdad: la UI lee de Room y Retrofit solo la actualiza.
// Los favoritos son de cada cuenta: se guardan en Room y en Firestore (FavoritosRepository).
class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao,
    private val favoritosRemoto: FavoritosRepository
) : ItemRepository {

    // Ids >= 10000 son formas especiales (megas, gigamax...)
    private val idMaximo = 10000

    // Favoritos de la cuenta logueada (null = aún no se leyeron)
    @Volatile
    private var favoritosCuenta: Set<Int>? = null

    // Los 18 tipos que pueden atacar, para calcular debilidades
    private val tiposAtacantes = listOf(
        "normal", "fire", "water", "electric", "grass", "ice", "fighting", "poison", "ground",
        "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy"
    )

    override val items: Flow<List<Item>> =
        dao.todos().map { lista -> lista.map { it.aModelo() } }

    override val favoritos: Flow<List<Item>> =
        dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    // Tipos con la cantidad de Pokémon de cada uno
    override val tipos: Flow<List<Tipo>> =
        dao.tiposConCantidad().map { lista -> lista.map { it.aModelo() } }

    override fun itemsPorTipo(tipo: String): Flow<List<Item>> =
        dao.porTipo(tipo).map { lista -> lista.map { it.aModelo() } }

    // Paginación con placeholders: se conoce el total y solo se carga de Room
    // la página que se está viendo; permite saltar directo a cualquier página.
    // Los descontinuados (por Pokémon o por tipo) se quitan en la consulta, así el total de páginas cuadra.
    override fun paginarPorTipo(
        tipo: String,
        texto: String,
        soloFavoritos: Boolean,
        orden: OrdenPokemon,
        excluidos: Set<Int>,
        tiposExcluidos: Set<String>
    ): Flow<PagingData<Item>> =
        Pager(
            config = PagingConfig(
                pageSize = 6,
                initialLoadSize = 12,
                enablePlaceholders = true,
                jumpThreshold = 18
            )
        ) {
            dao.paginarPorTipo(tipo, texto, soloFavoritos, orden.name, excluidos.toList(), tiposExcluidos.toList())
        }.flow.map { pagina -> pagina.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = try {
        // 1. Nombres de todos los tipos
        val nombres = api.listarTipos().results.map { it.name }

        // 2. Detalle de cada tipo, en paralelo; se descartan los tipos vacíos
        val detalles = coroutineScope {
            nombres.map { nombre -> async { api.listarPorTipo(nombre) } }.awaitAll()
        }.filter { it.pokemon.isNotEmpty() }

        // 3. Favoritos de la cuenta (o los del teléfono si aún no se leyeron)
        val favoritos = favoritosCuenta ?: dao.idsFavoritos().toSet()

        // 4. Pokémon únicos, sin formas especiales, con su favorito reaplicado
        val items = detalles
            .flatMap { tipo -> tipo.pokemon.map { it.pokemon.aEntity() } }
            .filter { it.id < idMaximo }
            .distinctBy { it.id }
            .map { it.copy(esFavorito = it.id in favoritos) }

        // 5. Relación Pokémon - tipo
        val relaciones = detalles
            .flatMap { tipo ->
                tipo.pokemon.map { ItemTipoEntity(idDesdeUrl(it.pokemon.url), tipo.name) }
            }
            .filter { it.itemId < idMaximo }

        // 6. Se guarda todo en Room en una sola transacción
        dao.guardarTodo(
            items = items,
            tipos = detalles.map { it.aEntity() },
            relaciones = relaciones
        )

        // 7. Si los favoritos de la cuenta llegaron mientras se descargaba, se aplican ahora
        favoritosCuenta?.let { dao.aplicarFavoritos(it.toList()) }

        Result.success(items.size)
    } catch (e: IOException) {          // sin internet
        Result.failure(e)
    } catch (e: HttpException) {        // 404, 500...
        Result.failure(e)
    }

    // Detalle completo: datos base + especie + evolución + debilidades + habilidades en español.
    // Solo los datos base son obligatorios; si falla algún dato extra, se muestra lo que se pudo obtener.
    override suspend fun obtenerDetalle(id: Int): Result<ItemDetalle> = try {
        coroutineScope {
            val base = api.obtenerPokemon(id).aDetalle()

            // Datos extra en paralelo
            val especieD = async { runCatching { api.obtenerEspecie(id) }.getOrNull() }
            val habilidadesD = base.habilidades.map { habilidad ->
                async {
                    runCatching {
                        api.obtenerHabilidad(habilidad).names
                            .firstOrNull { it.language.name == "es" }?.name
                    }.getOrNull() ?: Traductor.formatear(habilidad)
                }
            }
            val relacionesD = base.tipos.map { tipo ->
                async { runCatching { api.obtenerRelacionesTipo(tipo).relaciones }.getOrNull() }
            }

            val especie = especieD.await()
            val cadena = especie?.cadenaEvolutiva?.url?.let { url ->
                runCatching { api.obtenerCadenaEvolutiva(url) }.getOrNull()
            }

            Result.success(
                base.copy(
                    // La descripción más reciente en español
                    descripcion = especie?.textos
                        ?.lastOrNull { it.language.name == "es" }
                        ?.texto
                        ?.let { Traductor.limpiarDescripcion(it) }
                        ?: "",
                    categoria = especie?.genera
                        ?.firstOrNull { it.language.name == "es" }
                        ?.genus
                        ?: "",
                    genero = especie?.let { Traductor.genero(it.tasaGenero) },
                    habilidades = habilidadesD.awaitAll(),
                    debilidades = calcularDebilidades(relacionesD.awaitAll().filterNotNull()),
                    evolucion = cadena?.let { aplanarCadena(it.chain) } ?: emptyList()
                )
            )
        }
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    }

    // Cambia en Room (la pantalla reacciona al instante) y en la cuenta (Firestore)
    override suspend fun alternarFavorito(id: Int) {
        dao.alternarFavorito(id)
        val ahora = dao.esFavorito(id) ?: return
        val actuales = favoritosCuenta ?: dao.idsFavoritos().toSet()
        favoritosCuenta = if (ahora) actuales + id else actuales - id
        if (ahora) favoritosRemoto.agregar(id) else favoritosRemoto.quitar(id)
    }

    override suspend fun cargarFavoritosCuenta(adoptarLocales: Boolean) {
        // Sin conexión ni copia guardada: se deja Room como está
        val remotos = favoritosRemoto.leer().getOrElse { return }
        val ids = if (remotos == null) {
            // La cuenta aún no tiene lista: se suben los del teléfono o se empieza vacía
            val iniciales = if (adoptarLocales) dao.idsFavoritos().toSet() else emptySet()
            favoritosRemoto.guardarTodos(iniciales)
            iniciales
        } else {
            remotos
        }
        favoritosCuenta = ids
        dao.aplicarFavoritos(ids.toList())
    }

    override suspend fun limpiarFavoritosLocales() {
        favoritosCuenta = null
        dao.quitarFavoritos()
    }

    override suspend fun cantidadGuardada(): Int = dao.contar()

    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    // Los favoritos siguen en la cuenta: al sincronizar se vuelven a marcar
    override suspend fun limpiarCache() = dao.borrarTodo()

    // Multiplica el daño de cada tipo atacante contra todos los tipos del Pokémon.
    // Debilidad = recibe más de x1 (x2 o x4)
    private fun calcularDebilidades(relaciones: List<RelacionesDanoDto>): List<Debilidad> {
        if (relaciones.isEmpty()) return emptyList()

        return tiposAtacantes
            .map { atacante ->
                val multiplicador = relaciones.fold(1.0) { acumulado, r ->
                    acumulado * when {
                        r.nadaDe.any { it.name == atacante } -> 0.0
                        r.dobleDe.any { it.name == atacante } -> 2.0
                        r.mitadDe.any { it.name == atacante } -> 0.5
                        else -> 1.0
                    }
                }
                Debilidad(atacante, multiplicador)
            }
            .filter { it.multiplicador > 1.0 }
            .sortedByDescending { it.multiplicador }
    }

    // Convierte el árbol de evolución en una lista de etapas con su fase y condición.
    // Funciona también con ramas (ej: Eevee tiene varias evoluciones en la fase 1)
    private fun aplanarCadena(
        eslabon: EslabonDto,
        fase: Int = 0,
        condicion: String = ""
    ): List<EtapaEvolucion> {
        val idEtapa = idDesdeUrl(eslabon.species.url)
        val etapa = EtapaEvolucion(
            id = idEtapa,
            nombre = eslabon.species.name,
            imagenUrl = imagenPokemon(idEtapa),
            condicion = condicion,
            fase = fase
        )
        val siguientes = eslabon.evolucionaA.flatMap { siguiente ->
            val detalle = siguiente.detalles.firstOrNull()
            val condicionSiguiente = Traductor.condicionEs(
                disparador = detalle?.trigger?.name,
                nivelMinimo = detalle?.nivelMinimo,
                objeto = detalle?.item?.name,
                objetoEquipado = detalle?.objetoEquipado?.name,
                amistadMinima = detalle?.amistadMinima,
                momentoDelDia = detalle?.momento?.ifBlank { null }
            )
            aplanarCadena(siguiente, fase + 1, condicionSiguiente)
        }
        return listOf(etapa) + siguientes
    }
}