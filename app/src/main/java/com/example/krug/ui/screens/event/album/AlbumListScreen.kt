package com.example.krug.ui.screens.event.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.album.AlbumResponse
import com.example.krug.ui.screens.event.album.AlbumListViewModel.AlbumsUiState
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun AlbumListScreen(
    uiState: AlbumsUiState,
    snackbarEvents: SharedFlow<String>,
    canCreateAlbum: Boolean,
    onCreateAlbum: (String, String?) -> Unit,
    onDeleteAlbum: (Long) -> Unit,
    onAlbumClick: (Long) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { msg -> snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (canCreateAlbum) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Создать альбом")
                }
            }
        }
    ) { padding ->
        when (uiState) {
            is AlbumsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is AlbumsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text(uiState.message, color = MaterialTheme.colorScheme.error) }
            }
            is AlbumsUiState.Content -> {
                if (uiState.albums.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Нет альбомов", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.albums) { album ->
                            AlbumCard(
                                album = album,
                                onClick = { onAlbumClick(album.albumId) },
                                onDelete = { onDeleteAlbum(album.albumId) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateAlbumDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, description ->
                onCreateAlbum(title, description)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun AlbumCard(
    album: AlbumResponse,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(album.title, style = MaterialTheme.typography.titleMedium)
                if (!album.description.isNullOrBlank()) {
                    Text(
                        album.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AlbumListScreenPreview() {
    KrugTheme {
        AlbumListScreen(
            uiState = AlbumsUiState.Content(
                listOf(
                    AlbumResponse(1, "42", "Общие фотки", "Альбом для всех", 5, "2024-01-01T12:00:00Z", "2024-01-01T12:00:00Z"),
                    AlbumResponse(2, "42", "После вечеринки", null, 5, "2024-01-02T10:00:00Z", "2024-01-02T10:00:00Z")
                )
            ),
            snackbarEvents = MutableSharedFlow(),
            onCreateAlbum = { _, _ -> },
            onDeleteAlbum = {},
            onAlbumClick = {},
            canCreateAlbum = true
        )
    }
}

@Preview(showBackground = true, name = "AlbumListScreen – пусто")
@Composable
fun AlbumListScreenEmptyPreview() {
    KrugTheme {
        AlbumListScreen(
            uiState = AlbumsUiState.Content(emptyList()),
            snackbarEvents = MutableSharedFlow(),
            onCreateAlbum = { _, _ -> },
            onDeleteAlbum = {},
            onAlbumClick = {},
            canCreateAlbum = true
        )
    }
}