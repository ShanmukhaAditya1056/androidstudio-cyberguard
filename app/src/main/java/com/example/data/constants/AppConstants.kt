package com.example.data.constants

import com.example.data.model.MalwareResult
import com.example.data.model.BreachModel

object AppConstants {
    const val GEMINI_API_KEY_PREF = "gemini_api_key_pref"

    val IndianThreatKeywords = listOf(
        "verify-now", "login-update", "otp-confirm", "kyc-update", "aadhaar-verify",
        "upi-reward", "claim-prize", "secure-hdfc", "sbi-alert", "paytm-verify",
        "free-jio", "win-prize", "trai-notice", "account-suspended", "urgent-action",
        "click-now", "lucky-winner", "reward-claim", "expire-today", "update-kyc",
        "bank-alert", "refund-claim", "prize-winner", "lottery-india", "gov-scheme"
    )

    val SuspiciousTLDs = listOf(
        ".xyz", ".tk", ".ml", ".ga", ".cf", ".click", ".top", ".work", ".loan", ".gq", ".pw", ".buzz", ".fun"
    )

    val SafeDomains = listOf(
        "google.com", "paytm.com", "phonepe.com", "gpay.com", "npci.org.in", "sbi.co.in",
        "hdfcbank.com", "icicibank.com", "axisbank.com", "amazon.in", "flipkart.com",
        "jio.com", "airtel.in", "bsnl.co.in", "incometax.gov.in", "uidai.gov.in",
        "irctc.co.in", "digilocker.gov.in", "bhimupi.org.in"
    )

    val FallbackBreachDatabase = listOf(
        BreachModel(
            site = "BigBasket",
            date = "Oct 2020",
            accounts = "20M",
            icon = "🛒",
            types = listOf("Email", "Phone", "Address", "Password"),
            remediation = listOf("Change BigBasket password immediately", "Enable 2FA on your account", "Monitor bank statements")
        ),
        BreachModel(
            site = "MobiKwik",
            date = "Mar 2021",
            accounts = "3.5M",
            icon = "💳",
            types = listOf("Email", "Phone", "KYC Data"),
            remediation = listOf("Change MobiKwik password", "Contact support to freeze custom actions", "Check Aadhaar linkages")
        ),
        BreachModel(
            site = "Air India",
            date = "May 2021",
            accounts = "4.5M",
            icon = "✈️",
            types = listOf("Passport", "Personal Info", "Credit Card"),
            remediation = listOf("Monitor credit statements", "Alert your issuing bank", "Verify passport usage")
        ),
        BreachModel(
            site = "JusPay",
            date = "Dec 2020",
            accounts = "35M",
            icon = "💰",
            types = listOf("Email", "Card Fingerprint", "Phone"),
            remediation = listOf("Alert your bank about potential compromise", "Change payment app keys", "Enable alerts")
        ),
        BreachModel(
            site = "Dominos India",
            date = "May 2021",
            accounts = "18M",
            icon = "🍕",
            types = listOf("Email", "Phone", "Location History"),
            remediation = listOf("Change password on Dominos instantly", "Scan unusual delivery and notification events")
        )
    )

