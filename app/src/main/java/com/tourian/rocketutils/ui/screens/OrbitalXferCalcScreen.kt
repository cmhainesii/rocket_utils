    package com.tourian.rocketutils.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.tourian.rocketutils.ui.viewmodels.OrbitalXferViewModel
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbitalXferCalcScreenContent(
    onBackToMenu: () -> Unit,
    selectedBody: CelestialBody,
    onSelectedBodyChanged: (CelestialBody) -> Unit,
    initialAltitude: String,
    onInitialAltitudeChanged: (String) -> Unit,
    targetAltitude: String,
    onTargetAltitudeChanged: (String) -> Unit,
    isKilometers: Boolean,
    onKilometersChanged: (Boolean) -> Unit,
    orbitalXferResult: OrbitalXferResult?,
    calculateResult: () -> Unit,
    onSaveMission: (String) -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Compute live radii for the canvas visualization
    val initialAltMeters = (initialAltitude.toDoubleOrNull() ?: 0.0) * if (isKilometers) 1000.0 else 1.0
    val targetAltMeters = (targetAltitude.toDoubleOrNull() ?: 0.0) * if (isKilometers) 1000.0 else 1.0

    val bodyRadius = selectedBody.radiusMeters
    val r1Meters = bodyRadius + initialAltMeters
    val r2Meters = bodyRadius + targetAltMeters

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var missionNameInput by remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
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

                Spacer(modifier = Modifier.weight(1f))
                RocketEmoji()

                Text(
                    stringResource(R.string.heading_orbital_xfer),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Orbit Canvas placed directly inside the Column
            OrbitalTransferCanvas(
                bodyRadiusMeters = bodyRadius,
                r1Meters = r1Meters,
                r2Meters = r2Meters,
                selectedBody = selectedBody,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    stringResource(R.string.form_description_orbital_period),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dropdown: Select Parent Body
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedBody.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_parent_body)) },
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
                            text = { Text(body.displayName) },
                            onClick = {
                                onSelectedBodyChanged(body)
                                dropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch: Kilometers Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = isKilometers,
                        onValueChange = { onKilometersChanged(it) },
                        role = Role.Switch
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_kilometers_switch),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isKilometers,
                    onCheckedChange = null
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Initial Altitude
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = initialAltitude,
                    onValueChange = { onInitialAltitudeChanged(it) },
                    label = { Text(stringResource(R.string.label_intial_altitude)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = ThousandsSeparatorTransformation(),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(4.dp))

                val unitLabel = if (isKilometers) "km" else "m"
                Text(
                    unitLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Target Altitude
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = targetAltitude,
                    onValueChange = { onTargetAltitudeChanged(it) },
                    label = { Text(stringResource(R.string.label_target_altitude)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = ThousandsSeparatorTransformation(),
                    modifier = Modifier.weight(1f)
                )

                val unitLabel = if (isKilometers) "km" else "m"
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    unitLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    calculateResult()

                    coroutineScope.launch {
                        kotlinx.coroutines.delay(100.milliseconds)
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            ) {
                Text(stringResource(R.string.label_button_calculate))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Result Card
            AnimatedVisibility(
                visible = orbitalXferResult != null,
                enter = fadeIn(animationSpec = tween(850, 150)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(850, 150)
                        )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    orbitalXferResult?.let { result ->
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ResultRow("Injection Burn:", result.dv1)
                            ResultRow("Circularization Burn:", result.dv2)
                            Spacer(modifier = Modifier.height(4.dp))
                            ResultRow("Total dV Required:", result.total)

                            Spacer(modifier = Modifier.height(8.dp))

                            // Save button
                            Button(
                                onClick = { onSaveMission("My KSP Mission")
                                          showSaveDialog = true},
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Save Mission")
                            }

                        }
                    }
                }
            }

        }
        if (showSaveDialog) {
            BasicAlertDialog(
                onDismissRequest = { showSaveDialog = false }
            ) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Save Mission Plan",
                            style = MaterialTheme.typography.headlineSmall)

                        OutlinedTextField(
                            value = missionNameInput,
                            onValueChange = { missionNameInput = it },
                            label = { Text("Mission Name")},
                            placeholder = {
                                Text("e.g. ${selectedBody.displayName} Transfer Alpha") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    showSaveDialog = false
                                    missionNameInput = ""
                                }
                            ) {
                                Text("Cancel")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    val finalName = missionNameInput.ifBlank { "${selectedBody.displayName} Transfer" }
                                    onSaveMission(finalName)
                                    showSaveDialog = false
                                    missionNameInput = ""

                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar("Saved '$finalName' to Mission Log!")
                                    }
                                }
                            ) { Text("Save") }
                        }
                    }
                }
            }
        }

    }


}

