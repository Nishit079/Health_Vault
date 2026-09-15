package com.example.healthvault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyProfileDao {
    @Query("SELECT * FROM emergency_profile WHERE id = 1")
    fun getProfile(): Flow<EmergencyProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProfile(profile: EmergencyProfileEntity)
}
