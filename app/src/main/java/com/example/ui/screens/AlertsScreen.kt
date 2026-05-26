package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertModel
import com.example.ui.components.*
import com.example.ui.theme.AppColors
import com.example.ui.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: SecurityViewModel
) {
    val alertsList by viewModel.alerts.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, CRITICAL, WARNING, LOW, SYSTEM

    val filteredAlerts = remember(alertsList, selectedFilter) {
        alertsList.filter {
            when (selectedFilter) {
                "ALL" -> true
                "CRITICAL/HIGH" -> it.severity == "CRITICAL" || it.severity == "HIGH"
                "MEDIUM" -> it.severity == "MEDIUM"
                "LOW" -> it.severity == "LOW"
                "SYSTEM" -> it.module == "SYSTEM" || it.module == "BREACH"
                else -> true
            }
        }
    }

    val filterScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
    ) {
        // App bar top
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Security Incidents",
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                    val unreadCount = alertsList.count { !it.isRead }
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(AppColors.criticalPink)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadCount NEW",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            actions = {
                TextButton(onClick = { viewModel.markAllAlertsRead() }) {
                    Text(text = "Mark All Read", color = AppColors.primaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Divider(color = AppColors.border, thickness = 0.5.dp)

        // Filter chips list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .horizontalScroll(filterScrollState)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chipsOptions = listOf(
                "ALL" to "All",
                "CRITICAL/HIGH" to "Critical Threats",
                "MEDIUM" to "Warnings",
                "LOW" to "Advisory",
                "SYSTEM" to "Core Operations"
            )

            chipsOptions.forEach { (filterVal, label) ->
                val isSelected = selectedFilter == filterVal
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AppColors.primaryBlue else AppColors.background)
                        .clickable { selectedFilter = filterVal }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else AppColors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Divider(color = AppColors.border, thickness = 0.5.dp)

        // Alerts logs stack
        if (filteredAlerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safe Shield Indicator",
                        tint = AppColors.safeGreen,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Device Shields secure",
                        color = AppColors.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All modules registered green. Zero incidents logged.",
                        color = AppColors.textSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredAlerts) { alert ->
                    AlertItemRowDetailed(
                        alert = alert,
                        onReadToggle = { viewModel.markAlertRead(alert.id) },
                        onDelete = { viewModel.deleteAlert(alert.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun AlertItemRowDetailed(
    alert: AlertModel,
    onReadToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val contentBorderColor = when (alert.severity) {
        "CRITICAL" -> AppColors.criticalPink
        "HIGH" -> AppColors.dangerRed
        "MEDIUM" -> AppColors.warningAmber
        else -> AppColors.safeGreen
    }

    val iconEmblem = when (alert.module) {
        "MALWARE" -> Icons.Default.BugReport
        "PHISHING" -> Icons.Default.Phishing
        "WIFI" -> Icons.Default.Wifi
        "BREACH" -> Icons.Default.LockReset
        else -> Icons.Default.Security
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable {
                expanded = !expanded
                onReadToggle()
            }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Thick left border based on severity
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(84.dp)
                    .background(contentBorderColor)
                    .align(Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(contentBorderColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconEmblem,
                                contentDescription = "Alert classification visual tag",
                                tint = contentBorderColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = alert.title,
                            color = AppColors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }
                    RiskBadge(risk = alert.severity)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.description,
                    color = AppColors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Date stamp when expanded plus action controls
                if (expanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = AppColors.border, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Module Source: ${alert.module}",
                            color = AppColors.textMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete threat incident entry from database logs",
                                tint = AppColors.dangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Simple compact datetime utility in Kotlin to replace date_utils.dart
object DateUtils {
    fun formatDateTime(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}
