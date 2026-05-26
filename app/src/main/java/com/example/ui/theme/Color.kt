package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {
    val background = Color(0xFFFDFBFF)
    val cardWhite = Color(0xFFFFFFFF)
    val primaryBlue = Color(0xFF6750A4)
    val primaryBlueDark = Color(0xFF21005D)
    val safeGreen = Color(0xFF0F9B6E)
    val safeGreenLight = Color(0xFF381E72)
    val warningAmber = Color(0xFFCC8800)
    val warningAmberLight = Color(0xFFF0A030)
    val dangerRed = Color(0xFFCC2929)
    val dangerRedLight = Color(0xFFE05555)
    val criticalPink = Color(0xFFFF3B6B)
    val textPrimary = Color(0xFF1C1B1F)
    val textSecondary = Color(0xFF49454F)
    val textMuted = Color(0xFF79747E)
    val border = Color(0xFFCAC4D0)
    val divider = Color(0xFFE8DEF8)
    
    val lowBadgeBg = Color(0xFFEADDFF)
    val lowBadgeText = Color(0xFF21005D)
    val medBadgeBg = Color(0xFFF3EDF7)
    val medBadgeText = Color(0xFF49454F)
    val highBadgeBg = Color(0xFFFFD8E4)
    val highBadgeText = Color(0xFF31111D)
    val critBadgeBg = Color(0xFFFFB4AB)
    val critBadgeText = Color(0xFF690005)
    val infoBlue = Color(0xFFE8DEF8)
    val infoBlueText = Color(0xFF21005D)
}

object AppGradients {
    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF6750A4), Color(0xFFD0BCFF))
    )
    val safeGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF21005D), Color(0xFFEADDFF))
    )
    val dangerGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFB3261E), Color(0xFFF2B8B5))
    )
    val warningGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF8B6508), Color(0xFFE6C229))
    )
    val criticalGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFB3261E), Color(0xFFEADDFF))
    )
    val headerGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF21005D), Color(0xFF6750A4), Color(0xFFEADDFF))
    )
}
