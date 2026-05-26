package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppGradients

// Header element matching Vivo iManager with top status bleeding
@Composable
fun CyberGradientHeader(
    modifier: Modifier = Modifier,
    gradient: Brush = AppGradients.headerGradient,
    height: Dp = 200.dp,
    title: String,
    subtitle: String? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(gradient)
            .padding(top = 24.dp) // Status bar clearance
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (actions != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        actions()
                    }
                }
            }
            if (bottomContent != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    bottomContent()
                }
            }
        }
    }
}

// Cards with subtle borders and shadow
@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    leftBorderColor: Color? = null,
    cornerRadius: Dp = 16.dp,
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scalVal by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1.0f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .border(0.5.dp, AppColors.border, RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .background(AppColors.cardWhite)
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (leftBorderColor != null) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                        .background(leftBorderColor)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(padding)
            ) {
                content()
            }
        }
    }
}

// High contrast pill badging
@Composable
fun RiskBadge(
    modifier: Modifier = Modifier,
    risk: String
) {
    val (bgColor, textColor) = when (risk.uppercase()) {
        "LOW", "SAFE" -> Pair(AppColors.lowBadgeBg, AppColors.lowBadgeText)
        "MEDIUM" -> Pair(AppColors.medBadgeBg, AppColors.medBadgeText)
        "HIGH" -> Pair(AppColors.highBadgeBg, AppColors.highBadgeText)
        "CRITICAL", "DANGER" -> Pair(AppColors.critBadgeBg, AppColors.critBadgeText)
        else -> Pair(AppColors.infoBlue, AppColors.infoBlueText)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = risk.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// SHAP horizontal feature weight visualizations
@Composable
fun ShapBarWidget(
    modifier: Modifier = Modifier,
    feature: String,
    contribution: Float
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(contribution) {
        animProgress.animateTo(
            targetValue = contribution,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
    }

    val barColor = when {
        contribution > 0.5f -> AppColors.dangerRed
        contribution > 0.3f -> AppColors.warningAmber
        else -> AppColors.primaryBlue
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            modifier = Modifier.weight(0.4f),
            color = AppColors.textSecondary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = Modifier
                .weight(0.45f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(AppColors.border)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animProgress.value.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
        Text(
            text = "${(contribution * 100).toInt()}%",
            modifier = Modifier.padding(start = 8.dp),
            color = barColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

// Ring layout representation
@Composable
fun ScoreRingWidget(
    modifier: Modifier = Modifier,
    score: Int,
    size: Dp = 120.dp
) {
    val scoreColor = when {
        score >= 70 -> AppColors.safeGreen
        score >= 40 -> AppColors.warningAmber
        else -> AppColors.dangerRed
    }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(score) {
        animProgress.animateTo(
            targetValue = score / 100f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { animProgress.value },
            modifier = Modifier.fillMaxSize(),
            color = scoreColor,
            strokeWidth = 10.dp,
            trackColor = Color.White.copy(alpha = 0.25f)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${score}",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when {
                    score >= 70 -> "SAFE"
                    score >= 40 -> "WARNING"
                    else -> "DANGER"
                },
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Shimmer Loader element
@Composable
fun ShimmerLoadingItem(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = alphaAnim))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = alphaAnim))
        )
    }
}

// Section title labels
@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text.uppercase(),
        modifier = modifier.padding(vertical = 8.dp),
        color = AppColors.textSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

// Cyber style buttons with gradient
@Composable
fun CyberButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    gradient: Brush = AppGradients.primaryGradient
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(gradient)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
