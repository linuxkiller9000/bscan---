package com.bscan.app.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProjectEntity::class,
        PageEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class BscanDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        private const val DATABASE_NAME = "bscan_offline.db"

        fun create(context: Context): BscanDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BscanDatabase::class.java,
                DATABASE_NAME,
            ).build()
        }
    }
}
