package com.tecsup.pokemon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ItemEntity::class, TipoEntity::class, ItemTipoEntity::class, EtapaCadenaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun evolucionDao(): EvolucionDao

    companion object {
        // Versión 1 -> 2: agrega la tabla de cadenas evolutivas sin borrar los datos guardados
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `etapas_cadena` (
                        `cadenaId` INTEGER NOT NULL,
                        `especieId` INTEGER NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `condicion` TEXT NOT NULL,
                        `metodo` TEXT NOT NULL,
                        `fase` INTEGER NOT NULL,
                        `orden` INTEGER NOT NULL,
                        PRIMARY KEY(`cadenaId`, `especieId`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}