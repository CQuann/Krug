package com.example.krug.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.auth.UserData
import com.example.krug.data.repository.AuthRepository
import com.example.krug.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _birthday = MutableStateFlow("")
    val birthday: StateFlow<String> = _birthday.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()

    private val _usernameAvailable = MutableStateFlow<Boolean?>(null)
    val usernameAvailable: StateFlow<Boolean?> = _usernameAvailable.asStateFlow()

    private val _isCheckingUsername = MutableStateFlow(false)
    val isCheckingUsername: StateFlow<Boolean> = _isCheckingUsername.asStateFlow()

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

    private var checkUsernameJob: Job? = null

    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init { loadUser() }

    fun loadUser() {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = authRepository.getUserData()) {
                is DataResult.Success -> {
                    val user = result.data
                    _userData.value = user
                    _displayName.value = user.displayName ?: ""
                    _username.value = user.username
                    _email.value = user.email
                    _birthday.value = user.birthday ?: ""
                    _description.value = user.description ?: ""
                    _avatarUrl.value = "${Constants.BASE_URL}/avatars/${user.userId}?t=${System.currentTimeMillis()}"
                    _usernameAvailable.value = null
                    _usernameError.value = null
                    _requestState.value = RequestState.Idle
                }
                is DataResult.Error -> _requestState.value = RequestState.Error(result.message)
            }
        }
    }

    fun enterEditMode() {
        _isEditing.value = true
        if (_username.value == _userData.value?.username) {
            _usernameAvailable.value = true
        }
    }

    fun cancelEditMode() {
        _isEditing.value = false
        _userData.value?.let {
            _displayName.value = it.displayName ?: ""
            _username.value = it.username
            _birthday.value = it.birthday ?: ""
            _description.value = it.description ?: ""
        }
        _usernameError.value = null
        _usernameAvailable.value = null
    }

    fun updateDisplayName(value: String) { _displayName.value = value }
    fun updateBirthday(value: String) { _birthday.value = value }
    fun updateDescription(value: String) { _description.value = value }

    fun updateUsername(value: String) {
        _username.value = value
        _usernameAvailable.value = null
        _usernameError.value = when {
            value.isBlank() -> "Введите никнейм"
            value.length < 3 -> "Слишком короткий (<3)"
            else -> null
        }
        checkUsername(value)
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = authRepository.uploadAvatar(uri)) {
                is DataResult.Success -> {
                    _events.emit(ProfileEvent.ShowSnackbar("Аватар обновлён"))
                    loadUser()
                    _requestState.value = RequestState.Idle
                }
                is DataResult.Error -> {
                    _events.emit(ProfileEvent.ShowSnackbar("Ошибка загрузки аватара"))
                    _requestState.value = RequestState.Idle
                }
            }
        }
    }

    private fun checkUsername(username: String) {
        checkUsernameJob?.cancel()
        if (username.isBlank() || username.length < 3) {
            _usernameAvailable.value = null
            _isCheckingUsername.value = false
            return
        }
        if (username == _userData.value?.username) {
            _usernameAvailable.value = true
            _isCheckingUsername.value = false
            return
        }
        _isCheckingUsername.value = true
        checkUsernameJob = viewModelScope.launch {
            val result = authRepository.checkUsername(username)
            val available = (result as? DataResult.Success)?.data ?: false
            _usernameAvailable.value = available
            _isCheckingUsername.value = false
            if (!available) _usernameError.value = "Никнейм занят"
        }
    }

    fun hasChanges(): Boolean {
        val user = _userData.value ?: return false
        return _displayName.value != user.displayName ||
                _username.value != user.username ||
                _birthday.value != (user.birthday ?: "") ||
                _description.value != (user.description ?: "")
    }

    fun isSaveEnabled(): Boolean =
        hasChanges() && _usernameError.value == null && _usernameAvailable.value == true

    fun saveProfile() {
        if (!isSaveEnabled()) return
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            val editResult = authRepository.editUserData(
                userData = UserData(
                    userId = _userData.value?.userId ?: "",
                    email = _email.value,
                    displayName = _displayName.value,
                    birthday = _birthday.value,
                    username = _username.value,
                    description = _description.value
                )
            )
            when (editResult) {
                is DataResult.Success -> {
                    _events.emit(ProfileEvent.ShowSnackbar("Профиль сохранён"))
                    loadUser()
                    _isEditing.value = false
                    _requestState.value = RequestState.Idle
                }
                is DataResult.Error -> _requestState.value = RequestState.Error(editResult.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            sessionManager.clearAll()
            _events.emit(ProfileEvent.NavigateToLogin)
        }
    }

    sealed class ProfileEvent {
        data class ShowSnackbar(val message: String) : ProfileEvent()
        object NavigateToLogin : ProfileEvent()
    }
}