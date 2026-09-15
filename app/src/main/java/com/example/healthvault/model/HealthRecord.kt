package com.example.healthvault.model

data class HealthRecord(
    val id: Int = 0,
    val title: String,
    val category: String,
    val doctorName: String,
    val date: String,
    val notes: String = ""
)