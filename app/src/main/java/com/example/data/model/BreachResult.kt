package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breach_results")
data class BreachResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val credentialChecked: String,
    val found: Boolean,
    val occurrences: Int,
    val datatypesLeaked: String, // Comma separated list
    val timestamp: Long = System.currentTimeMillis()
)

data class BreachModel(
    val site: String,
    val date: String,
    val accounts: String,
    val icon: String,
    val types: List<String>,
    val remediation: List<String>
)
