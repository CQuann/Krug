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
    private val albumRepository: AlbumRepository,
    private val eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    sealed class AlbumsUiState {
        object Loading : AlbumsUiState()
        data class Content(val albums: List<AlbumResponse>) : AlbumsUiState()
        data class Error(val message: String) : AlbumsUiState()
    }

    private val _uiState = MutableStateFlow<AlbumsUiState>(AlbumsUiState.Loading)
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    private val _canCreateAlbum = MutableStateFlow(false)
    val canCreateAlbum: StateFlow<Boolean> = _canCreateAlbum.asStateFlow()

    private var lastAlbums: List<AlbumResponse>? = null

    init {
        loadAlbums()
        loadPermissions()
    }

    fun loadAlbums(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh && lastAlbums == null) {
                _uiState.value = AlbumsUiState.Loading
            }
            when (val result = albumRepository.getAlbums(eventId)) {
                is DataResult.Success -> {
                    lastAlbums = result.data
                    _uiState.value = AlbumsUiState.Content(lastAlbums!!)
                }
                is DataResult.Error -> {
                    if (lastAlbums == null) {
                        _uiState.value = AlbumsUiState.Error(result.message)
                    }
                    _snackbarEvents.emit(result.message)
                }
            }
            if (isRefresh) _isRefreshing.value = false
        }
    }

    fun refreshAlbums() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadAlbums(isRefresh = true)
        }
    }

    fun createAlbum(title: String, description: String?) {
        viewModelScope.launch {
            when (val result = albumRepository.createAlbum(eventId, title, description)) {
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
            _uiState.update { state ->
                if (state is AlbumsUiState.Content) {
                    state.copy(albums = state.albums.filter { it.albumId != albumId })
                } else state
            }
            when (val result = albumRepository.deleteAlbum(eventId, albumId)) {
                is DataResult.Success -> {
                    _snackbarEvents.emit("Альбом удалён")
                    // Обновляем список актуальными данными в фоне
                    loadAlbums(isRefresh = true)
                }
                is DataResult.Error -> {
                    _snackbarEvents.emit(result.message)
                    // Откатываем изменения, перезагружая точный список
                    loadAlbums(isRefresh = true)
                }
            }
        }
    }

    private fun loadPermissions() {
        viewModelScope.launch {
            when (val result = eventRepository.getEvent(eventId)) {
                is DataResult.Success -> {
                    val permissions = result.data.permissions
                    _canCreateAlbum.value = permissions.isNotEmpty() &&
                            (permissions[0] == '1' || permissions[1] == '1')
                }
                is DataResult.Error -> { /* nothing */ }
            }
        }
    }
}