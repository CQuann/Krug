package com.example.krug.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.AuthResult
import com.example.krug.data.model.VerifyResult
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
class VerifyCodeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VerifyCodeUiState>(VerifyCodeUiState.Idle)
    val uiState: StateFlow<VerifyCodeUiState> = _uiState.asStateFlow()

    // События навигации: либо на главный, либо на регистрацию
    private val _navigationEvent = MutableSharedFlow<VerifyNavigation>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun verifyCode(email: String, code: String) {
        viewModelScope.launch {
            _uiState.value = VerifyCodeUiState.Loading
            val result = authRepository.verifyCode(email, code)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = VerifyCodeUiState.Idle
                    when (val verifyResult = result.data) {
                        is VerifyResult.LoginSuccess -> {
                            // TODO: Сохраняем токен (пока просто событие, сохранение сделаем позже)
                            _navigationEvent.emit(VerifyNavigation.GoToMain(verifyResult.token))
                        }
                        is VerifyResult.RegisterNeeded -> {
                            _navigationEvent.emit(VerifyNavigation.GoToRegister(email, verifyResult.tempToken))
                        }
                    }
                }
                is AuthResult.Error -> {
                    _uiState.value = VerifyCodeUiState.Error(result.message)
                }
            }
        }
    }

    fun resetError() {
        if (_uiState.value is VerifyCodeUiState.Error) {
            _uiState.value = VerifyCodeUiState.Idle
        }
    }
}

sealed class VerifyCodeUiState {
    object Idle : VerifyCodeUiState()
    object Loading : VerifyCodeUiState()
    data class Error(val message: String) : VerifyCodeUiState()
}

sealed class VerifyNavigation {
    data class GoToMain(val token: String) : VerifyNavigation()
    data class GoToRegister(val email: String, val tempToken: String) : VerifyNavigation()
}