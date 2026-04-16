package com.example.krug.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.TokenManager
import com.example.krug.data.model.AuthResult
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    fun checkAuth() {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (token == null) {
                _state.value = SplashState.NavigateToLogin
                return@launch
            }
            val result = authRepository.validateToken(token)
            if (result is AuthResult.Success && result.data) {
                _state.value = SplashState.NavigateToMain
            } else {
                tokenManager.clearToken()
                _state.value = SplashState.NavigateToLogin
            }
        }
    }
}

sealed class SplashState {
    object Loading : SplashState()
    object NavigateToMain : SplashState()
    object NavigateToLogin : SplashState()
}