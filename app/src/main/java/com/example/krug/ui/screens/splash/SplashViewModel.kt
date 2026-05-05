package com.example.krug.ui.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigation>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun checkAuth() {
        viewModelScope.launch {
            sessionManager.saveToken("123")
            val token = sessionManager.getToken()
            Log.d("Splash", "Token: $token")
            if (token == null) {
                Log.d("Splash", "No token, go to login")
                _navigationEvent.emit(SplashNavigation.GoToLogin)
                return@launch
            }
            _navigationEvent.emit(SplashNavigation.GoToMain)
//            Log.d("Splash", "Calling validateToken...")
//            val result = authRepository.validateToken()
//            Log.d("Splash", "Result: $result")
//            if (result is DataResult.Success && result.data) {
//                _navigationEvent.emit(SplashNavigation.GoToMain)
//            } else {
//                sessionManager.clearToken()
//                _navigationEvent.emit(SplashNavigation.GoToLogin)
//            }
        }
    }
}

sealed class SplashNavigation {
    object GoToMain : SplashNavigation()
    object GoToLogin : SplashNavigation()
}