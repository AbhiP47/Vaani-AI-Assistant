package com.example.appshalavoiceassistant.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appshalavoiceassistant.ui.animations.VoiceWaveformAnimation
import com.example.appshalavoiceassistant.ui.navigation.findActivity // Import the helper we made
import com.example.appshalavoiceassistant.ui.viewModel.ConnectionStatus
import com.example.appshalavoiceassistant.ui.viewModel.VoiceChatViewModel

@Composable
fun VoiceCallScreen(onEndCall: () -> Unit, viewModel: VoiceChatViewModel) {
    // --- 1. BRIDGING TO ACTIVITY & VIEWMODEL ---
    val context = LocalContext.current
    val activity = context.findActivity()
    val haptic = LocalHapticFeedback.current

    // Observe the ViewModel flows
    val connectionStatus by viewModel.connectionState.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    // Local UI states
    val isOnHold = connectionStatus is ConnectionStatus.Hold
    var captionText by remember { mutableStateOf("वाणी आपकी बात सुनने के लिए तैयार है...") }
    var isAiSpeaking by remember { mutableStateOf(false) }

    // --- 2. AUTO-START BACKEND ---
    LaunchedEffect(Unit) {
        activity?.startVoiceBackend(viewModel)
    }

    val backgroundGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF1A1033), Color(0xFF02040A))
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Heading
            Text(
                text = when {
                    isOnHold -> "होल्ड पर है (On Hold)"
                    isMuted -> "आवाज बंद है (Muted)"
                    connectionStatus is ConnectionStatus.Loading -> "कनेक्ट हो रहा है..."
                    else -> "VAANI सक्रिय है"
                },
                color = when {
                    isOnHold -> Color.Yellow
                    isMuted -> Color.Red
                    else -> Color.White
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 60.dp).graphicsLayer(alpha = 0.8f)
            )

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                VoiceWaveformAnimation(isMuted = isMuted, isOnHold = isOnHold)
            }

            // --- GLASS BOX ---
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp)
                    .shadow(30.dp, RoundedCornerShape(28.dp), ambientColor = Color.Magenta),
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(
                            if (isAiSpeaking) Color(0xFFD481FF) else Color(0xFF00E5FF), CircleShape
                        ).blur(4.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = if (isAiSpeaking) "वाणी (VAANI)" else "आप (You)",
                            color = if (isAiSpeaking) Color(0xFFD481FF) else Color(0xFF00E5FF),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isMuted) "No-one Speaking..." else if (isOnHold) "Paused!..." else captionText,
                        color = Color.White,
                        fontSize = 18.sp,
                        lineHeight = 26.sp
                    )
                }
            }

            // --- CONTROL ROW ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 70.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute/Unmute Button
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (isMuted) activity?.unmuteVoice(viewModel) else activity?.muteVoice(viewModel)
                    },
                    modifier = Modifier.size(60.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, null, tint = Color.White)
                }

                Spacer(modifier = Modifier.width(30.dp))

                // Hold/Resume Button
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (isOnHold) activity?.resumeVoice(viewModel) else activity?.holdVoice(viewModel)
                    },
                    modifier = Modifier.size(72.dp).background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(if (isOnHold) Icons.Default.PlayArrow else Icons.Default.Pause, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.width(30.dp))

                // End Call Button
                FloatingActionButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        activity?.stopVoiceBackend(viewModel)
                        onEndCall()
                    },
                    containerColor = Color(0xFFE91E63),
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(Icons.Default.CallEnd, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}