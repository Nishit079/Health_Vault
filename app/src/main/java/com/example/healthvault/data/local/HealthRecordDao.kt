package com.example.healthvault.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HealthRecordDao {

    @Insert
    suspend fun insert(record: HealthRecordEntity)

    @Update
    suspend fun update(record: HealthRecordEntity)

    @Delete
    suspend fun delete(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records ORDER BY id DESC")
    fun getAllRecords(): LiveData<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records WHERE id = :id")
    suspend fun getRecordById(id: Int): HealthRecordEntity?
}