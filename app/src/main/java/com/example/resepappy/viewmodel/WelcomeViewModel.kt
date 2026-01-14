package com.example.resepappy.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WelcomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<WelcomeUiState>(WelcomeUiState.Success)
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()
}

sealed interface WelcomeUiState {
    object Success : WelcomeUiState
}