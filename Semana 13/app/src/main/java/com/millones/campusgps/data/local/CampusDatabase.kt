package com.millones.campusgps.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Base de datos local: caché de los lugares de Firestore (una sola instancia para toda la app).
// Versión 2: el id pasó a ser el id de Firestore. Como es solo caché, al cambiar de versión
// se borra y se vuelve a llenar desde Firestore.
@Database(entities = [LugarEntity::class], version = 2, exportSchema = false)
abstract class CampusDatabase : RoomDatabase() {

    abstract fun lugarDao(): LugarDao

    companion object {
        @Volatile
        private var instancia: CampusDatabase? = null

        fun obtener(contexto: Context): CampusDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    CampusDatabase::class.java,
                    "campusgps.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { instancia = it }
            }
    }
}
