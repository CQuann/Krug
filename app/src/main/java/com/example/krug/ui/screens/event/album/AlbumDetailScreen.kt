package com.example.krug.ui.screens.event.album

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.data.model.album.AlbumWithPhotosResponse
import com.example.krug.data.model.album.PhotoResponse
import com.example.krug.ui.components.PhotoPicker
import com.example.krug.ui.screens.event.album.AlbumDetailViewModel.DetailUiState
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.Constants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    uiState: DetailUiState,
    canDelete: Boolean,
    fullScreenPhotos: List<PhotoResponse>,
    currentPhotoIndex: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    snackbarEvents: SharedFlow<String>,
    onBack: () -> Unit,
    onDeleteAlbum: () -> Unit,
    onUploadPhoto: (Uri) -> Unit,
    onDeletePhoto: (Long) -> Unit,
    onOpenFullScreen: (List<PhotoResponse>, Int) -> Unit,
    onCloseFullScreen: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onDownloadCurrentPhoto: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteAlbumDialog by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { snackbarEvents.collect { snackbarHostState.showSnackbar(it) } }

    if (fullScreenPhotos.isNotEmpty()) {
        FullScreenViewer(
            photos = fullScreenPhotos,
            initialPage = currentPhotoIndex,
            canDelete = canDelete,
            onClose = onCloseFullScreen,
            onPageChanged = onPageChanged,
            onDownload = onDownloadCurrentPhoto,
            onDelete = { photoId ->
                onDeletePhoto(photoId)
                if (fullScreenPhotos.size == 1) onCloseFullScreen()
            }
        )
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text((uiState as? DetailUiState.Content)?.album?.title ?: "Альбом", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } },
                actions = {
                    if (canDelete) {
                        IconButton(onClick = { showDeleteAlbumDialog = true }) {
                            Icon(Icons.Default.Delete, "Удалить альбом", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showPhotoPicker = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить фото")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState) {
                is DetailUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is DetailUiState.Error -> Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(uiState.message, color = MaterialTheme.colorScheme.error) }
                is DetailUiState.Content -> {
                    if (uiState.album.photos.isEmpty()) {
                        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text("Нет фотографий") }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize().padding(padding),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(uiState.album.photos) { photo ->
                                PhotoGridItem(photo = photo, onClick = { onOpenFullScreen(uiState.album.photos, uiState.album.photos.indexOf(photo)) })
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог удаления альбома
    if (showDeleteAlbumDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAlbumDialog = false },
            title = { Text("Удалить альбом?") },
            text = { Text("Все фотографии будут удалены. Действие необратимо.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAlbumDialog = false
                    onDeleteAlbum()
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAlbumDialog = false }) { Text("Отмена") }
            }
        )
    }

    // PhotoPicker
    if (showPhotoPicker) {
        PhotoPicker(
            currentUri = null,
            onUriSelected = { uri ->
                showPhotoPicker = false
                onUploadPhoto(uri)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxSize(),
            useBottomSheet = true,
            onDismiss = { showPhotoPicker = false }
        )
    }
}


@Composable
private fun PhotoGridItem(photo: PhotoResponse, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = "${Constants.BASE_URL}${photo.url}",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun FullScreenViewer(
    photos: List<PhotoResponse>,
    initialPage: Int,
    canDelete: Boolean,
    onClose: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onDownload: () -> Unit,
    onDelete: (Long) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { photos.size }, initialPage = initialPage)
    var showMenu by remember { mutableStateOf(false) }
    val currentPhoto = photos.getOrNull(pagerState.currentPage)
    var offsetY by remember { mutableStateOf(0f) }

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (abs(offsetY) > 150f) onClose()
                        offsetY = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        offsetY += dragAmount
                    }
                )
            }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val photo = photos[page]
            AsyncImage(
                model = "${Constants.BASE_URL}${photo.url}",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Верхняя панель
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "Закрыть", tint = Color.White)
            }
            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, "Ещё", tint = Color.White)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Скачать") },
                        onClick = {
                            showMenu = false
                            onDownload()
                        }
                    )
                    if (canDelete && currentPhoto != null) {
                        DropdownMenuItem(
                            text = { Text("Удалить") },
                            onClick = {
                                showMenu = false
                                onDelete(currentPhoto.photoId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "AlbumDetailScreen – контент")
@Composable
fun AlbumDetailScreenPreview() {
    KrugTheme {
        AlbumDetailScreen(
            uiState = DetailUiState.Content(
                AlbumWithPhotosResponse(
                    albumId = 1,
                    eventId = "42",
                    title = "Общие фотки",
                    description = "Наш первый альбом",
                    createdBy = 5,
                    createdAt = "2024-01-01T12:00:00Z",
                    photos = listOf(
                        PhotoResponse(
                            10,
                            "10.jpg",
                            "IMG_001.jpg",
                            "image/jpeg",
                            123456L,
                            5,
                            "2024-01-01T13:00:00Z",
                            "/events/42/albums/1/photos/10"
                        ),
                        PhotoResponse(
                            11,
                            "11.jpg",
                            "IMG_002.jpg",
                            "image/jpeg",
                            98765L,
                            5,
                            "2024-01-01T14:00:00Z",
                            "/events/42/albums/1/photos/11"
                        )
                    )
                )
            ),
            canDelete = true,
            fullScreenPhotos = emptyList(),
            currentPhotoIndex = 0,
            snackbarEvents = MutableSharedFlow(),
            onBack = {},
            onDeleteAlbum = {},
            onUploadPhoto = {},
            onDeletePhoto = {},
            onOpenFullScreen = { _, _ -> },
            onCloseFullScreen = {},
            onPageChanged = {},
            onDownloadCurrentPhoto = {},
            isRefreshing = false,
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "AlbumDetailScreen – полный экран")
@Composable
fun AlbumDetailScreenFullScreenPreview() {
    KrugTheme {
        AlbumDetailScreen(
            uiState = DetailUiState.Content(
                AlbumWithPhotosResponse(1, "42", "Тест", "", 5, "", listOf())
            ),
            canDelete = true,
            fullScreenPhotos = listOf(
                PhotoResponse(10, "10.jpg", "test.jpg", "image/jpeg", 100L, 5, "", "/events/42/albums/1/photos/10")
            ),
            currentPhotoIndex = 0,
            snackbarEvents = MutableSharedFlow(),
            onBack = {},
            onDeleteAlbum = {},
            onUploadPhoto = {},
            onDeletePhoto = {},
            onOpenFullScreen = { _, _ -> },
            onCloseFullScreen = {},
            onPageChanged = {},
            onDownloadCurrentPhoto = {},
            isRefreshing = false,
            onRefresh = {}
        )
    }
}