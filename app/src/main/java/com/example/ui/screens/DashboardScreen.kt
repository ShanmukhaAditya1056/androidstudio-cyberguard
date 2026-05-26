package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertModel
import com.example.ui.components.*
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppGradients
import com.example.ui.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SecurityViewModel,
    onNavigateToSection: (Int) -> Unit
) {
    val securityScore by viewModel.securityScore.collectAsState()
    val scannedAppsList by viewModel.scannedApps.collectAsState()
    val alertLogs by viewModel.alerts.collectAsState()
    val phishingRecordCount by viewModel.phishingHistory.collectAsState()
    val wifiSsid by viewModel.currentWifiSsid.collectAsState()
    val wifiTextScore by viewModel.wifiTrustScore.collectAsState()

    val unreadAlertsCount = alertLogs.count { !it.isRead }

    val scanScrollState = rememberScrollState()
    val mainPageScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
    ) {
        // Extended Vivo iManager styled gradient header card
        CyberGradientHeader(
            height = 230.dp,
            title = "CyberGuard AI",
            subtitle = "SITUATION: SHIELDS ACTIVE",
            actions = {
                // Notifications icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onNavigateToSection(3) }, // Alerts page index
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Active Notifications Alert Indicator",
                        tint = Color.White
                    )
                    if (unreadAlertsCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(10.dp)
                                .background(AppColors.criticalPink, CircleShape)
                        )
                    }
                }
            },
            bottomContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Last scan: Just now",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (securityScore >= 70) "System Status: Clean" else "System Action Required",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    ScoreRingWidget(
                        score = securityScore,
                        size = 90.dp
                    )
                }
            }
        )

        // Main content content scrolling card below header
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(mainPageScrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(text = "Protection Modules")

            // 2x2 grid of feature cards styled like Vivo iManager
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // PHISHING CARD
                    FeatureGridCard(
                        modifier = Modifier.weight(1f),
                        title = "Phishing Scan",
                        status = if (phishingRecordCount.any { it.isPhishing }) "Blocked Threat" else "Protected",
                        statusColor = if (phishingRecordCount.any { it.isPhishing }) AppColors.dangerRed else AppColors.safeGreen,
                        icon = Icons.Default.Phishing,
                        iconBg = AppColors.infoBlue,
                        iconColor = AppColors.infoBlueText,
                        onClick = { onNavigateToSection(1) } // Phishing
                    )
                    // MALWARE CARD
                    val hasMalware = scannedAppsList.any { it.risk == "CRITICAL" || it.risk == "HIGH" }
                    val activeThreatsText = scannedAppsList.count { it.risk == "CRITICAL" || it.risk == "HIGH" }
                    FeatureGridCard(
                        modifier = Modifier.weight(1f),
                        title = "App Malware",
                        status = if (hasMalware) "$activeThreatsText Alerts" else "${scannedAppsList.size} Apps Checked",
                        statusColor = if (hasMalware) AppColors.dangerRed else AppColors.safeGreen,
                        icon = Icons.Default.BugReport,
                        iconBg = AppColors.medBadgeBg,
                        iconColor = AppColors.medBadgeText,
                        onClick = { onNavigateToSection(2) } // Malware
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // BREACH CARD
                    FeatureGridCard(
                        modifier = Modifier.weight(1f),
                        title = "Breach Monitor",
                        status = "Zero Log Leaks",
                        statusColor = AppColors.safeGreen,
                        icon = Icons.Default.LockReset,
                        iconBg = AppColors.highBadgeBg,
                        iconColor = AppColors.highBadgeText,
                        onClick = { onNavigateToSection(5) } // Breach monitor
                    )
                    // WIFI CARD
                    val isWifiDangerous = wifiTextScore < 45
                    FeatureGridCard(
                        modifier = Modifier.weight(1f),
                        title = "Wi-Fi Shield",
                        status = if (isWifiDangerous) "Insecure AP" else "SSID Secure",
                        statusColor = if (isWifiDangerous) AppColors.dangerRed else AppColors.safeGreen,
                        icon = Icons.Default.Wifi,
                        iconBg = AppColors.lowBadgeBg,
                        iconColor = AppColors.lowBadgeText,
                        onClick = { onNavigateToSection(6) } // WiFi Scan
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(text = "Security Summary metrics")

            // Horizontal analytics summaries
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scanScrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(value = "${scannedAppsList.size}", label = "Checked Packages")
                StatCard(value = "${alertLogs.size}", label = "Audited Incidents")
                StatCard(value = "$wifiTextScore%", label = "Wi-Fi Trust Rating")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(text = "Recent Incident Alerts")
                Text(
                    text = "See All",
                    color = AppColors.primaryBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSection(3) }
                )
            }

            // List of top 3 unread alerts
            val topAlerts = alertLogs.take(3)
            if (topAlerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "System secure. No threats detected.",
                        color = AppColors.textSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    topAlerts.forEach { alert ->
                        AlertSummaryRow(alert = alert) {
                            viewModel.markAlertRead(alert.id)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureGridCard(
    modifier: Modifier = Modifier,
    title: String,
    status: String,
    statusColor: Color,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                6.dp, 
                RoundedCornerShape(16.dp), 
                ambientColor = Color.Black.copy(alpha = 0.04f), 
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .background(Color.White)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                color = AppColors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = status,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Forward link arrow icon",
                    tint = AppColors.textMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String) {
    Box(
        modifier = Modifier
            .width(135.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = value,
                color = AppColors.primaryBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = AppColors.textSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AlertSummaryRow(alert: AlertModel, onRead: () -> Unit) {
    val indicatorColor = when (alert.severity) {
        "CRITICAL", "HIGH" -> AppColors.dangerRed
        "MEDIUM" -> AppColors.warningAmber
        else -> AppColors.safeGreen
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onRead() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(indicatorColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    color = AppColors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = alert.description,
                    color = AppColors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (!alert.isRead) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.medBadgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "NEW", color = AppColors.medBadgeText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
