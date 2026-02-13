package com.example.appshalavoiceassistant.ui.navigation

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appshalavoiceassistant.MainActivity
import com.example.appshalavoiceassistant.ui.screens.HomeScreen
import com.example.appshalavoiceassistant.ui.screens.SplashScreen
import com.example.appshalavoiceassistant.ui.screens.VoiceCallScreen
import com.example.appshalavoiceassistant.ui.viewModel.VoiceChatViewModel

// Helper to find the MainActivity from the current context
fun Context.findActivity(): MainActivity? = when (this) {
    is MainActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val VOICE_CALL = "voice_call"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context.findActivity()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(onTimeout = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(onStartCall = {
                navController.navigate(Routes.VOICE_CALL) {
                    launchSingleTop = true
                    restoreState = true
                }
            })
        }

        composable(Routes.VOICE_CALL) {
            val voiceViewModel: VoiceChatViewModel = viewModel()

            VoiceCallScreen(
                viewModel = voiceViewModel,
                onEndCall = {
                    // 1. Tell the backend to stop
                    activity?.stopVoiceBackend(voiceViewModel)
                    // 2. Navigate back
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }
    }
}