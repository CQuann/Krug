package com.example.krug.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.TokenManager
import com.example.krug.data.model.auth.AuthResult
import com.example.krug.data.model.UserData
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainAppViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = tokenManager.getToken()
            if (token == null) {
                _error.value = "Не авторизован"
                _isLoading.value = false
                return@launch
            }
            val result = authRepository.getUserData(token)
            when (result) {
                is AuthResult.Success -> {
                    _userData.value = result.data
                }
                is AuthResult.Error -> {
                    _error.value = result.message
                }
            }
            _isLoading.value = false
        }
    }
}