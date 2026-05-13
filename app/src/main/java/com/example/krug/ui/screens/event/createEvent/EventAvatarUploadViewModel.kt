package com.example.krug.ui.screens.event.createEvent

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventAvatarUploadViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    private val _uiState = MutableStateFlow<EventAvatarUploadUiState>(EventAvatarUploadUiState.Idle)
    val uiState: StateFlow<EventAvatarUploadUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun setAvatarUri(uri: Uri) {
        _avatarUri.value = uri
        _uiState.value = EventAvatarUploadUiState.Idle
    }

    fun uploadAvatar() {
        val uri = _avatarUri.value ?: return
        viewModelScope.launch {
            _uiState.value = EventAvatarUploadUiState.Loading
            when (val result = eventRepository.uploadEventAvatar(eventId, uri)) {
                is DataResult.Success -> {
                    _uiState.value = EventAvatarUploadUiState.Success
                    _navigationEvent.emit(Unit)
                }

                is DataResult.Error -> {
                    _uiState.value = EventAvatarUploadUiState.Error(result.message)
                }
            }
        }
    }

    fun skip() {
        viewModelScope.launch { _navigationEvent.emit(Unit) }
    }
}

sealed class EventAvatarUploadUiState {
    object Idle : EventAvatarUploadUiState()
    object Loading : EventAvatarUploadUiState()
    object Success : EventAvatarUploadUiState()
    data class Error(val message: String) : EventAvatarUploadUiState()
}