package com.example.krug.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.UserData

import com.example.krug.data.repository.AuthRepository
import com.example.krug.ui.screens.auth.AvatarUploadUiState
import com.example.krug.ui.screens.splash.SplashNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _usernameError = MutableStateFlow<Boolean>(false)
    val usernameError: StateFlow<Boolean> = _usernameError.asStateFlow()

    private val _isEdited = MutableStateFlow<Boolean>(false)
    val isEdited: StateFlow<Boolean> = _isEdited.asStateFlow()

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()


    private val _isEditingAvatar = MutableStateFlow<Boolean>(false)
    val isEditingAvatar: StateFlow<Boolean?> = _isEditingAvatar.asStateFlow()


    private val _avatarIsChanged = MutableStateFlow<Boolean>(false)

    private val _events = MutableSharedFlow<ProfileEvent?>()
    val events = _events.asSharedFlow()


    init {
        loadUser()
    }

    fun changeAvatarState() {
        _isEditingAvatar.value = true
    }


    fun setAvatarUri(uri: Uri) {
        _avatarUri.value = uri
        _isEditingAvatar.value = false
        _avatarIsChanged.value = true
        _isEdited.value = true
    }


    fun checkIfEdited(
        username: String,
        email: String,
        displayName: String,
        birthday: String
    ) {
        _isEdited.value = !(userData.value != null &&
                username == userData.value!!.username &&
                email == userData.value!!.email &&
                displayName == userData.value!!.display_name &&
                birthday == userData.value!!.birthday &&
                !_avatarIsChanged.value
        )
    }

    fun logout() {
        viewModelScope.launch {
            val token = sessionManager.getToken() ?: return@launch
            authRepository.logout(token)
            sessionManager.clearAll()
            _events.emit(ProfileEvent.NavigateToEmailScreen)
        }
    }


    fun loadUser(){
        viewModelScope.launch {
            val token = sessionManager.getToken() ?: return@launch

            when (val result = authRepository.getUserData()){
                is DataResult.Success -> {
                    _userData.value = result.data
                    Log.d("ProfileViewModel getUserData", _userData.value.toString())
                }
                is DataResult.Error -> {
                    _events.emit(ProfileEvent.ShowSnackbar("При загрузке профиля произошла ошибка"))
                }
            }
        }
    }


    fun checkIfUsernameIsUnique(
        username: String
    ) {
        viewModelScope.launch {
            if (userData.value != null && username == userData.value!!.username) {
                _usernameError.value = false
            } else {
                val result = authRepository.checkUsername(username)
                val isAvailable = (result as? DataResult.Success)?.data ?: false
                _usernameError.value = !isAvailable
            }

        }
    }


    fun editUserData(
        username: String,
        email: String,
        displayName: String,
        birthday: String
    ) {
        viewModelScope.launch {
            val result = authRepository.editUserData(
                userData = UserData(
                    email = email,
                    display_name = displayName,
                    birthday = birthday,
                    username = username
                )
            )

            if (avatarUri.value != null && _avatarIsChanged.value) {
                authRepository.uploadAvatar(avatarUri.value!!, sessionManager.getToken())
            }


            Log.d ("ProfileViewModel editedUserData", birthday + "" + displayName)

            when (result) {
                is DataResult.Success -> {
                    loadUser()
                }
                is DataResult.Error -> {
                    _events.emit(ProfileEvent.ShowSnackbar("При редактировании профиля произошла ошибка"))
                }
            }
        }
    }

    sealed class ProfileEvent {
        data class ShowSnackbar(val message: String) : ProfileEvent()
        object NavigateToEmailScreen: ProfileEvent()
    }
}