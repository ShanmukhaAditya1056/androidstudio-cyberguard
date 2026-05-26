package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppGradients

data class OnboardingPageData(
    val title: String,
    val description: String,
    val gradient: Brush,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var currentPageIndex by remember { mutableStateOf(0) }
    
    val pages = remember {
        listOf(
            OnboardingPageData(
                title = "Real-Time Scan Shield",
                description = "Proactively intercept cyber phishing links and harmful SMS payload triggers before loading on your device.",
                gradient = AppGradients.primaryGradient,
                icon = Icons.Default.Phishing
            ),
            OnboardingPageData(
                title = "Ensemble Malware Guard",
                description = "Scan your applications with our multi-model AI system (Random Forest + LightGBM + GNN spy-cluster analyses).",
                gradient = AppGradients.warningGradient,
                icon = Icons.Default.BugReport
            ),
            OnboardingPageData(
                title = "K-Anonymity Credential Check",
                description = "Verify identity leaks securely against HaveIBeenPwned repositories. Your credentials never leak from your phone.",
                gradient = AppGradients.dangerGradient,
                icon = Icons.Default.Security
            ),
            OnboardingPageData(
                title = "Isolation Forest Wi-Fi Scan",
                description = "Run advanced physical and logical network audits to detect Rogue APs, Evil-Twins, and MITM wiretapping dangers.",
                gradient = AppGradients.safeGradient,
                icon = Icons.Default.Wifi
            )
        )
    }

    val page = pages[currentPageIndex]

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Content Layout
            Column(modifier = Modifier.fillMaxSize()) {
                // Top 55% Area: Banner Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.55f)
                        .background(page.gradient),
                    contentAlignment = Alignment.Center
                ) {
                    // Slide header icon transition
                    AnimatedContent(
                        targetState = page.icon,
                        transitionSpec = {
                            fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
                        },
                        label = "onboard_icon"
                    ) { currentIcon ->
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = currentIcon,
                                contentDescription = "Onboarding Visual Banner Icon",
                                tint = Color.White,
                                modifier = Modifier.size(76.dp)
                            )
                        }
                    }
                }

                // Bottom 45% Area: White rounded card sliding up
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(12.dp))
                            // Sliding text animation transitions
                            AnimatedContent(
                                targetState = page.title,
                                transitionSpec = {
                                    fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                                },
                                label = "onboard_title"
                            ) { titleText ->
                                Text(
                                    text = titleText,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.textPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            AnimatedContent(
                                targetState = page.description,
                                transitionSpec = {
                                    fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                                },
                                label = "onboard_desc"
                            ) { descText ->
                                Text(
                                    text = descText,
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp,
                                    color = AppColors.textSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }

                        // Bottom Actions: Next button + indicators
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Dot progress indicators
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (i in pages.indices) {
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (currentPageIndex == i) AppColors.primaryBlue
                                                else AppColors.border
                                            )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            // Next/Finish CTA button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AppGradients.primaryGradient)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AppGradients.primaryGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {
                                        if (currentPageIndex < pages.size - 1) {
                                            currentPageIndex++
                                        } else {
                                            onFinish()
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text(
                                        text = if (currentPageIndex == pages.size - 1) "GET STARTED" else "CONTINUE",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Top-right Skip Button (visible unless on the last page)
            if (currentPageIndex < pages.size - 1) {
                Text(
                    text = "Skip",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .background(Color.Transparent)
                        .background(Color.Transparent)
                        .background(Color.Transparent)
                        .background(Color.Transparent)
                        .clickable { onFinish() }
                )
            }
        }
    }
}
