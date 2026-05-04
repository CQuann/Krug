package com.example.krug.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.TokenManager
import com.example.krug.data.local.UserIdManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.auth.VerifyResult
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
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val userIdManager: UserIdManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<VerifyCodeUiState>(VerifyCodeUiState.Idle)
    val uiState: StateFlow<VerifyCodeUiState> = _uiState.asStateFlow()

    private val _navigationEvent =
        MutableSharedFlow<VerifyNavigation>(
            replay = 0,
            extraBufferCapacity = 1
        )
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun verifyCode(email: String, code: String) {
        viewModelScope.launch {
            _uiState.value = VerifyCodeUiState.Loading
            val result = authRepository.verifyCode(email, code)
            when (result) {
                is DataResult.Success -> {
                    _uiState.value = VerifyCodeUiState.Idle
                    when (val verifyResult = result.data) {
                        is VerifyResult.LoginSuccess -> {
                            tokenManager.saveToken(verifyResult.token)
                            userIdManager.saveUserId(verifyResult.userId)
                            _navigationEvent.emit(VerifyNavigation.GoToMain)
                        }
                        is VerifyResult.RegisterNeeded -> {
                            _navigationEvent.emit(VerifyNavigation.GoToRegister(email))
                        }
                    }
                }
                is DataResult.Error -> {
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
    object GoToMain : VerifyNavigation()
    data class GoToRegister(val email: String) : VerifyNavigation()
}