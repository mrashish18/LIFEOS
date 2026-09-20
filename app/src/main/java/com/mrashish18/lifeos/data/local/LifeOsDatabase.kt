package com.mrashish18.lifeos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mrashish18.lifeos.data.local.dao.BehaviorEventDao
import com.mrashish18.lifeos.data.local.dao.EmergencyMessageDao
import com.mrashish18.lifeos.data.local.dao.InvestigationDao
import com.mrashish18.lifeos.data.local.dao.TaskDao
import com.mrashish18.lifeos.data.local.entity.BehaviorEventEntity
import com.mrashish18.lifeos.data.local.entity.EmergencyMessageEntity
import com.mrashish18.lifeos.data.local.dao.NotificationDao
import com.mrashish18.lifeos.data.local.entity.InvestigationEntity
import com.mrashish18.lifeos.data.local.entity.NotificationEntity
import com.mrashish18.lifeos.data.local.entity.TaskEntity

/**
 * Main Room Database for LIFEOS local persistence.
 */
@Database(
    entities = [
        TaskEntity::class,
        BehaviorEventEntity::class,
        EmergencyMessageEntity::class,
        InvestigationEntity::class,
        NotificationEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class LifeOsDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun behaviorEventDao(): BehaviorEventDao
    abstract fun emergencyMessageDao(): EmergencyMessageDao
    abstract fun investigationDao(): InvestigationDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        private const val DATABASE_NAME = "lifeos_database.db"

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `investigation_records` (
                        `id` TEXT NOT NULL,
                        `originalClaim` TEXT NOT NULL,
                        `normalizedClaim` TEXT NOT NULL,
                        `claimType` TEXT NOT NULL,
                        `domainCategory` TEXT NOT NULL,
                        `verdict` TEXT NOT NULL,
                        `confidenceScore` REAL NOT NULL,
                        `confidencePercentage` INTEGER NOT NULL,
                        `reasoning` TEXT NOT NULL,
                        `evidenceCount` INTEGER NOT NULL,
                        `topSourceNamesJson` TEXT NOT NULL,
                        `timestampEpochMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `notifications` (
                        `id` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `message` TEXT NOT NULL,
                        `timestampEpochMillis` INTEGER NOT NULL,
                        `isRead` INTEGER NOT NULL,
                        `destination` TEXT,
                        `entityId` TEXT,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_notifications_timestampEpochMillis` ON `notifications` (`timestampEpochMillis`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_notifications_isRead` ON `notifications` (`isRead`)")
            }
        }

        @Volatile
        private var INSTANCE: LifeOsDatabase? = null

        fun getInstance(context: Context): LifeOsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOsDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
