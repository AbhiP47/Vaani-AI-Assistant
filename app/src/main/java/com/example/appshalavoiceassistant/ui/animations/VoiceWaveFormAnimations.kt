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

    // Increase bar count for a "Frequency" look (30-40 bars looks premium)
    val barCount = 32

    // Create a base animation for the movement
    val baseAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "base_movement"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp), // Increased height for better visual impact
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = 4.dp.toPx()
            val gap = 4.dp.toPx()

            val totalWidth = (barWidth * barCount) + (gap * (barCount - 1))
            val startX = (canvasWidth - totalWidth) / 2

            for (i in 0 until barCount) {
                // Use a sine wave to create a beautiful "mountain" shape
                val xPosition = i.toFloat() / barCount
                val sineValue = kotlin.math.sin((xPosition * Math.PI) + (baseAnim * Math.PI * 2)).toFloat()

                // Calculate height: If muted/hold, it's a flat line. If active, it's dynamic.
                val heightMultiplier = if (isMuted || isOnHold) 0.05f else (0.3f + 0.7f * kotlin.math.abs(sineValue))
                val barHeight = canvasHeight * heightMultiplier * kotlin.math.sin(xPosition * Math.PI).toFloat()

                val xOffset = startX + (i * (barWidth + gap))
                val yOffset = (canvasHeight - barHeight) / 2

                // Glow Effect: Draw a faint, wider line behind the main bar
                if (!isMuted && !isOnHold) {
                    drawRoundRect(
                        color = Color(0xFF9C27B0).copy(alpha = 0.3f),
                        topLeft = Offset(xOffset - 2.dp.toPx(), yOffset - 2.dp.toPx()),
                        size = Size(barWidth + 4.dp.toPx(), barHeight + 4.dp.toPx()),
                        cornerRadius = CornerRadius(barWidth, barWidth)
                    )
                }

                drawRoundRect(
                    color = when {
                        isOnHold -> Color.Gray
                        isMuted -> Color.Red.copy(alpha = 0.6f)
                        else -> Color(0xFF9C27B0) // Signature Purple
                    },
                    topLeft = Offset(xOffset, yOffset),
                    size = Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                    cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                )
            }
        }
    }
}