    val PresetMalwareDatabase = listOf(
        MalwareResult(
            name = "PhonePe",
            packageName = "com.phonepe.app",
            icon = "💚",
            permCount = 4,
            riskScore = 8,
            risk = "LOW",
            reason = "All permissions legitimate for Unified Payments Interface (UPI)",
            shapReasonsJson = "[]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "GPay",
            packageName = "com.google.android.apps.nbu.paisa.user",
            icon = "🔵",
            permCount = 4,
            riskScore = 12,
            risk = "LOW",
            reason = "Standard secure payment permissions requested",
            shapReasonsJson = "[]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "WhatsApp",
            packageName = "com.whatsapp",
            icon = "🟢",
            permCount = 12,
            riskScore = 18,
            risk = "LOW",
            reason = "Justified credentials for global secure chat messenger services",
            shapReasonsJson = "[]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "BHIM UPI",
            packageName = "in.org.npci.upiapp",
            icon = "🇮🇳",
            permCount = 3,
            riskScore = 6,
            risk = "LOW",
            reason = "Minimal essential permission footprint for instant UPI bank transfers",
            shapReasonsJson = "[]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "Paytm",
            packageName = "net.one97.paytm",
            icon = "💙",
            permCount = 6,
            riskScore = 15,
            risk = "LOW",
            reason = "Legitimate financial application with authorized KYC attributes",
            shapReasonsJson = "[]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "TrueCaller",
            packageName = "com.truecaller",
            icon = "🔷",
            permCount = 5,
            riskScore = 22,
            risk = "LOW",
            reason = "Standard permissions for background spam filtering and primary caller lookup",
            shapReasonsJson = "[]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "JioSaavn",
            packageName = "com.jio.media.jiobeats",
            icon = "🟣",
            permCount = 4,
            riskScore = 9,
            risk = "LOW",
            reason = "Legitimate audio/music content player with background media controls",
            shapReasonsJson = "[]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "MX Player",
            packageName = "com.mxtech.videoplayer.ad",
            icon = "🟠",
            permCount = 5,
            riskScore = 19,
            risk = "LOW",
            reason = "Requires standard local video scanning and device hardware decoding access",
            shapReasonsJson = "[]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "ShareIt",
            packageName = "com.lenovo.anyshare.gps",
            icon = "⬛",
            permCount = 6,
            riskScore = 48,
            risk = "MEDIUM",
            reason = "Excessive access including localized location mapping for transmission",
            shapReasonsJson = "[{\"feature\":\"Location unnecessary\",\"contribution\":0.34},{\"feature\":\"Microphone not needed\",\"contribution\":0.28},{\"feature\":\"Third party data sharing\",\"contribution\":0.22}]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "FreeMovies HD",
            packageName = "com.freemovies.hdplay",
            icon = "🎬",
            permCount = 4,
            riskScore = 52,
            risk = "MEDIUM",
            reason = "Suspicious tracking markers coupled with microphone triggers on launch",
            shapReasonsJson = "[{\"feature\":\"Microphone in video app\",\"contribution\":0.31},{\"feature\":\"Contacts unnecessary\",\"contribution\":0.28},{\"feature\":\"Storage access\",\"contribution\":0.12}]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "UC Browser",
            packageName = "com.UCMobile.intl",
            icon = "🌐",
            permCount = 7,
            riskScore = 68,
            risk = "HIGH",
            reason = "SMS detection markers activated on third-party mobile web core",
            shapReasonsJson = "[{\"feature\":\"SMS in browser app\",\"contribution\":0.58},{\"feature\":\"Contact harvesting\",\"contribution\":0.44},{\"feature\":\"Privacy violations history\",\"contribution\":0.39}]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "FlashLight Ultra",
            packageName = "com.ultra.flashlight",
            icon = "🔦",
            permCount = 5,
            riskScore = 76,
            risk = "HIGH",
            reason = "Dangerous permission requests that are unrelated to core camera flashlight controls",
            shapReasonsJson = "[{\"feature\":\"SMS read in flashlight\",\"contribution\":0.52},{\"feature\":\"Location+Contacts\",\"contribution\":0.41},{\"feature\":\"No legitimate SMS need\",\"contribution\":0.38}]",
            gnnNote = ""
        ),
        MalwareResult(
            name = "BatteryFast Pro",
            packageName = "com.battery.saver.fastpro",
            icon = "⚡",
            permCount = 8,
            riskScore = 94,
            risk = "CRITICAL",
            reason = "Malicious cluster of hardware and service permissions signature",
            shapReasonsJson = "[{\"feature\":\"Camera+Mic+Contacts cluster\",\"contribution\":0.61},{\"feature\":\"Background execution\",\"contribution\":0.44},{\"feature\":\"SMS banking trojan pattern\",\"contribution\":0.38},{\"feature\":\"Boot persistence\",\"contribution\":0.29}]",
            gnnNote = "GNN detected: All 8 permissions form a SPYWARE cluster. Camera+Microphone+Contacts+SMS+Background together match 94% of known spyware patterns. No legitimate battery optimization app requires this permission combination."
        )
    )
}
