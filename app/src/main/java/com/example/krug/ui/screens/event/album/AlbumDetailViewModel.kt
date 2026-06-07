package com.example.krug.ui.screens.event.album

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.request.ImageRequest
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.album.AlbumWithPhotosResponse
import com.example.krug.data.model.album.PhotoResponse
import com.example.krug.data.repository.AlbumRepository
import com.example.krug.data.repository.EventRepository
import com.example.krug.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val eventRepository: EventRepository,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""
    private val albumId: Long = savedStateHandle.get<Long>("albumId") ?: 0L

    sealed class DetailUiState {
        object Loading : DetailUiState()
        data class Content(val album: AlbumWithPhotosResponse) : DetailUiState()
        data class Error(val message: String) : DetailUiState()
    }

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    private val _navEvents = MutableSharedFlow<DetailNavEvent>()
    val navEvents: SharedFlow<DetailNavEvent> = _navEvents.asSharedFlow()

    private val _canDelete = MutableStateFlow(false)
    val canDelete: StateFlow<Boolean> = _canDelete.asStateFlow()

    // Для полноэкранного просмотра
    private val _fullScreenPhotos = MutableStateFlow<List<PhotoResponse>>(emptyList())
    val fullScreenPhotos: StateFlow<List<PhotoResponse>> = _fullScreenPhotos.asStateFlow()

    private val _currentPhotoIndex = MutableStateFlow(0)
    val currentPhotoIndex: StateFlow<Int> = _currentPhotoIndex.asStateFlow()

    init {
        loadAlbum()
        loadPermissions()
    }

    fun loadAlbum() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            when (val result = albumRepository.getAlbum(eventId, albumId)) {
                is DataResult.Success -> {
                    _uiState.value = DetailUiState.Content(result.data)
                }
                is DataResult.Error -> {
                    _uiState.value = DetailUiState.Error(result.message)
                    _snackbarEvents.emit(result.message)
                }
            }
        }
    }

    private fun loadPermissions() {
        viewModelScope.launch {
            when (val result = eventRepository.getEvent(eventId)) {
                is DataResult.Success -> {
                    val permissions = result.data.permissions
                    _canDelete.value = permissions.isNotEmpty() &&
                            (permissions[0] == '1' || permissions[1] == '1')  // создатель или админ
                }
                is DataResult.Error -> { /* оставляем false */ }
            }
        }
    }

    fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            when (val result = albumRepository.uploadPhoto(eventId, albumId, uri)) {
                is DataResult.Success -> {
                    _snackbarEvents.emit("Фото загружено")
                    val newPhoto = result.data
                    _uiState.update { state ->
                        if (state is DetailUiState.Content) {
                            state.copy(
                                album = state.album.copy(
                                    photos = state.album.photos + newPhoto
                                )
                            )
                        } else state
                    }
                }
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }

    fun deletePhoto(photoId: Long) {
        viewModelScope.launch {
            when (val result = albumRepository.deletePhoto(eventId, albumId, photoId)) {
                is DataResult.Success -> {
                    _snackbarEvents.emit("Фото удалено")
                    // Оптимистичное удаление фото из списка
                    _uiState.update { state ->
                        if (state is DetailUiState.Content) {
                            state.copy(
                                album = state.album.copy(
                                    photos = state.album.photos.filter { it.photoId != photoId }
                                )
                            )
                        } else state
                    }
                }
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }

    fun deleteAlbum() {
        viewModelScope.launch {
            when (val result = albumRepository.deleteAlbum(eventId, albumId)) {
                is DataResult.Success -> {
                    _snackbarEvents.emit("Альбом удалён")
                    _navEvents.emit(DetailNavEvent.GoBack)
                }
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }

    // Полноэкранный просмотр
    fun openFullScreen(photos: List<PhotoResponse>, index: Int) {
        _fullScreenPhotos.value = photos
        _currentPhotoIndex.value = index
    }

    fun closeFullScreen() {
        _fullScreenPhotos.value = emptyList()
    }

    fun onPageChanged(index: Int) {
        _currentPhotoIndex.value = index
    }

    // Скачивание текущего фото
    fun downloadCurrentPhoto() {
        val photos = _fullScreenPhotos.value
        if (photos.isEmpty()) return
        val photo = photos[_currentPhotoIndex.value]
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${Constants.BASE_URL}${photo.url}"
                val request = ImageRequest.Builder(appContext)
                    .data(url)
                    .build()
                val bitmap = Coil.imageLoader(appContext).execute(request).drawable?.toBitmap()
                    ?: throw Exception("Не удалось загрузить изображение")
                val fileName = photo.originalName ?: "image_${photo.photoId}.jpg"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, photo.mimeType ?: "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val uri = appContext.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw Exception("Не удалось создать файл")
                appContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Фото сохранено в галерею", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Ошибка при сохранении: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    sealed class DetailNavEvent {
        object GoBack : DetailNavEvent()
    }
}

// helper extension to convert drawable to bitmap
private fun android.graphics.drawable.Drawable.toBitmap(): Bitmap? {
    if (this is android.graphics.drawable.BitmapDrawable) {
        return bitmap
    }
    return null
}