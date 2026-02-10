package com.example.appshalavoiceassistant.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VoiceWaveformAnimation(isMuted: Boolean, isOnHold: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val barCount = 5
    val animations = List(barCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            // If EITHER state is active, targetValue stays low (no movement)
            targetValue = if (isMuted || isOnHold) 0.2f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 400 + (index * 100),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = 15.dp.toPx()
            val gap = 10.dp.toPx()
            val totalWidth = (barWidth * barCount) + (gap * (barCount - 1))
            val startX = (canvasWidth - totalWidth) / 2

            animations.forEachIndexed { index, animValue ->
                // Flatten the bars if muted or on hold
                val currentBarHeight = if (isMuted || isOnHold) 10.dp.toPx() else canvasHeight * animValue.value
                val xOffset = startX + (index * (barWidth + gap))
                val yOffset = (canvasHeight - currentBarHeight) / 2

                drawRoundRect(
                    color = when {
                        isOnHold -> Color.Gray
                        isMuted -> Color(0xFF9C27B0) // Purple
                        else -> Color(0xFF9C27B0)
                    },
                    topLeft = Offset(xOffset, yOffset),
                    size = Size(barWidth, currentBarHeight),
                    cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                )
            }
        }
    }
}