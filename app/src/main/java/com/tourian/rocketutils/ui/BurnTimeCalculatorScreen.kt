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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourian.rocketutils.R
import com.tourian.rocketutils.objects.TimeHolder
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme
import java.util.Locale

@Composable
fun BurnTimeCalculatorScreen(onBackToMenu: () -> Unit) {

    var days by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    val defaultResultText = stringResource(R.string.default_text_full_burn_calc)
    var resultText by remember { mutableStateOf(defaultResultText)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))


        // Top row with back button and title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBackToMenu) {
                Text(stringResource(R.string.label_button_back))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(stringResource(R.string.title_burn_time_calc),
                style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Input section
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = days,
                onValueChange = { input -> days = input.filter { it.isDigit() }},
                label = { Text(stringResource(R.string.label_days)) },
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                label = { Text( stringResource( R.string.label_secs))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Calculate Trigger ---
        Button(
            onClick = {

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
                val formattedSecondsLine =
                    String.format(Locale.US,
                        "%,d",
                        burnStartSeconds) + halfSecDecimal

                val rawTimeStr = burnTime.toFormattedString()
                val formattedTimeLine = if (isOdd) {
                    rawTimeStr.dropLast(1) + ".5s"
                } else {
                    rawTimeStr
                }


                resultText = """
                    Start Burn At: T - $formattedSecondsLine seconds
                    $formattedTimeLine
                """.trimIndent()
                //resultText = "Start burn at: T - $burnStartSeconds seconds"



            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Burn Start Time")
        }

        Spacer(modifier = Modifier.height(24.dp))

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
fun BurnTimeCalcPreview() {
    RocketUtilsTheme{
        BurnTimeCalculatorScreen(onBackToMenu = {})
    }
}