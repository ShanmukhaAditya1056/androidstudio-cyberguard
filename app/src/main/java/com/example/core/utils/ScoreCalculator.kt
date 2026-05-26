package com.example.core.utils

import com.example.ui.theme.AppColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import com.example.ui.theme.AppGradients

object ScoreCalculator {
    fun calculate(
        phishingScore: Int, // 100 for secure, lower if threats
        malwareScore: Int,  // 100 for clean, lower if high risks
        breachScore: Int,   // 100 for clean, lower if leaked
        wifiScore: Int,     // 100 for safe, lower if public, rogue, etc.
        hasActiveLeak: Boolean
    ): Int {
        val weighted = (phishingScore * 0.30) +
                       (malwareScore * 0.35) +
                       (breachScore * 0.25) +
                       (wifiScore * 0.10)
        val rounded = weighted.toInt().coerceIn(0, 100)
        return if (hasActiveLeak) rounded.coerceAtMost(45) else rounded
    }

    fun getLabel(score: Int): String {
        return when {
            score >= 70 -> "SAFE"
            score >= 40 -> "WARNING"
            else -> "CRITICAL"
        }
    }

    fun getGradient(score: Int): Brush {
        return when {
            score >= 70 -> AppGradients.safeGradient
            score >= 40 -> AppGradients.warningGradient
            else -> AppGradients.dangerGradient
        }
    }

    fun getColor(score: Int): Color {
        return when {
            score >= 70 -> AppColors.safeGreen
            score >= 40 -> AppColors.warningAmber
            else -> AppColors.dangerRed
        }
    }
}
