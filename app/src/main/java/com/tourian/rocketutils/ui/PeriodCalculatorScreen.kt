package com.tourian.rocketutils.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourian.rocketutils.R
import com.tourian.rocketutils.objects.CelestialBody
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodCalculatorScreen(onBackToMenu: () -> Unit) {
    // Core states
    var altitudeInput by remember { mutableStateOf("") }
    var selectedBody by remember { mutableStateOf(CelestialBody.KERBIN)}

    // Tracks whether the dropdown list is currently popped open or closed
    var dropdownExpanded by remember { mutableStateOf(false) }
    val resultDefaultText = stringResource(R.string.period_calc_result_default_text)
    var resultText by remember { mutableStateOf(resultDefaultText) }

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
            Spacer(modifier = Modifier.width(16.dp))
            Text(stringResource(R.string.label_orbital_period_calculator))
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
                label = { Text("Parent Body")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)},
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
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
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PeriodCalculatorScreenPreview() {
    RocketUtilsTheme{
        PeriodCalculatorScreen(onBackToMenu = {})
    }
}