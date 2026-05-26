package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.utils.ShapExplainer
import com.example.data.model.ScanResult
import com.example.ui.components.*
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppGradients
import com.example.ui.viewmodel.ScanState
import com.example.ui.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhishingScreen(
    viewModel: SecurityViewModel
) {
    val context = LocalContext.current
    val scanHistory by viewModel.phishingHistory.collectAsState()
    val scanState by viewModel.phishingScanState.collectAsState()

    var urlInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
    ) {
        // Top banner header
        item {
            CyberGradientHeader(
                height = 140.dp,
                title = "Phishing Scanner",
                subtitle = "INTERCEPT PHISHING AT SOURCE"
            )
        }

        // Paste URL & Input Block
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                CyberCard {
                    Text(
                        text = "Enter Suspicious Web URL / SMS Text",
                        color = AppColors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Paste WhatsApp message, reward link or URL here...") },
                        trailingIcon = {
                            IconButton(onClick = {
                                try {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clipData = clipboard.primaryClip
                                    if (clipData != null && clipData.itemCount > 0) {
                                        val pasteText = clipData.getItemAt(0).text?.toString() ?: ""
                                        urlInput = pasteText
                                        Toast.makeText(context, "URL pasted from clipboard", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Paste failed", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste from device clip buffer",
                                    tint = AppColors.primaryBlue
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.primaryBlue,
                            unfocusedBorderColor = AppColors.border
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    CyberButton(
                        text = "SCAN SECURELY",
                        onClick = {
                            if (urlInput.trim().isNotEmpty()) {
                                viewModel.scanUrl(urlInput)
                            } else {
                                Toast.makeText(context, "Please input a URL first", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // Loading and results views
        item {
            AnimatedContent(targetState = scanState, label = "scan_transition") { state ->
                when (state) {
                    is ScanState.Idle -> {
                        Box(modifier = Modifier.padding(16.dp)) {
                            InfoCard(
                                title = "Natural Language Processing Activated",
                                desc = "Analyzes words, banking keywords and domain suffixes to predict phishing attacks."
                            )
                        }
                    }
                    is ScanState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = AppColors.primaryBlue)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Analyzing with DistilBERT AI Network...",
                                    color = AppColors.textPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Searching token classifications (84%)...",
                                    color = AppColors.textSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    is ScanState.Success -> {
                        val outcome = state.result
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            // RESULT HIGHLIGHT CARD
                            CyberCard(
                                leftBorderColor = if (outcome.isPhishing) AppColors.dangerRed else AppColors.safeGreen
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (outcome.isPhishing) AppColors.highBadgeBg else AppColors.lowBadgeBg
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (outcome.isPhishing) Icons.Default.Warning else Icons.Default.Check,
                                            contentDescription = "Scan status emblem",
                                            tint = if (outcome.isPhishing) AppColors.dangerRed else AppColors.safeGreen,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = if (outcome.isPhishing) "PHISHING DETECTED" else "SAFE URL VERIFIED",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (outcome.isPhishing) AppColors.dangerRed else AppColors.safeGreen
                                        )
                                        Text(
                                            text = "${"%.1f".format(outcome.confidence)}% confidence metric level",
                                            fontSize = 13.sp,
                                            color = AppColors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (outcome.isPhishing) "⚠️ Refrain from clicking. Urgency signals active." else "✅ Legitimate URL registered inside known whitelists.",
                                            fontSize = 12.sp,
                                            color = AppColors.textSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            // SHAP ATTRIBUTION GRAPH SCREEN
                            SectionHeader(text = "Why Flagged (AI SHAP Attribution)")
                            
                            val contributions = remember(outcome) {
                                val listTriggeredRules = outcome.matchedKeywords.split(";")
                                ShapExplainer.explainPhishing(outcome.url, listTriggeredRules)
                            }

                            CyberCard {
                                Text(
                                    text = "TreeExplainer Feature Weight Log",
                                    color = AppColors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                contributions.forEach { reason ->
                                    ShapBarWidget(
                                        feature = reason.feature,
                                        contribution = reason.contribution
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        // Recent Scans history list
        if (scanHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(text = "Recent URL Scans")
                    Text(
                        text = "Clear All",
                        color = AppColors.dangerRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.clearPhishingHistory() }
                    )
                }
            }

            items(scanHistory) { scan ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(0.7f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (scan.isPhishing) AppColors.highBadgeBg else AppColors.lowBadgeBg,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (scan.isPhishing) Icons.Default.Warning else Icons.Default.Language,
                                    contentDescription = "Type Indicator",
                                    tint = if (scan.isPhishing) AppColors.dangerRed else AppColors.safeGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = scan.url,
                                    color = AppColors.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (scan.isPhishing) "Phishing detected" else "Safe",
                                    color = if (scan.isPhishing) AppColors.dangerRed else AppColors.safeGreen,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.deletePhishingScan(scan.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete item",
                                tint = AppColors.textMuted
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoCard(title: String, desc: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.infoBlue)
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Shield logo informative bullet pointer",
                    tint = AppColors.infoBlueText,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = AppColors.infoBlueText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = desc,
                color = AppColors.infoBlueText.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
