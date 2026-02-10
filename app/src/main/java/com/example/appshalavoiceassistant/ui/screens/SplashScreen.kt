package com.example.appshalavoiceassistant.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appshalavoiceassistant.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000) // Wait for 3 seconds
        onTimeout() // Tell NavGraph to move to the next screen
    }
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF0B112B), Color(0xFF02040A)),
        radius = 2000f
    )

// Spotlight Effect: White to Transparent
    val whiteGlow = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f), // Soft white glow
            Color.Transparent               // Fades out
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient),
        contentAlignment = Alignment.Center // Centers perfectly both ways
    ) {
        // 1. Minimal Glow Layer
        Box(
            modifier = Modifier
                .size(100.dp) // Smaller size for "minimal" effect
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f), // Lower alpha for subtlety
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. The Logo Layer
        Image(
            painter = painterResource(R.drawable.appshala_logo),
            contentDescription = "Appshala Logo",
            modifier = Modifier.width(344.dp) // From your Figma properties
        )

    }
}