data class OrbitalXferResult(
    val dv1: String,
    val dv2: String,
    val total: String,
)

@Composable
fun OrbitalXferCalcScreen(
    onBackToMenu: () -> Unit,
    viewModel: OrbitalXferViewModel = viewModel()
) {
    OrbitalXferCalcScreenContent(
        onBackToMenu = onBackToMenu,
        selectedBody = viewModel.selectedBody,
        onSelectedBodyChanged = { viewModel.onChangeSelectedBody(it) },
        initialAltitude = viewModel.initialAltitude,
        onInitialAltitudeChanged = { viewModel.onInitialAltitudeChange(it) },
        targetAltitude = viewModel.targetAltitude,
        onTargetAltitudeChanged = { viewModel.onTargetAltitudeChange(it) },
        isKilometers = viewModel.isKilometers,
        onKilometersChanged = { viewModel.isKilometers(it) },
        orbitalXferResult = viewModel.orbitalXferResult,
        calculateResult = { viewModel.calculateResult() },
        onSaveMission = { name -> viewModel.saveMission(name)}
    )
}

@Composable
fun OrbitalTransferCanvas(
    bodyRadiusMeters: Double,
    r1Meters: Double,
    r2Meters: Double,
    selectedBody: CelestialBody,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "TransferAnimation")
    val animationProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShipProgress"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color(0xFF0B0E14))
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)

        // 85% boundary so orbits don't clip the canvas edges
        val maxCanvasRadius = (size.minDimension / 2f) * 0.85f

        // Scale factor relative to largest radius
        val maxOrbitMeters = maxOf(r1Meters, r2Meters).coerceAtLeast(bodyRadiusMeters * 1.1)
        val scale = maxCanvasRadius / maxOrbitMeters.toFloat()

        // Pixel dimensions
        val bodyPixelRadius = (bodyRadiusMeters * scale).toFloat().coerceAtLeast(12.dp.toPx())
        val r1Px = (r1Meters * scale).toFloat()
        val r2Px = (r2Meters * scale).toFloat()

        // 1. Central Body
        drawCircle(
            color = selectedBody.color,
            radius = bodyPixelRadius,
            center = center
        )

        // 2. Initial Orbit (r1)
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.5f),
            radius = r1Px,
            center = center,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
            )
        )

        // 3. Target Orbit (r2)
        drawCircle(
            color = Color.Magenta.copy(alpha = 0.5f),
            radius = r2Px,
            center = center,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
            )
        )

        // 4. Hohmann Transfer Ellipse Geometry
        val semiMajorPx = (r1Px + r2Px) / 2f
        val semiMinorPx = kotlin.math.sqrt(r1Px * r2Px)
        val ellipseOffsetX = (r2Px - r1Px) / 2f
        val ellipseCenter = Offset(center.x - ellipseOffsetX, center.y)

        // Proper bounding box for the ellipse (left, top, right, bottom)
        val ellipseBoundingRect = Rect(
            left = ellipseCenter.x - semiMajorPx,
            top = ellipseCenter.y - semiMinorPx,
            right = ellipseCenter.x + semiMajorPx,
            bottom = ellipseCenter.y + semiMinorPx
        )

        drawPath(
            path = Path().apply {
                addOval(ellipseBoundingRect)
            },
            color = Color(0xFF10B981),
            style = Stroke(width = 3.dp.toPx())
        )

        // 5. Spacecraft Dot
        val angle = animationProgress * 2 * Math.PI
        val shipX = ellipseCenter.x + semiMajorPx * kotlin.math.cos(angle).toFloat()
        val shipY = ellipseCenter.y + semiMinorPx * kotlin.math.sin(angle).toFloat()

        drawCircle(
            color = Color.White,
            radius = 5.dp.toPx(),
            center = Offset(shipX, shipY)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 36)
@Composable
fun OrbitalXferPreview() {
    RocketUtilsTheme {
        OrbitalXferCalcScreenContent (
            onBackToMenu = {},
            CelestialBody.MINMUS,
            onSelectedBodyChanged = {},
            "250000",
            onInitialAltitudeChanged = {},
            "960000",
            onTargetAltitudeChanged = {},
            isKilometers = false,
            onKilometersChanged = {},
            orbitalXferResult = OrbitalXferResult("280.90 m/s", "240.92 m/s",
                "521.82 m/s"),
            calculateResult = {},
            onSaveMission = {},
        )

    }
}