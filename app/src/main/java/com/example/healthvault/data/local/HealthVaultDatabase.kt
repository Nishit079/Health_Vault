package com.example.healthvault.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [HealthRecordEntity::class, EmergencyProfileEntity::class],
    version = 3,
    exportSchema = false
)
abstract class HealthVaultDatabase : RoomDatabase() {

    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun emergencyProfileDao(): EmergencyProfileDao

    companion object {

        @Volatile
        private var INSTANCE: HealthVaultDatabase? = null

        fun getDatabase(context: Context): HealthVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthVaultDatabase::class.java,
                    "health_vault_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}