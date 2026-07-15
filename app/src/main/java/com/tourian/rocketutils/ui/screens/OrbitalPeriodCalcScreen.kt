package com.tourian.rocketutils.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourian.rocketutils.R
import com.tourian.rocketutils.objects.CelestialBody
import com.tourian.rocketutils.objects.ThousandsSeparatorTransformation
import com.tourian.rocketutils.objects.TimeHolder
import com.tourian.rocketutils.ui.components.RocketEmoji
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbitalPeriodCalcScreen(onBackToMenu: () -> Unit) {
    // Core states
    var altitudeInput by remember { mutableStateOf("") }
    var selectedBody by remember { mutableStateOf(CelestialBody.KERBIN)}
    var kilometers by remember { mutableStateOf(false) }

    // Tracks whether the dropdown list is currently popped open or closed
    var dropdownExpanded by remember { mutableStateOf(false) }
    val resultDefaultText = stringResource(R.string.period_calc_result_default_text)
    var resultText by remember { mutableStateOf(resultDefaultText) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Top navigation row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBackToMenu) {
                Text(stringResource(R.string.label_button_back))
            }

            Spacer(modifier = Modifier.weight(1f))
            RocketEmoji()
            Text(stringResource(R.string.label_orbital_period_calculator),
                style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // The parent body dropdown form
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedBody.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_parent_body))},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)},
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                // Dynamically loops through every single item inside the enum class
                CelestialBody.entries.forEach { body ->
                    DropdownMenuItem(
                        text = { Text(body.displayName)},
                        onClick = {
                            selectedBody = body         // Update state to the chosen planet
                            dropdownExpanded = false    // Close the drawer
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Altitude input field.
        OutlinedTextField(
            value = altitudeInput,
            onValueChange = { input -> altitudeInput = input.filter { it.isDigit() } },
            label = { Text(stringResource(R.string.label_period_calc_altitude))},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = ThousandsSeparatorTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = kilometers,
                    onValueChange = { kilometers = it },
                    role = Role.Switch // Tells accessibility tools this acts like a switch
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Input in Kilometers (km)?",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))
            Switch( checked = kilometers,
                onCheckedChange = null
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Calculate execution
        Button(
            onClick = {
                keyboardController?.hide()
                var alt = altitudeInput.toDoubleOrNull() ?: 0.0
                if (kilometers) {
                    alt *= 1000
                }

                val periodSeconds = selectedBody.calculateOrbitalPeriod(alt)

                val timeResult = TimeHolder.fromSeconds(periodSeconds.toInt())
                val secondsFormatted =
                    String.format(Locale.US, "%,.4f", periodSeconds)
                val altitudeFormatted =
                    String.format(Locale.US, "%,.0f", alt)

                resultText = """
                    Parent Body: ${selectedBody.displayName}
                    Apoapsis/Periapsis/Altitude: $altitudeFormatted meters
                    Calculated Period:
                    ${timeResult.toFormattedString()}
                    (${secondsFormatted} seconds)
                """.trimIndent()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Period")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Output display card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
fun OrbitalPeriodCalcScreenPreview() {
    RocketUtilsTheme{
        OrbitalPeriodCalcScreen(onBackToMenu = {})
    }
}