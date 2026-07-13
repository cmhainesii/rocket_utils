package com.tourian.rocketutils.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourian.rocketutils.R
import com.tourian.rocketutils.ui.theme.RocketUtilsTheme

@Composable
fun MainMenuScreen(
    onNavigateToCalc: () -> Unit,
    onNavigateToBurnCalc: () -> Unit,
    onNavigateToPeriodCalc: () -> Unit,
) {
    Spacer(modifier = Modifier.height(24.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // First utility button!
        Button(
            onClick = onNavigateToCalc,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.application_title),
                style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToBurnCalc,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.button_label_calculate_burn_start,
            ), style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToPeriodCalc,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                stringResource(R.string.button_label_orbital_period_calculator),
                style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainMenuPreview() {
    RocketUtilsTheme {
        MainMenuScreen(
            onNavigateToCalc = {},
            onNavigateToBurnCalc = {},
            onNavigateToPeriodCalc = {})
    }
}


