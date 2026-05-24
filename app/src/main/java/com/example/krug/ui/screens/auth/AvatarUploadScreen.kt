package com.example.krug.ui.screens.auth

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.krug.data.model.RequestState
import com.example.krug.ui.components.AvatarPicker
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun AvatarUploadScreen(
    requestState: RequestState,
    avatarUri: Uri?,
    snackbarEvents: SharedFlow<String>,
    onSetAvatarUri: (Uri) -> Unit,
    onUploadAvatar: () -> Unit,
    onSkipAvatar: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { msg -> snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Text(
                "Добавьте фото профиля",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
            )

            // Аватар по центру доступного пространства
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarPicker(currentAvatarUri = avatarUri, onAvatarUriChanged = onSetAvatarUri)
            }

            // Кнопки приклеены к низу
            Surface(tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (requestState is RequestState.Error) {
                        Text(
                            text = requestState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Button(
                        onClick = onUploadAvatar,
                        enabled = avatarUri != null && requestState !is RequestState.Loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (requestState is RequestState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Загрузить", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = onSkipAvatar, ) {
                        Text("Пропустить", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

// --------------- Preview ---------------

@Preview(showBackground = true, name = "AvatarUpload – пусто")
@Composable
fun AvatarUploadEmptyPreview() {
    KrugTheme {
        AvatarUploadScreen(
            requestState = RequestState.Idle,
            avatarUri = null,
            snackbarEvents = MutableSharedFlow(),
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkipAvatar = {}
        )
    }
}

@Preview(showBackground = true, name = "AvatarUpload – выбран файл")
@Composable
fun AvatarUploadWithImagePreview() {
    val fakeUri = "android.resource://com.example.krug/drawable/ic_launcher_foreground".toUri()
    KrugTheme {
        AvatarUploadScreen(
            requestState = RequestState.Idle,
            avatarUri = fakeUri,
            snackbarEvents = MutableSharedFlow(),
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkipAvatar = {}
        )
    }
}

@Preview(showBackground = true, name = "AvatarUpload – загрузка")
@Composable
fun AvatarUploadLoadingPreview() {
    KrugTheme {
        AvatarUploadScreen(
            requestState = RequestState.Loading,
            avatarUri = null,
            snackbarEvents = MutableSharedFlow(),
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkipAvatar = {}
        )
    }
}

@Preview(showBackground = true, name = "AvatarUpload – ошибка")
@Composable
fun AvatarUploadErrorPreview() {
    KrugTheme {
        AvatarUploadScreen(
            requestState = RequestState.Error("Не удалось загрузить фото"),
            avatarUri = null,
            snackbarEvents = MutableSharedFlow(),
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkipAvatar = {}
        )
    }
}