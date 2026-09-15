package com.example.healthvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_profile")
data class EmergencyProfileEntity(
    @PrimaryKey val id: Int = 1, // We only ever want one profile
    val name: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val medications: String = "",
    val emergencyContact: String = ""
)
