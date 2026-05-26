package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wifi_results")
data class WifiResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ssid: String,
    val trustScore: Int,
    val riskLevel: String, // "SECURE", "CAUTION", "DANGER"
    val timestamp: Long = System.currentTimeMillis()
)

data class SecurityCheck(
    val title: String,
    val detail: String,
    val status: String // "PASS", "WARNING", "FAIL"
)
