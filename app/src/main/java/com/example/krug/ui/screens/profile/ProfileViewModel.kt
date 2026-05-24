package com.example.krug.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.auth.UserData
import com.example.krug.data.repository.AuthRepository
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

    // Загруженные данные пользователя
    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    // Режим редактирования
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    // Поля формы (локальные копии, чтобы не менять оригинал до сохранения)
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

    // Аватар
    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    private val _isEditingAvatar = MutableStateFlow(false) // открыт пикер
    val isEditingAvatar: StateFlow<Boolean> = _isEditingAvatar.asStateFlow()

    private val _avatarChanged = MutableStateFlow(false) // был ли выбран новый аватар

    // Проверка никнейма
    private val _usernameAvailable = MutableStateFlow<Boolean?>(null)
    val usernameAvailable: StateFlow<Boolean?> = _usernameAvailable.asStateFlow()

    private val _isCheckingUsername = MutableStateFlow(false)
    val isCheckingUsername: StateFlow<Boolean> = _isCheckingUsername.asStateFlow()

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

    private var checkUsernameJob: Job? = null // для отмены предыдущей проверки

    //  Общее состояние запроса 
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    //  Одноразовые события (снекбар, навигация) 
    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        loadUser()
    }

    // Загрузка актуальных данных с сервера
    fun loadUser() {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = authRepository.getUserData()) {
                is DataResult.Success -> {
                    val user = result.data
                    _userData.value = user
                    _displayName.value = user.displayName
                    _username.value = user.username
                    _email.value = user.email
                    _birthday.value = user.birthday ?: ""
                    _description.value = user.description ?: ""
                    _usernameAvailable.value = null
                    _usernameError.value = null
                    _requestState.value = RequestState.Idle
                }

                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                }
            }
        }
    }

    fun enterEditMode() {
        _isEditing.value = true
    }

    // Отмена редактирования – возвращаем исходные значения
    fun cancelEditMode() {
        _isEditing.value = false
        _userData.value?.let {
            _displayName.value = it.displayName
            _username.value = it.username
            _birthday.value = it.birthday ?: ""
            _description.value = it.description ?: ""
            _avatarUri.value = null
            _avatarChanged.value = false
        }
        _usernameError.value = null
        _usernameAvailable.value = null
    }

    fun updateDisplayName(value: String) {
        _displayName.value = value
    }

    fun updateBirthday(value: String) {
        _birthday.value = value
    }

    fun updateDescription(value: String) {
        _description.value = value
    }

    fun updateUsername(value: String) {
        _username.value = value
        _usernameAvailable.value = null   // сбрасываем, пока идёт проверка
        _usernameError.value = when {
            value.isBlank() -> "Введите никнейм"
            value.length < 3 -> "Слишком короткий (<3)"
            else -> null
        }
        checkUsername(value)
    }

    fun startAvatarEditing() {
        _isEditingAvatar.value = true
    }

    fun setAvatarUri(uri: Uri) {
        _avatarUri.value = uri
        _isEditingAvatar.value = false
        _avatarChanged.value = true
    }

    // Проверка уникальности никнейма (с debounce через отмену Job)
    private fun checkUsername(username: String) {
        checkUsernameJob?.cancel()
        if (username.isBlank() || username.length < 3) {
            _usernameAvailable.value = null
            _isCheckingUsername.value = false
            return
        }
        // Если никнейм совпадает с текущим – сразу считаем доступным
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

    // Изменилось ли хотя бы одно поле (включая аватар)
    fun hasChanges(): Boolean {
        val user = _userData.value ?: return false
        return _displayName.value != user.displayName ||
                _username.value != user.username ||
                _birthday.value != (user.birthday ?: "") ||
                _description.value != (user.description ?: "") ||
                _avatarChanged.value
    }

    fun isSaveEnabled(): Boolean =
        hasChanges() && _usernameError.value == null && _usernameAvailable.value == true

    // Сохранение профиля (данные + аватар при необходимости)
    fun saveProfile() {
        if (!isSaveEnabled()) return
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            val editResult = authRepository.editUserData(
                userData = UserData(
                    email = _email.value,
                    displayName = _displayName.value,
                    birthday = _birthday.value.takeIf { it.isNotBlank() },
                    username = _username.value,
                    description = _description.value.takeIf { it.isNotBlank() }
                )
            )
            when (editResult) {
                is DataResult.Success -> {
                    // Если аватар менялся – загружаем его
                    if (_avatarChanged.value && _avatarUri.value != null) {
                        val avatarResult = authRepository.uploadAvatar(_avatarUri.value!!)
                        if (avatarResult is DataResult.Error) {
                            _events.emit(ProfileEvent.ShowSnackbar("Ошибка загрузки аватара"))
                        }
                    }
                    _events.emit(ProfileEvent.ShowSnackbar("Профиль сохранён"))
                    loadUser() // перечитываем свежие данные
                    _isEditing.value = false
                    _avatarChanged.value = false
                    _avatarUri.value = null
                    _requestState.value = RequestState.Idle
                }

                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(editResult.message)
                }
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