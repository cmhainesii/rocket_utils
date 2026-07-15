package com.tourian.rocketutils

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tourian.rocketutils.ui.screens.BurnTimeCalcScreen
import com.tourian.rocketutils.ui.screens.MainMenuScreen
import com.tourian.rocketutils.ui.screens.ResonateOrbitCalcScreen
import com.tourian.rocketutils.ui.screens.OrbitalXferCalcScreen
import com.tourian.rocketutils.ui.screens.OrbitalPeriodCalcScreen
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RocketUtilsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Define a state tracker for which screens we are on
                    var currentScreen by remember { mutableStateOf("menu") }

                    // 2. Simple conditional rendering
                    when (currentScreen) {
                        "menu" -> {
                            MainMenuScreen(
                                onNavigateToCalc = { currentScreen = "calculator" },
                                onNavigateToBurnCalc = { currentScreen = "burn_time" },
                                onNavigateToPeriodCalc = { currentScreen = "period_calc"},
                                onNavigateToOrbitalXferCalc = { currentScreen = "orbital_xfer"})

                        }
                        "burn_time" -> {
                            BurnTimeCalcScreen(onBackToMenu = { currentScreen = "menu" })
                        }

                        "period_calc" -> {
                            OrbitalPeriodCalcScreen(onBackToMenu = { currentScreen = "menu"})
                        }

                        "orbital_xfer" -> {
                            OrbitalXferCalcScreen(onBackToMenu = { currentScreen = "menu"})
                        }

                        else -> {
                            ResonateOrbitCalcScreen(onBackToMenu = { currentScreen = "menu" })
                        }
                    }

                }
            }
        }
    }
}
