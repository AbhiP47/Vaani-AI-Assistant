package com.example.appshalavoiceassistant.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appshalavoiceassistant.MainActivity
import com.example.appshalavoiceassistant.ui.animations.VoiceWaveformAnimation
import com.example.appshalavoiceassistant.ui.viewModel.ConnectionStatus
import com.example.appshalavoiceassistant.ui.viewModel.VoiceChatViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun VoiceCallScreen(
    viewModel: VoiceChatViewModel,
    onEndCall: () -> Unit
) {
    val activity = LocalContext.current as MainActivity

    val isMuted by viewModel.isMuted.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    var isOnHold by remember { mutableStateOf(false) }

    // 🔥 START BACKEND WHEN SCREEN OPENS (ONCE)
    LaunchedEffect(Unit) {
        activity.startVoiceBackend(viewModel)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF02040A)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // STATUS TEXT
        Text(
            text = when (connectionState) {
                is ConnectionStatus.Connected -> "VAANI सफलतापूर्वक शुरू हो गई है ✅"
                is ConnectionStatus.Loading -> "कनेक्ट हो रहा है..."
                is ConnectionStatus.Error ->
                    (connectionState as ConnectionStatus.Error).message
                else -> "तैयार (Ready)"
            },
            color = if (isMuted) Color.Red else Color.White,
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 60.dp)
        )

        // WAVEFORM
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            VoiceWaveformAnimation(
                isMuted = isMuted,
                isOnHold = isOnHold
            )
        }

        // CONTROLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // MUTE BUTTON
            IconButton(
                onClick = { viewModel.toggleMute() },
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isMuted)
                        Icons.Default.MicOff
                    else
                        Icons.Default.Mic,
                    contentDescription = "Mute",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // HOLD BUTTON (UI ONLY)
            IconButton(
                onClick = { isOnHold = !isOnHold },
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isOnHold)
                        Icons.Default.PlayArrow
                    else
                        Icons.Default.Pause,
                    contentDescription = "Hold",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // END CALL BUTTON
            FloatingActionButton(
                modifier = Modifier.size(60.dp),
                containerColor = Color(0xFF9C27B0),
                shape = CircleShape,
                onClick = {
                    activity.stopVoiceBackend(viewModel)
                    onEndCall()
                }
            ) {
                Icon(
                    Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White
                )
            }
        }
    }
}
