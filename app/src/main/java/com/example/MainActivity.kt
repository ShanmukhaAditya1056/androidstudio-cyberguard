package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.service.NotificationService
import com.example.ui.screens.*
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppGradients
import com.example.ui.theme.CyberGuardTheme
import com.example.ui.viewmodel.SecurityViewModel

class MainActivity : ComponentActivity() {
    private val securityViewModel: SecurityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Android notification channels
        NotificationService.init(this)

        setContent {
            CyberGuardTheme {
                AppScreenEntry(viewModel = securityViewModel)
            }
        }
    }
}

@Composable
fun AppScreenEntry(viewModel: SecurityViewModel) {
    var appStateIndex by remember { mutableStateOf("SPLASH") } // SPLASH, ONBOARDING, CENTRAL_SHELL
    var selectedTabItem by remember { mutableStateOf(0) } // 0: Home, 1: Phishing, 2: Malware, 3: Alerts, 4: Settins

    // Navigation switches holding states
    when (appStateIndex) {
        "SPLASH" -> {
            SplashScreen(onNavigateNext = { appStateIndex = "ONBOARDING" })
        }
        "ONBOARDING" -> {
            OnboardingScreen(onFinish = { appStateIndex = "CENTRAL_SHELL" })
        }
        "CENTRAL_SHELL" -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    CustomNavigationBar(
                        selectedItem = selectedTabItem,
                        onItemClick = { index ->
                            selectedTabItem = index
                        }
                    )
                },
                contentWindowInsets = WindowInsets.navigationBars // Protect device notched safe indicators
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Slide central route animator
                    AnimatedContent(
                        targetState = selectedTabItem,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "screen_content_switch"
                    ) { activeIndex ->
                        when (activeIndex) {
                            0 -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToSection = { index ->
                                    selectedTabItem = index
                                }
                            )
                            1 -> PhishingScreen(viewModel = viewModel)
                            2 -> MalwareScreen(viewModel = viewModel)
                            3 -> AlertsScreen(viewModel = viewModel)
                            4 -> SettingsScreen(viewModel = viewModel)
                            // Grid targets routing index options
                            5 -> BreachScreen(viewModel = viewModel)
                            6 -> WifiScreen(viewModel = viewModel)
                        }
                    }

                    // Floating back arrow visible if on sub-grid scan views (WiFi/Breach)
                    if (selectedTabItem == 5 || selectedTabItem == 6) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 16.dp, start = 16.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.25f))
                                .clickable { selectedTabItem = 0 }, // Back to dashboard Home
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Return back arrow",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom Bottom Navigation Bar exactly matching modern Material 3 / Professional Polish guidelines
@Composable
fun CustomNavigationBar(
    selectedItem: Int,
    onItemClick: (Int) -> Unit
) {
    val items = listOf(
        NavigationTabItem("Home", Icons.Default.Home, 0),
        NavigationTabItem("Scan", Icons.Default.Shield, 1),
        NavigationTabItem("Apps", Icons.Default.Apps, 2),
        NavigationTabItem("Alerts", Icons.Default.Notifications, 3),
        NavigationTabItem("More", Icons.Default.Menu, 4)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color(0xFFF3F4F9)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Divider(color = AppColors.border, thickness = 1.dp)
            Row(
                modifier = Modifier.fillMaxSize().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isHighlighted = selectedItem == item.index || 
                            (item.index == 0 && (selectedItem == 5 || selectedItem == 6))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onItemClick(item.index) }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isHighlighted) Color(0xFFEADDFF) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isHighlighted) Color(0xFF21005D) else AppColors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            color = if (isHighlighted) AppColors.textPrimary else AppColors.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

data class NavigationTabItem(
    val label: String,
    val icon: ImageVector,
    val index: Int
)
