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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tourian.rocketutils.R
import com.tourian.rocketutils.ui.components.ResultRow
import com.tourian.rocketutils.ui.components.RocketEmoji
import com.tourian.rocketutils.ui.viewmodels.BurnTimeViewModel

@Composable
fun BurnTimeCalcScreen(
    onBackToMenu: () -> Unit,
    viewModel: BurnTimeViewModel = viewModel()
    ) {



    val keyboardController = LocalSoftwareKeyboardController.current

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

            Spacer(modifier = Modifier.weight(1f))

            RocketEmoji()

            Text(stringResource(R.string.title_burn_time_calc),
                style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enter Estimated/Predicted Burn time:")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input section
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = viewModel.days,
                onValueChange = { viewModel.onDaysChange(it) },
                label = { Text(stringResource(R.string.label_days)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = viewModel.hours,
                onValueChange = { viewModel.onHoursChange(it) },
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
                value = viewModel.minutes,
                onValueChange = { viewModel.onMinutesChange(it) },
                label = { Text(stringResource(R.string.label_mins))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = viewModel.seconds,
                onValueChange = { viewModel.onSecondsChange(it) },
                label = { Text( stringResource( R.string.label_secs))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Calculate Trigger ---
        Button(
            onClick = {
                keyboardController?.hide()
                viewModel.calculateBurn()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.label_button_calculate))
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = viewModel.burnResult != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {

            viewModel.burnResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResultRow("Start burn at:", result.seconds)
                        ResultRow("Formatted Time: ", result.time)
                    }
                }
            }

        }

//                keyboardController?.hide()
//
//                val d = days.toIntOrNull() ?: 0
//                val h = hours.toIntOrNull() ?: 0
//                val m = minutes.toIntOrNull() ?: 0
//                val s = seconds.toIntOrNull() ?: 0
//
//                val fullBurnTime = TimeHolder(d, h, m, s)
//                val fullBurnSeconds = fullBurnTime.toSeconds()
//                val burnStartSeconds = fullBurnSeconds / 2
//                val burnTime = TimeHolder.fromSeconds(burnStartSeconds)
//
//                val isOdd = fullBurnSeconds % 2 != 0
//                val halfSecDecimal = if (isOdd) ".5" else ""
//                val formattedSeconds =
//                    String.format(Locale.US,
//                        "%,d",
//                        burnStartSeconds) + halfSecDecimal + " seconds"
//
//
//                val rawTimeStr = burnTime.toFormattedString()
//                val formattedTime = if (isOdd) {
//                    rawTimeStr.dropLast(1) + ".5s"
//                } else {
//                    rawTimeStr
//                }
//
//
//                burnResult = BurnResult(
//                    seconds = formattedSeconds,
//                    time = formattedTime
//                )






//
    }
}

data class BurnResult(
    val seconds: String,
    val time: String
)

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun BurnTimeCalcPreview() {
//    RocketUtilsTheme{
//        BurnTimeCalcScreen(onBackToMenu = {}, viewModel = BurnTimeViewModel() )
//    }
//}