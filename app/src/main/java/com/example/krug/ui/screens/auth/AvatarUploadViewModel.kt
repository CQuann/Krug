package com.example.krug.ui.screens.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.TokenManager
import com.example.krug.data.model.auth.AuthResult
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvatarUploadViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    private val _uiState = MutableStateFlow<AvatarUploadUiState>(AvatarUploadUiState.Idle)
    val uiState: StateFlow<AvatarUploadUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun setAvatarUri(uri: Uri) {
        _avatarUri.value = uri
        _uiState.value = AvatarUploadUiState.Idle
    }

    fun uploadAvatar() {
        val uri = _avatarUri.value ?: return
        viewModelScope.launch {
            _uiState.value = AvatarUploadUiState.Loading
            val result = authRepository.uploadAvatar(uri)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = AvatarUploadUiState.Success
                    _navigationEvent.emit(Unit)
                }
                is AuthResult.Error -> {
                    _uiState.value = AvatarUploadUiState.Error(result.message)
                }
            }
        }
    }

    fun skipAvatar() {
        viewModelScope.launch {
            _navigationEvent.emit(Unit)
        }
    }
}

sealed class AvatarUploadUiState {
    object Idle : AvatarUploadUiState()
    object Loading : AvatarUploadUiState()
    object Success : AvatarUploadUiState()
    data class Error(val message: String) : AvatarUploadUiState()
}