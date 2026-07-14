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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourian.rocketutils.R
import com.tourian.rocketutils.objects.CelestialBody
import com.tourian.rocketutils.objects.ThousandsSeparatorTransformation
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbitalXferCalculator(onBackToMenu: () -> Unit ) {

    var initialAltitude by remember { mutableStateOf("") }
    var targetAltitude by remember { mutableStateOf("") }
    var selectedBody by remember { mutableStateOf(CelestialBody.KERBIN)}
    var dropdownExpanded by remember { mutableStateOf(false) }

    var calculationResult by remember { mutableStateOf<CalculationResult?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Top row - Title & Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBackToMenu) {
                Text(stringResource(R.string.label_button_back))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(stringResource(R.string.heading_orbital_xfer),
                style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Input fields
        // Select parent body
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
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                },
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
                CelestialBody.entries.forEach { body ->
                    DropdownMenuItem(
                        text = { Text(body.displayName)},
                        onClick = {
                            selectedBody = body
                            dropdownExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = initialAltitude,
            onValueChange = { input -> initialAltitude = input.filter { it.isDigit() }},
            label = {Text(stringResource(R.string.label_intial_altitude))},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = ThousandsSeparatorTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = targetAltitude,
            onValueChange = { input -> targetAltitude = input.filter { it.isDigit() }},
            label = { Text(stringResource(R.string.label_target_altitude))},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = ThousandsSeparatorTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                keyboardController?.hide()
                val initialAlt = initialAltitude.toDoubleOrNull() ?: 0.0
                val targetAlt = targetAltitude.toDoubleOrNull() ?: 0.0


                val r1 = selectedBody.radiusMeters + initialAlt
                val r2 = selectedBody.radiusMeters + targetAlt
                val a_tx = (r1 + r2) /  2.0 // Semi-major axis

                val v1 = sqrt(selectedBody.mu / r1)
                val vtx1 =
                    sqrt(
                        selectedBody.mu * (
                                (2 / r1) - (1 / a_tx)
                                )
                    )
                val dv1 = abs(vtx1 - v1)

                val vtx2 =
                    sqrt(
                        selectedBody.mu * (
                                (2 / r2) - (1 / a_tx)
                                )
                    )
                val v2 = sqrt(selectedBody.mu / r2)
                val dv2 = abs(vtx2 - v2)
                val totalDv = dv1 + dv2

                val dv1Formatted = String.format(Locale.US, "%,.2f", dv1)
                val dv2Formatted = String.format(Locale.US, "%,.2f", dv2)
                val totalDvFormatted = String.format(Locale.US, "%,.2f", totalDv)

                calculationResult = CalculationResult(
                    dv1 = "$dv1Formatted m/s",
                    dv2 = "$dv2Formatted m/s",
                    total = "$totalDvFormatted m/s"
                )

            }
        ) {
            Text(stringResource(R.string.label_button_calculate))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val result = calculationResult
            if (result == null) {
                Text("<Insert Intelligent Comment Here>",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge)
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultRow("Injection Burn:", result.dv1)
                    ResultRow("Circularization Burn:", result.dv2)
                    Spacer(modifier = Modifier.height(4.dp))
                    ResultRow("Total dV Required:", result.total)
                }
            }
        }


    }
}

@Composable
fun ResultRow(label: String, value: String, isBold: Boolean = false) {
    val weight = if (isBold) FontWeight.Bold else FontWeight.Normal

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.weight(1.2f), // Adjust weights to change column widths
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = weight
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = weight
        )
    }
}

data class CalculationResult(
    val dv1: String,
    val dv2: String,
    val total: String,
)


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrbitalXferPreview() {
    RocketUtilsTheme {
        OrbitalXferCalculator(onBackToMenu = {})
    }
}