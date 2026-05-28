package com.example.krug.ui.screens.event.eventDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
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

    private val _requestState = MutableStateFlow<RequestState>(RequestState.Loading)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

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

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    init { loadEvent() }

    fun loadEvent() {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = eventRepository.getEvent(eventId)) {
                is DataResult.Success -> {
                    val detailed = result.data
                    _detailedEvent.value = detailed
                    applyPermissions(detailed.permissions)
                    _requestState.value = RequestState.Idle
                }
                is DataResult.Error -> _requestState.value = RequestState.Error(result.message)
            }
        }
    }

    private fun applyPermissions(perms: String) {
        val isCreator = perms.length > 0 && perms[0] == '1'
        val isAdmin   = perms.length > 1 && perms[1] == '1'

        _canEdit.value = isCreator || isAdmin
        _canUploadAvatar.value = isCreator || isAdmin
        _canArchive.value = isCreator          // только создатель
        _canDelete.value = isCreator           // только создатель
        _canManageMembers.value = isCreator || isAdmin
        _canToggleAdmin.value = isCreator      // только создатель может назначать других админов
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
            _requestState.value = RequestState.Loading
            when (val result = eventRepository.updateEventStatus(eventId, "archived")) {
                is DataResult.Success -> {
                    _snackbarEvents.emit("Событие архивировано")
                    _navigationEvents.emit(DetailNavigationEvent.GoBack)
                }
                is DataResult.Error -> {
                    _snackbarEvents.emit(result.message)
                    _requestState.value = RequestState.Idle
                }
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
                    _snackbarEvents.emit("Событие удалено")
                    _navigationEvents.emit(DetailNavigationEvent.GoBack)
                }
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }

    fun removeMember(userId: String) {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = eventRepository.removeMember(eventId, userId)) {
                is DataResult.Success -> loadEvent()
                is DataResult.Error -> {
                    _snackbarEvents.emit(result.message)
                    _requestState.value = RequestState.Idle
                }
            }
        }
    }

    fun toggleAdmin(member: Member) {
        if (!_canToggleAdmin.value) {
            viewModelScope.launch { _snackbarEvents.emit("Недостаточно прав") }
            return
        }
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            val perms = member.permissions ?: "000"
            val newPerms = StringBuilder(perms.padEnd(3, '0'))
            val isAdmin = newPerms[1] == '1'
            newPerms.setCharAt(1, if (isAdmin) '0' else '1')
            when (val result = eventRepository.updateMemberPermissions(eventId, member.userId, newPerms.toString())) {
                is DataResult.Success -> loadEvent()
                is DataResult.Error -> {
                    _snackbarEvents.emit(result.message)
                    _requestState.value = RequestState.Idle
                }
            }
        }
    }

    sealed class DetailNavigationEvent {
        data class EditEvent(val eventId: String) : DetailNavigationEvent()
        data class UploadAvatar(val eventId: String) : DetailNavigationEvent()
        object GoBack : DetailNavigationEvent()
    }
}