package com.example.krug.ui.screens.event.createEvent

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState
import com.example.krug.ui.components.AvatarPicker
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun EventAvatarUploadScreen(
    requestState: RequestState,
    avatarUri: Uri?,
    snackbarEvents: SharedFlow<String>,
    onSetAvatarUri: (Uri) -> Unit,
    onUploadAvatar: () -> Unit,
    onSkip: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Добавьте фото события",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

            AvatarPicker(
                currentAvatarUri = avatarUri,
                onAvatarUriChanged = onSetAvatarUri
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onUploadAvatar,
                enabled = avatarUri != null && requestState !is RequestState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (requestState is RequestState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Загрузить")
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onSkip) {
                Text("Пропустить")
            }

            if (requestState is RequestState.Error) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = (requestState as RequestState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// ------------ Preview ------------

@Preview(showBackground = true, name = "EventAvatarUpload – пусто")
@Composable
fun EventAvatarUploadScreenPreviewEmpty() {
    KrugTheme {
        EventAvatarUploadScreen(
            requestState = RequestState.Idle,
            avatarUri = null,
            snackbarEvents = MutableSharedFlow(),
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true, name = "EventAvatarUpload – загрузка")
@Composable
fun EventAvatarUploadScreenPreviewLoading() {
    KrugTheme {
        EventAvatarUploadScreen(
            requestState = RequestState.Loading,
            avatarUri = null,
            snackbarEvents = MutableSharedFlow(),
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true, name = "EventAvatarUpload – ошибка")
@Composable
fun EventAvatarUploadScreenPreviewError() {
    KrugTheme {
        EventAvatarUploadScreen(
            requestState = RequestState.Error("Не удалось загрузить изображение"),
            avatarUri = null,
            snackbarEvents = MutableSharedFlow(),
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkip = {}
        )
    }
}