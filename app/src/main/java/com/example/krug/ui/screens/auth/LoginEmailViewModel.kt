package com.example.krug.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.AuthResult
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _uiState = MutableStateFlow<LoginEmailUiState>(LoginEmailUiState.Idle)
    val uiState: StateFlow<LoginEmailUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun sendCode() {
        val currentEmail = _email.value
        // Валидация email перед отправкой
        if (currentEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
            _uiState.value = LoginEmailUiState.Error("Введите корректный email")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginEmailUiState.Loading
            val result = authRepository.requestCode(currentEmail)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = LoginEmailUiState.Idle
                    _navigationEvent.emit(currentEmail)
                }
                is AuthResult.Error -> {
                    _uiState.value = LoginEmailUiState.Error(result.message)
                }
            }
        }
    }

    fun resetError() {
        if (_uiState.value is LoginEmailUiState.Error) {
            _uiState.value = LoginEmailUiState.Idle
        }
    }
}

sealed class LoginEmailUiState {
    object Idle : LoginEmailUiState()
    object Loading : LoginEmailUiState()
    data class Error(val message: String) : LoginEmailUiState()
}