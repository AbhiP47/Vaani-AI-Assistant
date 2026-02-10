package com.example.appshalavoiceassistant

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.appshalavoiceassistant.ui.navigation.NavGraph
import com.example.appshalavoiceassistant.ui.theme.AppShalaVoiceAssistantTheme
import com.example.appshalavoiceassistant.ui.viewModel.VoiceChatViewModel
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.AudioTranscriptionConfig
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.LiveSession
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.liveGenerationConfig
import kotlinx.coroutines.launch

@OptIn(PublicPreviewAPI::class)
class MainActivity : ComponentActivity() {

    private var liveSession: LiveSession? = null

    private val liveModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-2.5-flash-native-audio-preview-12-2025",
            systemInstruction = content {
                text(
                    "You are Vaani, a friendly Indian AI assistant. " +
                            "Respond in the speaker's language."
                )
            },
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(
                    voice = Voice("KORE")
                )
                inputAudioTranscription = AudioTranscriptionConfig()
                outputAudioTranscription = AudioTranscriptionConfig()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()

        setContent {
            AppShalaVoiceAssistantTheme {
                NavGraph()
            }
        }
    }

    // CALLED FROM VoiceCallScreen
    fun startVoiceBackend(viewModel: VoiceChatViewModel) {
        lifecycleScope.launch {
            try {
                viewModel.onConnecting()

                if (liveSession == null) {
                    liveSession = liveModel.connect()
                }

                liveSession?.startAudioConversation()
                viewModel.onConnected()

            } catch (e: SecurityException) {
                viewModel.onError("Microphone permission error")
            } catch (e: Exception) {
                viewModel.onError(e.message ?: "Backend connection failed")
            }
        }
    }

    fun stopVoiceBackend(viewModel: VoiceChatViewModel) {
        lifecycleScope.launch {
            try {
                liveSession?.stopAudioConversation()
            } finally {
                liveSession = null
                viewModel.onStopped()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        liveSession?.stopAudioConversation()
        liveSession = null
    }
}
