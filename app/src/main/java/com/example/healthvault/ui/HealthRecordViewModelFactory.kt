package com.example.healthvault.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthvault.data.repository.HealthRecordRepository
import com.example.healthvault.ui.vault.RecordListViewModel

class HealthRecordViewModelFactory(
    private val repository: HealthRecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(RecordListViewModel::class.java)) {
            return RecordListViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}