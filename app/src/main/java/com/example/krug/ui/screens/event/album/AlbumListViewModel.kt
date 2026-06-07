package com.example.krug.ui.screens.event.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.album.AlbumResponse
import com.example.krug.data.repository.AlbumRepository
import com.example.krug.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val repository: AlbumRepository,
    private val eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _canCreateAlbum = MutableStateFlow(false)
    val canCreateAlbum: StateFlow<Boolean> = _canCreateAlbum.asStateFlow()

    sealed class AlbumsUiState {
        object Loading : AlbumsUiState()
        data class Content(val albums: List<AlbumResponse>) : AlbumsUiState()
        data class Error(val message: String) : AlbumsUiState()
    }

    private val _uiState = MutableStateFlow<AlbumsUiState>(AlbumsUiState.Loading)
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refreshAlbums() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadAlbums()
            _isRefreshing.value = false
        }
    }

    init {
        loadAlbums()
        loadPermissions()
    }

    private fun loadPermissions() {
        viewModelScope.launch {
            when (val result = eventRepository.getEvent(eventId)) {
                is DataResult.Success -> {
                    val permissions = result.data.permissions
                    _canCreateAlbum.value = permissions.isNotEmpty() &&
                            (permissions[0] == '1' || permissions[1] == '1')  // создатель или админ
                }
                is DataResult.Error -> { /* оставляем false */ }
            }
        }
    }

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = AlbumsUiState.Loading
            when (val result = repository.getAlbums(eventId)) {
                is DataResult.Success -> _uiState.value = AlbumsUiState.Content(result.data)
                is DataResult.Error -> {
                    _uiState.value = AlbumsUiState.Error(result.message)
                    _snackbarEvents.emit(result.message)
                }
            }
        }
    }

    fun createAlbum(title: String, description: String?) {
        viewModelScope.launch {
            when (val result = repository.createAlbum(eventId, title, description)) {
                is DataResult.Success -> {
                    _snackbarEvents.emit("Альбом создан")
                    loadAlbums()
                }
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }

    fun deleteAlbum(albumId: Long) {
        viewModelScope.launch {
            when (val result = repository.deleteAlbum(eventId, albumId)) {
                is DataResult.Success -> {
                    _snackbarEvents.emit("Альбом удалён")
                    loadAlbums()
                }
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }
}