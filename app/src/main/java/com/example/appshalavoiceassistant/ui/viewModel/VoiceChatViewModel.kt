package com.example.appshalavoiceassistant.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ConnectionStatus {
    object Idle : ConnectionStatus()
    object Loading : ConnectionStatus()
    object Connected : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
}

class VoiceChatViewModel : ViewModel() {

    private val _connectionState =
        MutableStateFlow<ConnectionStatus>(ConnectionStatus.Idle)
    val connectionState = _connectionState.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted = _isMuted.asStateFlow()

    fun onConnecting() {
        _connectionState.value = ConnectionStatus.Loading
    }

    fun onConnected() {
        _connectionState.value = ConnectionStatus.Connected
    }

    fun onError(message: String) {
        _connectionState.value = ConnectionStatus.Error(message)
    }

    fun onStopped() {
        _connectionState.value = ConnectionStatus.Idle
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }
}
