package com.tourian.rocketutils.ui

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
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme

@Composable
fun OrbitCalculatorScreen(onBackToMenu: () -> Unit) {
    // State variables for inputs
    var days by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    var numSatellites by remember { mutableStateOf( "" ) }

    val keyboardController = LocalSoftwareKeyboardController.current

    // State variable for the final calculation readout
    val defaultResultText = stringResource(R.string.default_result_text)
    var resultText by remember { mutableStateOf(defaultResultText) }

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
            Spacer(modifier = Modifier.width(16.dp))
            Text(stringResource(R.string.title_resonate_orbit_calculator),
                style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

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

                resultText = """
                    Target Period: ${input.toFormattedString()} 
                    
                    ${num+1}/${num} Ratio: ${highResonateOrbit.toFormattedString()}
                    ${num-1}/${num} Ratio: ${lowResonateOrbit.toFormattedString()}
                """.trimIndent()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.label_button_calculate))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Output Display ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = resultText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrbitCalcPreview() {
    RocketUtilsTheme {
        OrbitCalculatorScreen(onBackToMenu = {})
    }
}