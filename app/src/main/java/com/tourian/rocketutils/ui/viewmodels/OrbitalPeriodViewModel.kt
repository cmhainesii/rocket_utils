package com.tourian.rocketutils.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tourian.rocketutils.objects.CelestialBody
import com.tourian.rocketutils.objects.TimeHolder
import com.tourian.rocketutils.ui.screens.OrbitalPeriodResult
import java.util.Locale

class OrbitalPeriodViewModel : ViewModel() {
    // State variables
    var altitude by mutableStateOf("")
        private set

    var selectedPlanetaryBody by mutableStateOf(CelestialBody.KERBIN)
        private set

    var kilometers by mutableStateOf(false)
        private set

    var orbitalPeriod by mutableStateOf<OrbitalPeriodResult?>(null)
        private set


    fun onAltitudeChange(newValue: String) {
        altitude = newValue.filter { it.isDigit() }
    }

    fun onSelectedPlanetaryBodyChange(newValue: CelestialBody) {
        selectedPlanetaryBody = newValue
    }

    fun onKilometersChange(newValue: Boolean) {
        kilometers = newValue
    }

    fun calculateOrbitalPeriod() {
        var alt = altitude.toDoubleOrNull() ?: 0.0

        if (kilometers) {
            alt *= 1000
        }

        val periodSeconds = selectedPlanetaryBody.calculateOrbitalPeriod(alt)
        val formattedSeconds = String.format(Locale.US,"%,.4f seconds", periodSeconds)

        val timeResult = TimeHolder.fromSeconds(periodSeconds.toInt())

        orbitalPeriod = OrbitalPeriodResult(
            seconds = formattedSeconds,
            time = timeResult.toFormattedString()
        )


    }
}