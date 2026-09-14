package com.mrashish18.lifeos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mrashish18.lifeos.data.local.dao.BehaviorEventDao
import com.mrashish18.lifeos.data.local.dao.TaskDao
import com.mrashish18.lifeos.data.local.entity.BehaviorEventEntity
import com.mrashish18.lifeos.data.local.entity.TaskEntity

/**
 * Main Room Database for LIFEOS local persistence.
 */
@Database(
    entities = [
        TaskEntity::class,
        BehaviorEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeOsDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun behaviorEventDao(): BehaviorEventDao

    companion object {
        private const val DATABASE_NAME = "lifeos_database.db"

        @Volatile
        private var INSTANCE: LifeOsDatabase? = null

        fun getInstance(context: Context): LifeOsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOsDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
