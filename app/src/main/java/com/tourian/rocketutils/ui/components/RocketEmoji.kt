package com.tourian.rocketutils.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RocketEmoji(modifier: Modifier = Modifier) {

    // Infinite drifting animation
    val infiniteTransition = rememberInfiniteTransition(label = "rocket_bob")

    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f, // How high it floats up
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    Text(
        text = "🚀",
        fontSize = 28.sp,
        modifier = modifier
            .offset(y = bobbingOffset.dp) // Drifts up and down

    )
    Spacer(modifier = Modifier.width(24.dp))
}