package com.tecsup.pokemon

import android.content.Context
import androidx.room.Room
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.data.auth.FirebaseAuthRepository
import com.tecsup.pokemon.data.local.AppDatabase
import com.tecsup.pokemon.data.remote.RetrofitClient
import com.tecsup.pokemon.data.repository.CartasRepository
import com.tecsup.pokemon.data.repository.CartasRepositoryImpl
import com.tecsup.pokemon.data.repository.EvolucionRepository
import com.tecsup.pokemon.data.repository.EvolucionRepositoryImpl
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.data.repository.ItemRepositoryImpl
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.notification.NotificadorAndroid

// Crea las dependencias una sola vez y las comparte con los ViewModel
class AppContainer(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "pokedex.db"
    )
        // Versión 1 -> 2: agrega las cadenas evolutivas sin perder favoritos ni Pokémon
        .addMigrations(AppDatabase.MIGRACION_1_2)
        .build()

    // PokeAPI + Room
    val itemRepository: ItemRepository = ItemRepositoryImpl(
        api = RetrofitClient.api,
        dao = database.itemDao()
    )

    // TCGdex (segunda API)
    val cartasRepository: CartasRepository = CartasRepositoryImpl(
        api = RetrofitClient.tcg
    )

    // Cadenas evolutivas (PokeAPI + Room)
    val evolucionRepository: EvolucionRepository = EvolucionRepositoryImpl(
        api = RetrofitClient.api,
        dao = database.evolucionDao()
    )

    val authRepository: AuthRepository = FirebaseAuthRepository()

    val notificador: Notificador = NotificadorAndroid(context)
}