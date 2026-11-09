package com.tecsup.pokemon

import android.content.Context
import androidx.room.Room
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.data.auth.FirebaseAuthRepository
import com.tecsup.pokemon.data.local.AppDatabase
import com.tecsup.pokemon.data.remote.RetrofitClient
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
    ).build()

    val itemRepository: ItemRepository = ItemRepositoryImpl(
        api = RetrofitClient.api,
        dao = database.itemDao()
    )

    val authRepository: AuthRepository = FirebaseAuthRepository()

    val notificador: Notificador = NotificadorAndroid(context)
}