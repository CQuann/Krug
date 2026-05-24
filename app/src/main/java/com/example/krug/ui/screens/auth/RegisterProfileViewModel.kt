package com.example.krug.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.auth.UserData
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _birthday = MutableStateFlow("")
    val birthday: StateFlow<String> = _birthday.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _usernameAvailable = MutableStateFlow<Boolean?>(null)
    val usernameAvailable: StateFlow<Boolean?> = _usernameAvailable.asStateFlow()

    private val _isCheckingUsername = MutableStateFlow(false)
    val isCheckingUsername: StateFlow<Boolean> = _isCheckingUsername.asStateFlow()

    private val _displayNameError = MutableStateFlow<String?>(null)
    val displayNameError: StateFlow<String?> = _displayNameError.asStateFlow()

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<RegisterNavigation>()
    val navigationEvents: SharedFlow<RegisterNavigation> = _navigationEvents.asSharedFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

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

    fun updateDescription(text: String) {
        _description.value = text
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
            if (!available) _usernameError.value = "Никнейм занят"
        }
    }

    fun register(email: String) {
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
            _requestState.value = RequestState.Loading
            val birthdayValue = _birthday.value.takeIf { it.isNotBlank() }
            val userData = UserData(
                email = email,
                displayName = _displayName.value,
                birthday = birthdayValue,
                username = _username.value,
                description = _description.value.takeIf { it.isNotBlank() }
            )
            when (val result = authRepository.register(userData)) {
                is DataResult.Success -> {
                    val (token, userId) = result.data
                    sessionManager.saveToken(token)
                    sessionManager.saveUserId(userId)
                    _navigationEvents.emit(RegisterNavigation.GoToAvatarUpload)
                }
                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                }
            }
        }
    }

    sealed class RegisterNavigation {
        object GoToAvatarUpload : RegisterNavigation()
    }
}