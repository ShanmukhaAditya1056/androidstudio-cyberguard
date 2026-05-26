package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val severity: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val module: String, // "MALWARE", "PHISHING", "WIFI", "BREACH"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
