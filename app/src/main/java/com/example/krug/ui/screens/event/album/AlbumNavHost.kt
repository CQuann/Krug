package com.example.krug.ui.screens.event.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AlbumNavHost(eventId: String, onFullScreenMode: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val isDetail = currentBackStackEntry?.destination?.route?.contains("album_detail") == true

    LaunchedEffect(isDetail) {
        onFullScreenMode(isDetail)
    }

    NavHost(
        navController = navController,
        startDestination = "album_list/$eventId"
    ) {
        composable("album_list/{eventId}") {
            val viewModel: AlbumListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val canCreateAlbum by viewModel.canCreateAlbum.collectAsStateWithLifecycle()
            val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

            val refreshState = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("refreshAlbums")
            refreshState?.observe(LocalLifecycleOwner.current) { needsRefresh ->
                if (needsRefresh == true) {
                    viewModel.loadAlbums()
                    navController.currentBackStackEntry?.savedStateHandle?.set("refreshAlbums", false)
                }
            }

            // Первичная загрузка
            LaunchedEffect(Unit) {
                viewModel.loadAlbums()
            }

            AlbumListScreen(
                uiState = uiState,
                canCreateAlbum = canCreateAlbum,
                snackbarEvents = viewModel.snackbarEvents,
                onCreateAlbum = { title, desc -> viewModel.createAlbum(title, desc) },
                onDeleteAlbum = { albumId -> viewModel.deleteAlbum(albumId) },
                onAlbumClick = { albumId -> navController.navigate("album_detail/$eventId/$albumId") },
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshAlbums() }
            )
        }

        composable(
            route = "album_detail/{eventId}/{albumId}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("albumId") { type = NavType.LongType }
            )
        ) {
            val viewModel: AlbumDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val canDelete by viewModel.canDelete.collectAsStateWithLifecycle()
            val fullScreenPhotos by viewModel.fullScreenPhotos.collectAsStateWithLifecycle()
            val currentPhotoIndex by viewModel.currentPhotoIndex.collectAsStateWithLifecycle()
            val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navEvents.collect { event ->
                    when (event) {
                        AlbumDetailViewModel.DetailNavEvent.GoBack -> {
                            navController.previousBackStackEntry?.savedStateHandle?.set("refreshAlbums", true)
                            navController.popBackStack()
                        }
                    }
                }
            }

            AlbumDetailScreen(
                uiState = uiState,
                canDelete = canDelete,
                fullScreenPhotos = fullScreenPhotos,
                currentPhotoIndex = currentPhotoIndex,
                snackbarEvents = viewModel.snackbarEvents,
                onBack = { navController.popBackStack() },
                onDeleteAlbum = { viewModel.deleteAlbum() },
                onUploadPhoto = { uri -> viewModel.uploadPhoto(uri) },
                onDeletePhoto = { photoId -> viewModel.deletePhoto(photoId) },
                onOpenFullScreen = { photos, index -> viewModel.openFullScreen(photos, index) },
                onCloseFullScreen = { viewModel.closeFullScreen() },
                onPageChanged = { viewModel.onPageChanged(it) },
                onDownloadCurrentPhoto = { viewModel.downloadCurrentPhoto() },
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshAlbum() }
            )
        }
    }
}