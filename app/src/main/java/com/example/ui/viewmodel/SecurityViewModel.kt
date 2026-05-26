package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.utils.ScoreCalculator
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.*
import com.example.data.service.HibpService
import com.example.data.service.NotificationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SecurityViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    
    val phishingRepository = PhishingRepository(db.scanDao())
    val malwareRepository = MalwareRepository(db.malwareDao())
    val wifiRepository = WifiRepository(db.wifiDao())
    val alertRepository = AlertRepository(db.alertDao())
    private val settingsDao = db.settingsDao()

    // PHISHING STATES
    val phishingHistory: StateFlow<List<ScanResult>> = phishingRepository.allScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _phishingScanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val phishingScanState: StateFlow<ScanState> = _phishingScanState.asStateFlow()

    // MALWARE STATES
    val scannedApps: StateFlow<List<MalwareResult>> = malwareRepository.allMalware
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _malwareScanActive = MutableStateFlow(false)
    val malwareScanActive: StateFlow<Boolean> = _malwareScanActive.asStateFlow()

    private val _malwareProgressText = MutableStateFlow("")
    val malwareProgressText: StateFlow<String> = _malwareProgressText.asStateFlow()

    private val _malwareProgressPercent = MutableStateFlow(0f)
    val malwareProgressPercent: StateFlow<Float> = _malwareProgressPercent.asStateFlow()

    // WI-FI STATES
    val wifiHistory: StateFlow<List<WifiResult>> = wifiRepository.allWifiScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentWifiSsid = MutableStateFlow("Vivo_Secure_WiFi_5G")
    val currentWifiSsid: StateFlow<String> = _currentWifiSsid.asStateFlow()

    private val _wifiTrustScore = MutableStateFlow(92)
    val wifiTrustScore: StateFlow<Int> = _wifiTrustScore.asStateFlow()

    private val _wifiChecks = MutableStateFlow<List<SecurityCheck>>(emptyList())
    val wifiChecks: StateFlow<List<SecurityCheck>> = _wifiChecks.asStateFlow()

    private val _wifiScanActive = MutableStateFlow(false)
    val wifiScanActive: StateFlow<Boolean> = _wifiScanActive.asStateFlow()

    // BREACH STATES
    private val _breachState = MutableStateFlow<BreachScanState>(BreachScanState.Idle)
    val breachState: StateFlow<BreachScanState> = _breachState.asStateFlow()

    // ALERTS STATES
    val alerts: StateFlow<List<AlertModel>> = alertRepository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // SETTINGS STATES
    val settings: StateFlow<SettingsModel> = settingsDao.getSettings()
        .map { it ?: SettingsModel() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsModel())

    // UNIFIED SECURITY SCORE
    val securityScore: StateFlow<Int> = combine(
        phishingHistory,
        scannedApps,
        wifiTrustScore,
        breachState
    ) { phScans, apps, wScore, bState ->
        // Dynamically compute scores
        val phScore = if (phScans.any { it.isPhishing }) 55 else 100
        val malScore = if (apps.any { it.risk == "CRITICAL" }) 40 else if (apps.any { it.risk == "HIGH" }) 65 else 100
        val leakFound = bState is BreachScanState.ResultFound && bState.result.found
        val bScore = if (leakFound) 40 else 100
        
        ScoreCalculator.calculate(phScore, malScore, bScore, wScore, leakFound)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    init {
        // Initialize preset database
        viewModelScope.launch {
            malwareRepository.populatePresetDatabaseIfEmpty()
            triggerWifiScan()
            // Add initial welcome alert if unconfigured
            val alertCount = alerts.value.size
            if (alertCount == 0) {
                alertRepository.addAlert(
                    title = "CyberGuard AI Operational",
                    description = "Comprehensive real-time smartphone and network shields activated successfully.",
                    severity = "LOW",
                    module = "SYSTEM"
                )
            }
        }
    }

    // ACTIONS: PHISHING
    fun scanUrl(url: String) {
        if (url.trim().isEmpty()) return
        viewModelScope.launch {
            _phishingScanState.value = ScanState.Loading
            delay(1500) // Simulate Bert NLP analysis delay
            val outcome = phishingRepository.analyzeUrl(url)
            _phishingScanState.value = ScanState.Success(outcome)

            // Trigger notification alert if Phishing detected
            if (outcome.isPhishing) {
                alertRepository.addAlert(
                    title = "Phishing URL Blocked",
                    description = "Blocked malicious web address: ${url.take(30)}... during automated clipboard sweep.",
                    severity = "HIGH",
                    module = "PHISHING"
                )
                NotificationService.showThreatAlert(
                    getApplication(),
                    "PHISHING",
                    "Security Threat Blocked",
                    "A dangerous phishing URL pattern was intercepted on the browser clip stack."
                )
            }
        }
    }

    fun clearPhishingHistory() {
        viewModelScope.launch {
            phishingRepository.clearHistory()
        }
    }

    fun deletePhishingScan(id: Int) {
        viewModelScope.launch {
            phishingRepository.deleteScan(id)
        }
    }

    // ACTIONS: MALWARE
    fun runFullMalwareScan() {
        viewModelScope.launch {
            _malwareScanActive.value = true
            _malwareProgressPercent.value = 0f
            
            val appsToScan = listOf(
                "PhonePe" to listOf("Internet", "Access_Wifi"),
                "GPay" to listOf("Internet", "Contacts"),
                "WhatsApp" to listOf("Camera", "Microphone", "Contacts", "SMS", "Storage"),
                "MX Player" to listOf("Storage"),
                "ShareIt" to listOf("Location", "Camera", "Microphone", "Storage"),
                "FreeMovies HD" to listOf("Microphone", "Contacts", "Storage"),
                "UC Browser" to listOf("Internet", "SMS", "Contacts"),
                "FlashLight Ultra" to listOf("Camera", "SMS", "Location", "Contacts"),
                "BatteryFast Pro" to listOf("Camera", "Microphone", "Contacts", "SMS", "Background", "Boot")
            )

            for (i in appsToScan.indices) {
                val app = appsToScan[i]
                _malwareProgressText.value = "Scanning: ${app.first}..."
                _malwareProgressPercent.value = (i + 1) / appsToScan.size.toFloat()
                delay(400) // visual scan progress speed
            }

            val finalMalwareResults = malwareRepository.scanInstalledApps(appsToScan)
            _malwareScanActive.value = false

            // Check if toxic products are installed, alert immediately if true
            val criticalApps = finalMalwareResults.filter { it.risk == "CRITICAL" || it.risk == "HIGH" }
            if (criticalApps.isNotEmpty()) {
                criticalApps.forEach { app ->
                    alertRepository.addAlert(
                        title = "${app.risk} Malware Payload Flagged",
                        description = "App ${app.name} is classified as spyware/trojan signature pattern. Uninstall recommended.",
                        severity = app.risk,
                        module = "MALWARE"
                    )
                    NotificationService.showThreatAlert(
                        getApplication(),
                        "MALWARE",
                        "High Risk Spyware Active",
                        "Malware payload identified inside ${app.name}. Click to view recommendations."
                    )
                }
            }
        }
    }

    fun deleteAppResult(packageName: String) {
        viewModelScope.launch {
            malwareRepository.deleteAppResult(packageName)
        }
    }

    fun clearMalwareHistory() {
        viewModelScope.launch {
            malwareRepository.clearHistory()
        }
    }

    // ACTIONS: WI-FI
    fun triggerWifiScan() {
        viewModelScope.launch {
            _wifiScanActive.value = true
            delay(1200) // simulation isolation delay
            val (result, checksList) = wifiRepository.analyzeNetwork(
                ssid = _currentWifiSsid.value,
                encryption = "WPA2",
                isPublic = false
            )
            _wifiTrustScore.value = result.trustScore
            _wifiChecks.value = checksList
            wifiRepository.saveScan(result)
            _wifiScanActive.value = false
        }
    }

    fun makeWifiPublicAndDegrade() {
        viewModelScope.launch {
            _currentWifiSsid.value = "Unsecured_Airport_WiFi_Open"
            _wifiScanActive.value = true
            delay(1000)
            val (result, checksList) = wifiRepository.analyzeNetwork(
                ssid = _currentWifiSsid.value,
                encryption = "Open",
                isPublic = true
            )
            _wifiTrustScore.value = result.trustScore
            _wifiChecks.value = checksList
            wifiRepository.saveScan(result)
            _wifiScanActive.value = false

            alertRepository.addAlert(
                title = "Insecure Wi-Fi Link Connected",
                description = "Unsecured open network. Traffic is susceptible to snooping and MITM attacks.",
                severity = "MEDIUM",
                module = "WIFI"
            )
        }
    }

    fun resetWifiSecure() {
        viewModelScope.launch {
            _currentWifiSsid.value = "Corporate_Secure_Network_WPA3"
            _wifiScanActive.value = true
            delay(1000)
            val (result, checksList) = wifiRepository.analyzeNetwork(
                ssid = _currentWifiSsid.value,
                encryption = "WPA3",
                isPublic = false
            )
            _wifiTrustScore.value = result.trustScore
            _wifiChecks.value = checksList
            wifiRepository.saveScan(result)
            _wifiScanActive.value = false
        }
    }

    // ACTIONS: BREACH MONITOR
    fun executeCredentialBreachCheck(credential: String) {
        if (credential.trim().isEmpty()) return
        viewModelScope.launch {
            _breachState.value = BreachScanState.Step1LocalHash
            delay(600)
            _breachState.value = BreachScanState.Step2QueryRange
            delay(600)
            _breachState.value = BreachScanState.Step3AnalyseResult
            delay(600)
            
            val outcome = HibpService.checkBreach(credential)
            _breachState.value = BreachScanState.ResultFound(outcome)

            if (outcome.found) {
                alertRepository.addAlert(
                    title = "Database Breach Detected",
                    description = "Credential leak found matching your email prefix inside JusPay/Dominos database repositories.",
                    severity = "CRITICAL",
                    module = "BREACH"
                )
                NotificationService.showThreatAlert(
                    getApplication(),
                    "BREACH",
                    "Credential Leak Alert",
                    "Your identity details have been matching leaked credential repositories on public records."
                )
            }
        }
    }

    fun resetBreachState() {
        _breachState.value = BreachScanState.Idle
    }

    // ACTIONS: SETTINGS
    fun updateSettings(model: SettingsModel) {
        viewModelScope.launch {
            settingsDao.saveSettings(model)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            phishingRepository.clearHistory()
            malwareRepository.clearHistory()
            wifiRepository.clearHistory()
            alertRepository.clearHistory()
            resetWifiSecure()
            resetBreachState()
            _phishingScanState.value = ScanState.Idle
            alertRepository.addAlert(
                title = "Scan logs cleared",
                description = "All previous logs, scans, and system notifications were securely deleted.",
                severity = "LOW",
                module = "SYSTEM"
            )
        }
    }

    // ACTIONS: ALERTS
    fun markAlertRead(id: Int) {
        viewModelScope.launch {
            alertRepository.markAsRead(id)
        }
    }

    fun markAllAlertsRead() {
        viewModelScope.launch {
            alertRepository.markAllAsRead()
        }
    }

    fun deleteAlert(id: Int) {
        viewModelScope.launch {
            alertRepository.deleteAlert(id)
        }
    }
}

// SUPPORT STATE ENUMS / SEALED CLASSES
sealed class ScanState {
    object Idle : ScanState()
    object Loading : ScanState()
    data class Success(val result: ScanResult) : ScanState()
}

sealed class BreachScanState {
    object Idle : BreachScanState()
    object Step1LocalHash : BreachScanState()
    object Step2QueryRange : BreachScanState()
    object Step3AnalyseResult : BreachScanState()
    data class ResultFound(val result: BreachResult) : BreachScanState()
}
