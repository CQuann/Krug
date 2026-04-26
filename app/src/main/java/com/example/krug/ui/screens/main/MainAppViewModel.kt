package com.example.krug.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.TokenManager
import com.example.krug.data.local.UserIdManager
import com.example.krug.data.model.auth.AuthResult
import com.example.krug.data.model.UserData
import com.example.krug.data.model.UserDataResponse
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainAppViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val userIdManager: UserIdManager
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            _userId.value = userIdManager.getUserId()
            loadUserData()
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            val token = tokenManager.getToken()
            if (token == null) {
                _isLoading.value = false
                return@launch
            }
            val result = authRepository.getUserData(token)
            when (result) {
                is AuthResult.Success -> {
                    _userData.value = result.data
                    _error.value = null
                }
                is AuthResult.Error -> {
                    _error.value = result.message
                }
            }
            _isLoading.value = false
        }
    }
}