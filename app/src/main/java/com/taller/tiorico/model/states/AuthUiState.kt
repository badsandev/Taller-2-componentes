package com.taller.tiorico.model.states

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val email: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
