package com.example.healthvault.ui.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthvault.data.local.EmergencyProfileDao
import com.example.healthvault.data.local.EmergencyProfileEntity
import kotlinx.coroutines.launch

class EmergencyProfileViewModel(private val dao: EmergencyProfileDao) : ViewModel() {

    val profile = dao.getProfile().asLiveData()

    fun saveProfile(
        name: String,
        bloodType: String,
        allergies: String,
        medications: String,
        emergencyContact: String
    ) {
        viewModelScope.launch {
            val entity = EmergencyProfileEntity(
                name = name,
                bloodType = bloodType,
                allergies = allergies,
                medications = medications,
                emergencyContact = emergencyContact
            )
            dao.updateProfile(entity)
        }
    }
}
