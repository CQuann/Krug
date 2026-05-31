package com.example.krug.ui.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoUploadScreen(
    title: String,
    currentUri: Uri?,
    requestState: RequestState,
    snackbarEvents: SharedFlow<String>,
    shape: Shape,
    uploadButtonText: String = "Загрузить",
    showSkipButton: Boolean = false,
    showBackButton: Boolean = false,
    onSetAvatarUri: (Uri) -> Unit,
    onUploadClick: () -> Unit,
    onSkipClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { msg -> snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (showBackButton) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!showBackButton) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(32.dp))
                } else {
                    Spacer(Modifier.height(24.dp))
                }


                PhotoPicker(
                    currentUri = currentUri,
                    onUriSelected = onSetAvatarUri,
                    shape = shape
                )
            }

            Button(
                onClick = onUploadClick,
                enabled = currentUri != null && requestState !is RequestState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (requestState is RequestState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(uploadButtonText)
                }
            }

            if (showSkipButton) {
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onSkipClick) {
                    Text("Пропустить")
                }
            }

            if (requestState is RequestState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = requestState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "PhotoUpload – круг, пусто")
@Composable
fun PhotoUploadCircleEmptyPreview() {
    KrugTheme {
        PhotoUploadScreen(
            title = "Добавьте фото профиля",
            currentUri = null,
            requestState = RequestState.Idle,
            snackbarEvents = MutableSharedFlow(),
            shape = CircleShape,
            onSetAvatarUri = {},
            onUploadClick = {},
            onSkipClick = {}
        )
    }
}

@Preview(showBackground = true, name = "PhotoUpload – квадрат, выбрано")
@Composable
fun PhotoUploadSquareWithImagePreview() {
    KrugTheme {
        PhotoUploadScreen(
            title = "Добавьте фото события",
            currentUri = Uri.parse("android.resource://com.example.krug/drawable/ic_launcher_foreground"),
            requestState = RequestState.Idle,
            snackbarEvents = MutableSharedFlow(),
            shape = RoundedCornerShape(16.dp),
            onSetAvatarUri = {},
            onUploadClick = {}
        )
    }
}

@Preview(showBackground = true, name = "PhotoUpload – загрузка")
@Composable
fun PhotoUploadLoadingPreview() {
    KrugTheme {
        PhotoUploadScreen(
            title = "Загрузка...",
            currentUri = null,
            requestState = RequestState.Loading,
            snackbarEvents = MutableSharedFlow(),
            shape = CircleShape,
            onSetAvatarUri = {},
            onUploadClick = {}
        )
    }
}

@Preview(showBackground = true, name = "PhotoUpload – ошибка")
@Composable
fun PhotoUploadErrorPreview() {
    KrugTheme {
        PhotoUploadScreen(
            title = "Ошибка",
            currentUri = null,
            requestState = RequestState.Error("Не удалось загрузить фото"),
            snackbarEvents = MutableSharedFlow(),
            shape = CircleShape,
            onSetAvatarUri = {},
            onUploadClick = {}
        )
    }
}