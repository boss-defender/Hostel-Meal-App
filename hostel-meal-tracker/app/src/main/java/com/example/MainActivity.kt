package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CosmicBgEnd
import com.example.ui.components.CosmicBgStart
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MealViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MealViewModel = viewModel()
                val onboardingComplete by viewModel.onboardingComplete.collectAsState()

                var currentScreen by remember(onboardingComplete) {
                    mutableStateOf(if (onboardingComplete) AppScreen.Dashboard else AppScreen.Onboarding)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(CosmicBgStart, CosmicBgEnd)
                            )
                        )
                ) {
                    when (currentScreen) {
                        AppScreen.Onboarding -> {
                            OnboardingScreen(
                                viewModel = viewModel,
                                onFinished = {
                                    currentScreen = AppScreen.Dashboard
                                }
                            )
                        }
                        AppScreen.Dashboard -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToSettings = {
                                    currentScreen = AppScreen.Settings
                                }
                            )
                        }
                        AppScreen.Settings -> {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    currentScreen = AppScreen.Dashboard
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class AppScreen { Onboarding, Dashboard, Settings }
