package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.ui.theme.AppColors

private val ColorScheme = lightColorScheme(
    primary = AppColors.primaryBlue,
    onPrimary = AppColors.cardWhite,
    primaryContainer = AppColors.lowBadgeBg,
    onPrimaryContainer = AppColors.lowBadgeText,
    secondary = AppColors.safeGreen,
    onSecondary = AppColors.cardWhite,
    secondaryContainer = AppColors.medBadgeBg,
    onSecondaryContainer = AppColors.medBadgeText,
    surface = AppColors.cardWhite,
    background = AppColors.background,
    onSurface = AppColors.textPrimary,
    onBackground = AppColors.textPrimary,
    outline = AppColors.border,
    outlineVariant = AppColors.divider
)

@Composable
fun CyberGuardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
