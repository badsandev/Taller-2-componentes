package com.taller.tiorico.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taller.tiorico.model.states.AuthUiState
import com.taller.tiorico.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Campos vacíos")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.login(email, pass)

            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success(email)
                _eventFlow.emit(UiEvent.NavigateToHome)
            } else {
                _uiState.value = AuthUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error desconocido"
                )
                _eventFlow.emit(UiEvent.ShowSnackbar("Error al iniciar sesión"))
            }
        }
    }

    fun register(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Campos vacíos")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.register(email, pass)

            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success(email)
                _eventFlow.emit(UiEvent.NavigateToHome)
            } else {
                _uiState.value = AuthUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al registrar"
                )
                _eventFlow.emit(UiEvent.ShowSnackbar("Error al registrar usuario"))
            }
        }
    }

    sealed class UiEvent {
        object NavigateToHome : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
