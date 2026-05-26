package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsModel(
    @PrimaryKey val id: Int = 1,
    val realTimeAlerts: Boolean = true,
    val clipboardScanner: Boolean = true,
    val autoScanFrequency: String = "Daily", // e.g. Daily, Weekly
    val wifiAutoScan: Boolean = true,
    val zeroDataCollection: Boolean = true,
    val kAnonymityProtocol: Boolean = true,
    val sha256Encryption: Boolean = true,
    val dpdpaCompliance: Boolean = true,
    
    // Model toggles or status details
    val distilBertAccuracy: String = "99.4%",
    val randomForestAccuracy: String = "97.2%",
    val lightGbmAccuracy: String = "97.2%",
    val gnnAccuracy: String = "97.2%",
    val isolationForestAccuracy: String = "91%"
)
