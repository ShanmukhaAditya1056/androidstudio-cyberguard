package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SettingsModel
import com.example.ui.components.*
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppGradients
import com.example.ui.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SecurityViewModel
) {
    val context = LocalContext.current
    val settingsState by viewModel.settings.collectAsState()
    val securityScore by viewModel.securityScore.collectAsState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
    ) {
        // App title appbar
        item {
            TopAppBar(
                title = { Text(text = "Security Settings", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
            Divider(color = AppColors.border, thickness = 0.5.dp)
        }

        // Top user info summary card
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(AppGradients.primaryGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Shield Guard Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "CyberGuard Professional AI",
                                    color = AppColors.textPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Version 2.0 • All shields operational",
                                    color = AppColors.textSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        // Active Score indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.lowBadgeBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Score: $securityScore",
                                color = AppColors.lowBadgeText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section 1: Protection switches
        item {
            SectionHeaderWithMargin(text = "Real-Time Protection Settings")
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                CyberCard(padding = 0.dp) {
                    SwitchSettingTile(
                        icon = Icons.Default.Security,
                        title = "Active Security Alerts",
                        subtitle = "Alert on trojan, phishing and connection changes",
                        checked = settingsState.realTimeAlerts,
                        onCheckedChange = { viewModel.updateSettings(settingsState.copy(realTimeAlerts = it)) }
                    )
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    SwitchSettingTile(
                        icon = Icons.Default.ContentPasteGo,
                        title = "Automated Clipboard sweep",
                        subtitle = "Checks browsers copy buffer for text keywords",
                        checked = settingsState.clipboardScanner,
                        onCheckedChange = { viewModel.updateSettings(settingsState.copy(clipboardScanner = it)) }
                    )
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    SwitchSettingTile(
                        icon = Icons.Default.WifiTethering,
                        title = "Automated Wi-Fi scanner",
                        subtitle = "Audits wireless AP fingerprints on association",
                        checked = settingsState.wifiAutoScan,
                        onCheckedChange = { viewModel.updateSettings(settingsState.copy(wifiAutoScan = it)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section 2: Privacy Standards Compliance
        item {
            SectionHeaderWithMargin(text = "Privacy Standards & k-Anonymity Compliance")
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                CyberCard(padding = 0.dp) {
                    ActionLabelSettingTile(
                        icon = Icons.Default.VerifiedUser,
                        title = "Zero Private Data collection",
                        subtitle = "Saves scans locally. No accounts creation required.",
                        actionText = "ACTIVE",
                        actionColor = AppColors.safeGreen
                    )
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    ActionLabelSettingTile(
                        icon = Icons.Default.Hub,
                        title = "k-Anonymity Hashing Tunnel",
                        subtitle = "Obfuscates credential inputs on remote queries",
                        actionText = "ENCRYPTED",
                        actionColor = AppColors.safeGreen
                    )
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    ActionLabelSettingTile(
                        icon = Icons.Default.Shield,
                        title = "DPDPA 2023 Regulations",
                        subtitle = "India Digital Personal Data compliant architecture",
                        actionText = "COMPLIANT",
                        actionColor = AppColors.safeGreen
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section 3: ML Models status listings
        item {
            SectionHeaderWithMargin(text = "AI/ML Detection Accuracy ratings")
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                CyberCard(padding = 0.dp) {
                    ModelAccuracySettingTile(model = "DistilBERT Transformer NLP", accuracy = settingsState.distilBertAccuracy, description = "Phishing textual analysis")
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    ModelAccuracySettingTile(model = "Ensemble Random Forest", accuracy = settingsState.randomForestAccuracy, description = "Application permission classification")
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    ModelAccuracySettingTile(model = "Ensemble LightGBM Boost", accuracy = settingsState.lightGbmAccuracy, description = "High speed malware signature audit")
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    ModelAccuracySettingTile(model = "GNN Spyware Cluster Node", accuracy = settingsState.gnnAccuracy, description = "Identifies permissions spy clusters")
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    ModelAccuracySettingTile(model = "Isolation Forest RSSI Anomaly", accuracy = settingsState.isolationForestAccuracy, description = "Rogue AP network fingerprinting")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section 4: Danger zone
        item {
            SectionHeaderWithMargin(text = "Danger maintenance zone")
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppColors.highBadgeBg)
                        .clickable { showClearHistoryDialog = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Wipe Database",
                                tint = AppColors.dangerRed
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Wipe scan histories & Cache",
                                    color = AppColors.dangerRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Securely overwrites local database audit files",
                                    color = AppColors.dangerRed.copy(alpha = 0.75f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Launch", tint = AppColors.dangerRed)
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Confirmation wipe Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text(text = "Confirm Secure Wipe", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Are you sure you want to completely erase all previous scanning trails, alert incident logs, and network histories? This action is irreversible on SQLite.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        Toast.makeText(context, "All security tables cleared securely.", Toast.LENGTH_SHORT).show()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text(text = "ERASE ALL DATA", color = AppColors.dangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(text = "Cancel")
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun SectionHeaderWithMargin(text: String) {
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        SectionHeader(text = text)
    }
}

@Composable
fun SwitchSettingTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(0.8f), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = AppColors.textPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = AppColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = AppColors.textSecondary, fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppColors.primaryBlue)
        )
    }
}

@Composable
fun ActionLabelSettingTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionText: String,
    actionColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(0.7f), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = AppColors.textPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = AppColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = AppColors.textSecondary, fontSize = 12.sp)
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(actionColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = actionText, color = actionColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ModelAccuracySettingTile(
    model: String,
    accuracy: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = model, color = AppColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = description, color = AppColors.textSecondary, fontSize = 11.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(AppColors.infoBlue)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = "Acc: $accuracy", color = AppColors.infoBlueText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
