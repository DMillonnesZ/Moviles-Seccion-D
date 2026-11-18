package com.millones.campusgps.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Base de datos local de la app (una sola instancia para toda la app)
@Database(entities = [LugarEntity::class], version = 1, exportSchema = false)
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
                ).build().also { instancia = it }
            }
    }
}
