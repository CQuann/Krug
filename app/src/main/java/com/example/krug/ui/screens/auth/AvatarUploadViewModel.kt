package com.example.krug.ui.screens.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvatarUploadViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    // Единое состояние экрана
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    // События для навигации (после успешной загрузки или пропуска)
    private val _navigationEvents = MutableSharedFlow<Unit>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    // События для снекбара (ошибки)
    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    fun setAvatarUri(uri: Uri) {
        _avatarUri.value = uri
        _requestState.value = RequestState.Idle
    }

    fun uploadAvatar() {
        val uri = _avatarUri.value ?: return
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = authRepository.uploadAvatar(uri)) {
                is DataResult.Success -> {
                    _requestState.value = RequestState.Success
                    _navigationEvents.emit(Unit)
                }
                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                }
            }
        }
    }

    fun skipAvatar() {
        viewModelScope.launch {
            _navigationEvents.emit(Unit)
        }
    }
}