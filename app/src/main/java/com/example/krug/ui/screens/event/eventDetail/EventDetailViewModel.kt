package com.example.krug.ui.screens.event.eventDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.event.DetailedEvent
import com.example.krug.data.model.event.Member
import com.example.krug.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _detailedEvent = MutableStateFlow<DetailedEvent?>(null)
    val detailedEvent: StateFlow<DetailedEvent?> = _detailedEvent.asStateFlow()

    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    private val _showArchiveDialog = MutableStateFlow(false)
    val showArchiveDialog: StateFlow<Boolean> = _showArchiveDialog.asStateFlow()
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _canEdit = MutableStateFlow(false)
    val canEdit: StateFlow<Boolean> = _canEdit.asStateFlow()
    private val _canUploadAvatar = MutableStateFlow(false)
    val canUploadAvatar: StateFlow<Boolean> = _canUploadAvatar.asStateFlow()
    private val _canArchive = MutableStateFlow(false)
    val canArchive: StateFlow<Boolean> = _canArchive.asStateFlow()
    private val _canDelete = MutableStateFlow(false)
    val canDelete: StateFlow<Boolean> = _canDelete.asStateFlow()
    private val _canManageMembers = MutableStateFlow(false)
    val canManageMembers: StateFlow<Boolean> = _canManageMembers.asStateFlow()
    private val _canToggleAdmin = MutableStateFlow(false)
    val canToggleAdmin: StateFlow<Boolean> = _canToggleAdmin.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<DetailNavigationEvent>()
    val navigationEvents: SharedFlow<DetailNavigationEvent> = _navigationEvents.asSharedFlow()

    init { loadEvent() }

    fun loadEvent() {
        viewModelScope.launch {
            _uiState.value = EventDetailUiState.Loading
            when (val result = eventRepository.getEvent(eventId)) {
                is DataResult.Success -> {
                    val detailed = result.data
                    _detailedEvent.value = detailed
                    val perms = detailed.permissions
                    val isCreator = perms.length > 0 && perms[0] == '1'
                    val isAdmin = perms.length > 1 && perms[1] == '1'

                    _canEdit.value = isCreator || isAdmin
                    _canUploadAvatar.value = _canEdit.value
                    _canArchive.value = isCreator || isAdmin
                    _canDelete.value = isCreator
                    _canManageMembers.value = isCreator || isAdmin
                    _canToggleAdmin.value = isCreator

                    _uiState.value = EventDetailUiState.Success
                }
                is DataResult.Error -> _uiState.value = EventDetailUiState.Error(result.message)
            }
        }
    }

    fun getCurrentUserId(): String? = sessionManager.cachedUserId

    fun onEditClick() {
        viewModelScope.launch { _navigationEvents.emit(DetailNavigationEvent.EditEvent(eventId)) }
    }
    fun onUploadAvatarClick() {
        viewModelScope.launch { _navigationEvents.emit(DetailNavigationEvent.UploadAvatar(eventId)) }
    }

    fun onArchiveClick() { _showArchiveDialog.value = true }
    fun onDismissArchiveDialog() { _showArchiveDialog.value = false }
    fun onConfirmArchive() {
        _showArchiveDialog.value = false
        viewModelScope.launch {
            _uiState.value = EventDetailUiState.Loading
            when (val result = eventRepository.updateEventStatus(eventId, "archived")) {
                is DataResult.Success -> {
                    _navigationEvents.emit(DetailNavigationEvent.ShowMessage("Событие архивировано"))
                    _navigationEvents.emit(DetailNavigationEvent.GoBack)
                }
                is DataResult.Error -> _navigationEvents.emit(DetailNavigationEvent.ShowMessage(result.message))
            }
        }
    }

    fun onDeleteClick() { _showDeleteDialog.value = true }
    fun onDismissDeleteDialog() { _showDeleteDialog.value = false }
    fun onConfirmDelete() {
        _showDeleteDialog.value = false
        viewModelScope.launch {
            when (val result = eventRepository.deleteEvent(eventId)) {
                is DataResult.Success -> {
                    _navigationEvents.emit(DetailNavigationEvent.ShowMessage("Событие удалено"))
                    _navigationEvents.emit(DetailNavigationEvent.GoBack)
                }
                is DataResult.Error -> _navigationEvents.emit(DetailNavigationEvent.ShowMessage(result.message))
            }
        }
    }

    fun removeMember(userId: String) {
        viewModelScope.launch {
            _uiState.value = EventDetailUiState.Loading
            when (val result = eventRepository.removeMember(eventId, userId)) {
                is DataResult.Success -> loadEvent()
                is DataResult.Error -> {
                    _navigationEvents.emit(DetailNavigationEvent.ShowMessage(result.message))
                    _uiState.value = EventDetailUiState.Success
                }
            }
        }
    }

    fun toggleAdmin(member: Member) {
        if (!_canToggleAdmin.value) {
            viewModelScope.launch { _navigationEvents.emit(DetailNavigationEvent.ShowMessage("Недостаточно прав")) }
            return
        }
        viewModelScope.launch {
            _uiState.value = EventDetailUiState.Loading
            val perms = member.permissions ?: "00"
            val newPerms = StringBuilder(perms.ensureLength(2))
            val isAdmin = newPerms[1] == '1'
            newPerms.setCharAt(1, if (isAdmin) '0' else '1')
            when (val result = eventRepository.updateMemberPermissions(eventId, member.user_id, newPerms.toString())) {
                is DataResult.Success -> loadEvent()
                is DataResult.Error -> {
                    _navigationEvents.emit(DetailNavigationEvent.ShowMessage(result.message))
                    _uiState.value = EventDetailUiState.Success
                }
            }
        }
    }

    private fun String.ensureLength(length: Int): String {
        var s = this
        while (s.length < length) s += '0'
        return s
    }
}

sealed class EventDetailUiState {
    object Loading : EventDetailUiState()
    object Success : EventDetailUiState()
    data class Error(val message: String) : EventDetailUiState()
}

sealed class DetailNavigationEvent {
    data class EditEvent(val eventId: String) : DetailNavigationEvent()
    data class UploadAvatar(val eventId: String) : DetailNavigationEvent()
    data class ShowMessage(val text: String) : DetailNavigationEvent()
    object GoBack : DetailNavigationEvent()
}