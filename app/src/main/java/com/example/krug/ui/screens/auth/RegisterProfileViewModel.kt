package com.example.krug.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.TokenManager
import com.example.krug.data.local.UserIdManager
import com.example.krug.data.model.DataResult
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
    private val tokenManager: TokenManager,
    private val userIdManager: UserIdManager
) : ViewModel() {

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _birthday = MutableStateFlow("")
    val birthday: StateFlow<String> = _birthday.asStateFlow()

    private val _usernameAvailable = MutableStateFlow<Boolean?>(null)
    val usernameAvailable: StateFlow<Boolean?> = _usernameAvailable.asStateFlow()

    private val _isCheckingUsername = MutableStateFlow(false)
    val isCheckingUsername: StateFlow<Boolean> = _isCheckingUsername.asStateFlow()

    // Ошибки валидации, управляются ViewModel
    private val _displayNameError = MutableStateFlow<String?>(null)
    val displayNameError: StateFlow<String?> = _displayNameError.asStateFlow()

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RegisterNavigation>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var checkUsernameJob: Job? = null

    fun updateDisplayName(name: String) {
        _displayName.value = name
        _displayNameError.value = if (name.isBlank()) "Введите имя" else null
    }

    fun updateUsername(name: String) {
        _username.value = name
        _usernameError.value = when {
            name.isBlank() -> "Введите никнейм"
            name.length < 3 -> "Слишком короткий никнейм (мин. 3 символа)"
            else -> null
        }
        checkUsername(name)
    }

    fun updateBirthday(date: String) {
        _birthday.value = date
    }

    private fun checkUsername(username: String) {
        checkUsernameJob?.cancel()
        if (username.isBlank() || username.length < 3) {
            _usernameAvailable.value = null
            _isCheckingUsername.value = false
            return
        }
        _isCheckingUsername.value = true
        checkUsernameJob = viewModelScope.launch {
            delay(500)
            val result = authRepository.checkUsername(username)
            val available = (result as? DataResult.Success)?.data ?: false
            _usernameAvailable.value = available
            _isCheckingUsername.value = false
        }
    }

    fun register(email: String) {
        // Финальная проверка
        var hasError = false
        if (_displayName.value.isBlank()) {
            _displayNameError.value = "Введите имя"
            hasError = true
        }
        if (_username.value.isBlank()) {
            _usernameError.value = "Введите никнейм"
            hasError = true
        } else if (_username.value.length < 3) {
            _usernameError.value = "Слишком короткий никнейм"
            hasError = true
        } else if (_usernameAvailable.value != true) {
            _usernameError.value = "Никнейм недоступен"
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            val birthdayValue = _birthday.value.takeIf { it.isNotBlank() }
            val userData = UserData(email, _displayName.value, birthdayValue, _username.value)
            when (val result = authRepository.register(userData)) {
                is DataResult.Success -> {
                    val (token, userId) = result.data
                    tokenManager.saveToken(token)
                    userIdManager.saveUserId(userId)
                    _navigationEvent.emit(RegisterNavigation.GoToAvatarUpload)
                }
                is DataResult.Error -> {
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
sealed class RegisterNavigation {
    object GoToAvatarUpload : RegisterNavigation()
}