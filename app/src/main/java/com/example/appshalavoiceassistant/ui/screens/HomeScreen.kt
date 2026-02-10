package com.example.appshalavoiceassistant.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appshalavoiceassistant.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(onStartCall: () -> Unit) {
    val context = LocalContext.current
    val micPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.RECORD_AUDIO
    )

    // --- REMOVED THE LAUNCHEDEFFECT AUTO-TRIGGER ---
    // This was the loop: Permission Granted -> Auto-Start Call -> End Call -> Home -> Auto-Start Call.

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF02040A))) {
        Image(
            painter = painterResource(id = R.drawable.homescreen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "नमस्ते! मैं 'वाणी' हूँ। मैं आपकी क्या मदद कर सकती हूँ? \uD83D\uDE0A ",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                GlowEffect(color = Color(0xFF9C27B0))

                Button(
                    onClick = {
                        // THE FIX: Navigation only happens here when the user MANUALLY clicks
                        when {
                            micPermissionState.status.isGranted -> onStartCall()
                            !micPermissionState.status.shouldShowRationale && !micPermissionState.status.isGranted -> {
                                val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = android.net.Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            }
                            else -> micPermissionState.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .height(64.dp)
                        .scale(pulseScale),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
                ) {
                    val buttonText = when {
                        micPermissionState.status.isGranted -> "शुरू करें"
                        !micPermissionState.status.shouldShowRationale -> "सेटिंग्स खोलें"
                        else -> "अनुमति दें"
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(buttonText, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun GlowEffect(color: Color) {
    Box(
        modifier = Modifier
            .size(width = 250.dp, height = 90.dp)
            .blur(radius = 30.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.5f), Color.Transparent)
                ),
                shape = RoundedCornerShape(45.dp)
            )
    )
}