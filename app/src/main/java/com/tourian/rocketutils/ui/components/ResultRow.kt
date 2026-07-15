package com.tourian.rocketutils.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

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