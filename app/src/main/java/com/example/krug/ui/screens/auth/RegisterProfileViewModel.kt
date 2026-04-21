package com.example.krug.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.TokenManager
import com.example.krug.data.model.auth.AuthResult
import com.example.krug.data.model.UserData
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _birthday = MutableStateFlow("")
    val birthday: StateFlow<String> = _birthday.asStateFlow()

    private val _usernameAvailable = MutableStateFlow<Boolean?>(null)
    val usernameAvailable: StateFlow<Boolean?> = _usernameAvailable.asStateFlow()


    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var checkUsernameJob: Job? = null

    fun updateDisplayName(name: String) {
        _displayName.value = name
    }

    fun updateUsername(name: String) {
        _username.value = name
        checkUsername(name)
    }

    fun updateBirthday(date: String) {
        _birthday.value = date
    }

    private val _isCheckingUsername = MutableStateFlow(false)
    val isCheckingUsername: StateFlow<Boolean> = _isCheckingUsername.asStateFlow()

    private fun checkUsername(username: String) {
        checkUsernameJob?.cancel()
        if (username.isBlank()) {
            _usernameAvailable.value = null
            _isCheckingUsername.value = false
            return
        }
        _isCheckingUsername.value = true
        checkUsernameJob = viewModelScope.launch {
            delay(500)
            val result = authRepository.checkUsername(username)
            val available = (result as? AuthResult.Success)?.data ?: false
            _usernameAvailable.value = available
            _isCheckingUsername.value = false
        }
    }

    fun register(email: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            val birthdayValue = _birthday.value.takeIf { it.isNotBlank() }
            val userData = UserData(email, _displayName.value, birthdayValue, _username.value)
            val result = authRepository.register(userData)
            when (result) {
                is AuthResult.Success -> {
                    tokenManager.saveToken(result.data)
                    _navigationEvent.emit(Unit)
                }
                is AuthResult.Error -> {
                    _uiState.value = RegisterUiState.Error(result.message)
                }
            }
        }
    }

    fun resetError() {
        if (_uiState.value is RegisterUiState.Error) {
            _uiState.value = RegisterUiState.Idle
        }
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}