package com.tourian.rocketutils.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourian.rocketutils.R
import com.tourian.rocketutils.objects.TimeHolder
import com.tourian.rocketutils.ui.components.ResultRow
import com.tourian.rocketutils.ui.components.RocketEmoji
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme
import java.util.Locale

@Composable
fun ResonateOrbitCalcScreen(onBackToMenu: () -> Unit) {
    // State variables for inputs
    var days by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }
    var numSatellites by remember { mutableStateOf( "" ) }

    var resonateOrbitResult by remember { mutableStateOf<ResonateOrbitResult?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Makes it scrollable when keyboard is up
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Tow row with a back button and title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBackToMenu) {
                Text(stringResource(R.string.label_button_back))
            }
            Spacer(modifier = Modifier.weight(1f))
            RocketEmoji()
            Text(stringResource(R.string.title_resonate_orbit_calculator),
                style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Desired orbital period:",
                style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Grid
        // Row 1: Days and Hours side by side
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = days,
                onValueChange = { input -> days = input.filter { it.isDigit() } },
                label = { Text(stringResource(R.string.label_days))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = hours,
                onValueChange = { input -> hours = input.filter { it.isDigit() } },
                label = { Text(stringResource(R.string.label_hours))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2: Minutes and seconds side by side
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = minutes,
                onValueChange = { input -> minutes = input.filter { it.isDigit() } },
                label = { Text(stringResource(R.string.label_mins))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = seconds,
                onValueChange = { input -> seconds = input.filter { it.isDigit() } },
                label = { Text(stringResource(R.string.label_secs))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text("Number of satellites:",
                style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Row 3: Number of satellites
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = numSatellites,
                onValueChange = { numSatellites = it },
                label = { Text(stringResource(R.string.label_number_of_satellites))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)

            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // -- CALCULATE TRIGGER --
        Button(
            onClick = {
                keyboardController?.hide()

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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.label_button_calculate))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Output Display ---

        AnimatedVisibility(
            visible = resonateOrbitResult != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2})
        ) {


            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                resonateOrbitResult?.let { result ->
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val lowOrbitLabel = String.format(
                            Locale.US,
                            "${result.numSatellites - 1}/${result.numSatellites} Orbit:"
                        )
                        val highOrbitLabel = String.format(
                            Locale.US,
                            "${result.numSatellites + 1}/${result.numSatellites} Orbit:"
                        )

                        ResultRow("Target Period:", result.desiredPeriod)
                        ResultRow(lowOrbitLabel, result.lowResonatePeriod)
                        ResultRow(highOrbitLabel, result.highResonatePeriod)
                    }

                }
            }
        }
    }
}

data class ResonateOrbitResult(
    val desiredPeriod: String,
    val lowResonatePeriod: String,
    val highResonatePeriod: String,
    val numSatellites: Int
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResonateOrbitCalcPreview() {
    RocketUtilsTheme {
        ResonateOrbitCalcScreen(onBackToMenu = {})
    }
}