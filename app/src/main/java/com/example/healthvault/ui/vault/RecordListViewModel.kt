package com.example.healthvault.ui.vault

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthvault.data.local.HealthRecordEntity
import com.example.healthvault.data.repository.HealthRecordRepository
import kotlinx.coroutines.launch

class RecordListViewModel(
    private val repository: HealthRecordRepository
) : ViewModel() {

    val allRecords: LiveData<List<HealthRecordEntity>> =
        repository.allRecords

    fun addRecord(record: HealthRecordEntity) {
        viewModelScope.launch {
            repository.insert(record)
        }
    }

    fun updateRecord(record: HealthRecordEntity) {
        viewModelScope.launch {
            repository.update(record)
        }
    }

    fun deleteRecord(record: HealthRecordEntity) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }

    suspend fun getRecordById(id: Int) =
        repository.getRecordById(id)
}