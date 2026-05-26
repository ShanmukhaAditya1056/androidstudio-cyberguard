package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val isPhishing: Boolean,
    val confidence: Float,
    val matchedKeywords: String, // comma separated keywords triggered
    val timestamp: Long = System.currentTimeMillis()
)
