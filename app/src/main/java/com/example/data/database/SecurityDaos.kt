package com.example.data.database

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan_results ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanResult)

    @Query("DELETE FROM scan_results WHERE id = :id")
    suspend fun deleteScanById(id: Int)

    @Query("DELETE FROM scan_results")
    suspend fun clearAllScans()
}

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertModel)

    @Query("UPDATE alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAlertAsRead(id: Int)

    @Query("UPDATE alerts SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteAlertById(id: Int)

    @Query("DELETE FROM alerts")
    suspend fun clearAllAlerts()
}

@Dao
interface WifiDao {
    @Query("SELECT * FROM wifi_results ORDER BY timestamp DESC")
    fun getAllWifiScans(): Flow<List<WifiResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWifiScan(wifi: WifiResult)

    @Query("DELETE FROM wifi_results")
    suspend fun clearWifiScans()
}

@Dao
interface MalwareDao {
    @Query("SELECT * FROM malware_results ORDER BY timestamp DESC")
    fun getAllMalwareResults(): Flow<List<MalwareResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMalwareResult(malware: MalwareResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMalwareResults(malwareList: List<MalwareResult>)

    @Query("DELETE FROM malware_results WHERE packageName = :packageName")
    suspend fun deleteMalwareResult(packageName: String)

    @Query("DELETE FROM malware_results")
    suspend fun clearAllMalware()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsModel?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SettingsModel)
}
