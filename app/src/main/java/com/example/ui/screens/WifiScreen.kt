package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.utils.ScoreCalculator
import com.example.data.model.SecurityCheck
import com.example.ui.components.*
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppGradients
import com.example.ui.viewmodel.SecurityViewModel

@Composable
fun WifiScreen(
    viewModel: SecurityViewModel
) {
    val wifiSsid by viewModel.currentWifiSsid.collectAsState()
    val trustScore by viewModel.wifiTrustScore.collectAsState()
    val checks by viewModel.wifiChecks.collectAsState()
    val isScanning by viewModel.wifiScanActive.collectAsState()
    val scanHistory by viewModel.wifiHistory.collectAsState()

    val scrollState = rememberScrollState()

    val headerGradient = remember(trustScore) {
        when {
            trustScore >= 70 -> AppGradients.safeGradient
            trustScore >= 45 -> AppGradients.warningGradient
            else -> AppGradients.dangerGradient
        }
    }

    val riskWord = remember(trustScore) {
        when {
            trustScore >= 70 -> "SECURE NETWORK"
            trustScore >= 45 -> "CAUTION ADVISED"
            else -> "CRITICAL THREAT"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
    ) {
        // High Contrast Score Gradient Header
        CyberGradientHeader(
            height = 240.dp,
            title = wifiSsid,
            subtitle = riskWord,
            gradient = headerGradient,
            bottomContent = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$trustScore",
                        color = Color.White,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "NETWORK TRUST RATING (ISOLATION FOREST)",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            
            // Testing Trigger Row (Degrade / Secure simulation buttons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.makeWifiPublicAndDegrade() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.dangerRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Connect Public AP (Danger)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = { viewModel.resetWifiSecure() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.safeGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Connect Office WPA3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Scanning Loading progress
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AppColors.primaryBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Running Isolation Forest RSSI anomaly scans...", color = AppColors.textPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Warning Card inside unsafe environments
            if (trustScore < 45) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppGradients.dangerGradient)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning exclamation",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "CRITICAL MITM RISK DETECTED",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "This wireless network has zero encryption. Avoid checking bank balances, paying via UPI, or entering passwords on this link.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Check Card: displays 6 specific parameters
            SectionHeader(text = "Dynamic Network Checks")
            CyberCard {
                if (checks.isEmpty()) {
                    Text(text = "Initiate audit to view checks", color = AppColors.textSecondary)
                } else {
                    checks.forEachIndexed { idx, check ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val indicatorIcon = when (check.status) {
                                    "PASS" -> Icons.Default.Verified
                                    "WARNING" -> Icons.Default.Info
                                    else -> Icons.Default.Cancel
                                }
                                val indicatorPaintColor = when (check.status) {
                                    "PASS" -> AppColors.safeGreen
                                    "WARNING" -> AppColors.warningAmber
                                    else -> AppColors.dangerRed
                                }
                                Icon(
                                    imageVector = indicatorIcon,
                                    contentDescription = "Check state icon indicator",
                                    tint = indicatorPaintColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = check.title,
                                        color = AppColors.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = check.detail,
                                        color = AppColors.textSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (check.status) {
                                            "PASS" -> AppColors.lowBadgeBg
                                            "WARNING" -> AppColors.medBadgeBg
                                            else -> AppColors.highBadgeBg
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = check.status,
                                    color = when (check.status) {
                                        "PASS" -> AppColors.lowBadgeText
                                        "WARNING" -> AppColors.medBadgeText
                                        else -> AppColors.highBadgeText
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (idx < checks.size - 1) {
                            Divider(color = AppColors.border, thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Scan trigger FAB equivalence
            CyberButton(
                text = "AUDIT NETWORK AGAIN",
                onClick = { viewModel.triggerWifiScan() }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
