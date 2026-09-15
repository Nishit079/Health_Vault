package com.example.healthvault.data.repository

import androidx.lifecycle.LiveData
import com.example.healthvault.data.local.HealthRecordDao
import com.example.healthvault.data.local.HealthRecordEntity

class HealthRecordRepository(
    private val dao: HealthRecordDao
) {

    val allRecords: LiveData<List<HealthRecordEntity>> =
        dao.getAllRecords()

    suspend fun insert(record: HealthRecordEntity) =
        dao.insert(record)

    suspend fun update(record: HealthRecordEntity) =
        dao.update(record)

    suspend fun delete(record: HealthRecordEntity) =
        dao.delete(record)

    suspend fun getRecordById(id: Int) =
        dao.getRecordById(id)
}