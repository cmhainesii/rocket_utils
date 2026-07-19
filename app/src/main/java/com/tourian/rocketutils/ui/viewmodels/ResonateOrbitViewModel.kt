package com.tourian.rocketutils.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tourian.rocketutils.objects.TimeHolder
import com.tourian.rocketutils.ui.screens.ResonateOrbitResult

class ResonateOrbitViewModel : ViewModel() {

    // Fields
    var days by mutableStateOf("")
        private set

    var hours by mutableStateOf("")
        private set

    var minutes by mutableStateOf("")
        private set

    var seconds by mutableStateOf("")
        private set

    var numSatellites by mutableStateOf("")
        private set

    var resonateOrbitResult by mutableStateOf<ResonateOrbitResult?>(null)
        private set

    // Mutators
    fun onDaysChanged(newValue: String) {
        days = newValue.filter { it.isDigit() }
    }

    fun onHoursChanged(newValue: String) {
        hours = newValue.filter { it.isDigit() }
    }

    fun onMinutesChanged(newValue: String) {
        minutes = newValue.filter { it.isDigit() }
    }

    fun onSecondsChanged(newValue: String) {
        seconds = newValue.filter { it.isDigit() }
    }

    fun onNumSatellitesChanged(newValue: String) {
        numSatellites = newValue.filter { it.isDigit() }
    }

    fun calculateResult() {
        // For now, we'll check if they entered anything and output the presets
        val d = days.toIntOrNull() ?: 0
        val h = hours.toIntOrNull() ?: 0
        val m = minutes.toIntOrNull() ?: 0
        val s = seconds.toIntOrNull() ?: 0

        val input = TimeHolder(d, h, m, s)
        val seconds = input.toSeconds()
        val num = numSatellites.ifBlank { "3" }.toInt()

        val highResonateSeconds = seconds * (num + 1) / num
        val highResonateOrbit = TimeHolder.fromSeconds(highResonateSeconds)

        val lowResonateSeconds = seconds * (num - 1) / num
        val lowResonateOrbit = TimeHolder.fromSeconds(lowResonateSeconds)

        resonateOrbitResult = ResonateOrbitResult(
            input.toFormattedString(),
            lowResonateOrbit.toFormattedString(),
            highResonateOrbit.toFormattedString(),
            num
        )
    }
}