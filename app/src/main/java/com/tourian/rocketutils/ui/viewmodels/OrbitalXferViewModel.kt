package com.tourian.rocketutils.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tourian.rocketutils.data.MissionPlanEntity
import com.tourian.rocketutils.data.MissionRepository
import com.tourian.rocketutils.data.RocketDatabase
import com.tourian.rocketutils.objects.CelestialBody
import com.tourian.rocketutils.ui.screens.OrbitalXferResult
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

class OrbitalXferViewModel(application: Application) : AndroidViewModel(application)  {
    var initialAltitude by mutableStateOf("")
        private set
    var targetAltitude by mutableStateOf("")
        private set
    var isKilometers by mutableStateOf(false)
    private set
    var selectedBody by mutableStateOf(CelestialBody.KERBIN)
        private set
    var orbitalXferResult by mutableStateOf<OrbitalXferResult?>(null)
        private set

    private val repository = MissionRepository(
        RocketDatabase.getDatabase(application).missionPlanDao()
    )

    fun onInitialAltitudeChange(newValue: String) {
        initialAltitude = newValue.filter { it.isDigit() }
    }

    fun onTargetAltitudeChange(newValue: String) {
        targetAltitude = newValue.filter { it.isDigit() }
    }

    fun isKilometers(newValue: Boolean) {
        isKilometers = newValue
    }

    fun onChangeSelectedBody(newValue: CelestialBody) {
        selectedBody = newValue
    }

    fun calculateResult() {

        var initialAlt = initialAltitude.toDoubleOrNull() ?: 0.0
        var targetAlt = targetAltitude.toDoubleOrNull() ?: 0.0

        if (isKilometers) {
            initialAlt *= 1000
            targetAlt *= 1000
        }

        val r1 = selectedBody.radiusMeters + initialAlt
        val r2 = selectedBody.radiusMeters + targetAlt
        val aTx = (r1 + r2) /  2.0 // Semi-major axis

        val v1 = sqrt(selectedBody.mu / r1)
        val vtx1 =
            sqrt(
                selectedBody.mu * (
                        (2 / r1) - (1 / aTx)
                        )
            )
        val dv1 = abs(vtx1 - v1)

        val vtx2 =
            sqrt(
                selectedBody.mu * (
                        (2 / r2) - (1 / aTx)
                        )
            )
        val v2 = sqrt(selectedBody.mu / r2)
        val dv2 = abs(vtx2 - v2)
        val totalDv = dv1 + dv2

        val dv1Formatted = String.format(Locale.US, "%,.2f", dv1)
        val dv2Formatted = String.format(Locale.US, "%,.2f", dv2)
        val totalDvFormatted = String.format(Locale.US, "%,.2f", totalDv)

        orbitalXferResult = OrbitalXferResult(
            dv1 = "$dv1Formatted m/s",
            dv2 = "$dv2Formatted m/s",
            total = "$totalDvFormatted m/s"
        )

    }

    fun saveMission(missionName: String) {

        val result = orbitalXferResult ?: return

        viewModelScope.launch {
            val entity = MissionPlanEntity(
                missionName = missionName.ifBlank { "${selectedBody.displayName} Orbit Transfer" },
                bodyName = selectedBody.displayName,
                initialAltitude = initialAltitude.toIntOrNull() ?: 0,
                targetAltitude = targetAltitude.toIntOrNull() ?: 0,
                isKilometers = isKilometers,
                totalDeltaV = result.total
            )
        }
    }
}