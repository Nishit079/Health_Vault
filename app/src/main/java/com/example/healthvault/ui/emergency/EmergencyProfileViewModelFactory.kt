package com.example.healthvault.ui.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthvault.data.local.EmergencyProfileDao

class EmergencyProfileViewModelFactory(private val dao: EmergencyProfileDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmergencyProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmergencyProfileViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
