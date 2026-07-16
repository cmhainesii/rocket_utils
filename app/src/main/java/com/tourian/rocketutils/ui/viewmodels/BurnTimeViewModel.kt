package com.tourian.rocketutils.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tourian.rocketutils.objects.TimeHolder
import com.tourian.rocketutils.ui.screens.BurnResult
import java.util.Locale

class BurnTimeViewModel : ViewModel() {
    // State variables
    var days by mutableStateOf("")
        private set
    var hours by mutableStateOf("")
        private set
    var minutes by mutableStateOf("")
        private set
    var seconds by mutableStateOf("")
        private set
    var burnResult by mutableStateOf<BurnResult?>(null)
        private set

    // State Mutation Methods
    fun onDaysChange(newValue: String) {
        days = newValue.filter { it.isDigit() }
    }

    fun onHoursChange(newValue: String) {
        hours = newValue.filter { it.isDigit() }
    }

    fun onMinutesChange(newValue: String) {
        minutes = newValue.filter { it.isDigit() }
    }

    fun onSecondsChange(newValue: String) {
        seconds = newValue.filter { it.isDigit() }
    }


    fun calculateBurn() {
        val d = days.toIntOrNull() ?: 0
        val h = hours.toIntOrNull() ?: 0
        val m = minutes.toIntOrNull() ?: 0
        val s = seconds.toIntOrNull() ?: 0

        val fullBurnTime = TimeHolder(d, h, m, s)
        val fullBurnSeconds = fullBurnTime.toSeconds()
        val burnStartSeconds = fullBurnSeconds / 2
        val burnTime = TimeHolder.fromSeconds(burnStartSeconds)

        val isOdd = fullBurnSeconds % 2 != 0
        val halfSecDecimal = if (isOdd) ".5" else ""

        val formattedSeconds = String.format(
            Locale.US,
            "%,d",
            burnStartSeconds) + halfSecDecimal + " seconds"


        val rawTimeStr = burnTime.toFormattedString()
        val formattedTime = if (isOdd) {
            rawTimeStr.dropLast(1) + ".5s"
        } else {
            rawTimeStr
        }


        burnResult = BurnResult(
            seconds = formattedSeconds,
            time = formattedTime
        )
    }
}