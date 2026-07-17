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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tourian.rocketutils.R
import com.tourian.rocketutils.objects.CelestialBody
import com.tourian.rocketutils.objects.ThousandsSeparatorTransformation
import com.tourian.rocketutils.ui.components.ResultRow
import com.tourian.rocketutils.ui.components.RocketEmoji
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme
import com.tourian.rocketutils.ui.viewmodels.OrbitalPeriodViewModel




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbitalPeriodCalcContent(
    selectedPlanetaryBody: CelestialBody,
    altitude: String,
    kilometers: Boolean,
    onBodySelected: (CelestialBody) -> Unit,
    onAltitudeEntered: (String) -> Unit,
    onKilometersToggle: (Boolean) -> Unit,
    onCalculateButtonPressed: () -> Unit,
    orbitalPeriodResult: OrbitalPeriodResult?,
    onBackToMenu: () -> Unit,

    ) {


    // Tracks whether the dropdown list is currently popped open or closed
    var dropdownExpanded by remember { mutableStateOf(false) }



    val keyboardController = LocalSoftwareKeyboardController .current

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16 .dp))
            Text(
                stringResource(R.string.form_description_orbital_period),
                style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))



        // The parent body dropdown form
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = selectedPlanetaryBody.displayName,
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
                        ).weight(1f)


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
                                onBodySelected(body)         // Update state to the chosen planet
                                dropdownExpanded = false    // Close the drawer
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = altitude,
                    onValueChange = { onAltitudeEntered(it) },
                label = { Text(stringResource(R.string.label_period_calc_altitude))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = ThousandsSeparatorTransformation(),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.padding(4.dp))


            val unitLabel = if (kilometers) {
                "km"
            } else {
                "m"
            }

            Text(unitLabel,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp))


        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = kilometers,
                    onValueChange = { onKilometersToggle(it) },
                    role = Role.Switch // Tells accessibility tools this acts like a switch
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.label_kilometers_switch),
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
                onCalculateButtonPressed()

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Period")
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = orbitalPeriodResult != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
            {
                orbitalPeriodResult?.let { result ->
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResultRow("Orbital Period:", result.time)
                        Spacer(modifier = Modifier.height(2.dp))
                        ResultRow("", result.seconds)
                    }
                }
            }
        }

    }

}


data class OrbitalPeriodResult(
    val seconds: String,
    val time: String
)

@Composable
fun OrbitalPeriodCalcScreen(
    onBackToMenu: () -> Unit,
    viewModel: OrbitalPeriodViewModel = viewModel()
) {
    OrbitalPeriodCalcContent(
        selectedPlanetaryBody = viewModel.selectedPlanetaryBody,
        altitude = viewModel.altitude,
        kilometers = viewModel.kilometers,
        onBodySelected = { body -> viewModel.onSelectedPlanetaryBodyChange(body) },
        onAltitudeEntered = { alt -> viewModel.onAltitudeChange(alt)},
        onKilometersToggle = { newState -> viewModel.onKilometersChange(newState)},
        onCalculateButtonPressed = { viewModel.calculateOrbitalPeriod() },
        orbitalPeriodResult = viewModel.orbitalPeriod,
        onBackToMenu = onBackToMenu
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrbitalPeriodCalcScreenPreview() {
    RocketUtilsTheme {
        OrbitalPeriodCalcContent(
            CelestialBody.IKE,
            "150",
            true,
            {},
            {},
            {},
            {},
            OrbitalPeriodResult("420 seconds", "69d 2h 21m 16s")
        ) { }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun OrbitalPeriodCalcScreenPreview() {
//    RocketUtilsTheme{
//        OrbitalPeriodCalcScreen(onBackToMenu = {}, null)
//    }
//}