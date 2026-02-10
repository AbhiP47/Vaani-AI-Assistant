package com.example.appshalavoiceassistant.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appshalavoiceassistant.ui.screens.HomeScreen
import com.example.appshalavoiceassistant.ui.screens.SplashScreen
import com.example.appshalavoiceassistant.ui.screens.VoiceCallScreen
import com.example.appshalavoiceassistant.ui.viewModel.VoiceChatViewModel

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val VOICE_CALL = "voice_call"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

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
                    // ONLY navigation here
                    navController.popBackStack(
                        Routes.HOME,
                        inclusive = false
                    )
                }
            )
        }
    }
}
