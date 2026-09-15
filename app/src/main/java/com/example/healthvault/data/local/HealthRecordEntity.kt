package com.example.healthvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val category: String,
    val doctorName: String,
    val date: String,
    val notes: String = ""
)