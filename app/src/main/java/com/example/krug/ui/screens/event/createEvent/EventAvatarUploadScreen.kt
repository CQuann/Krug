package com.example.krug.ui.screens.event.createEvent

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.krug.ui.components.AvatarPicker
import com.example.krug.ui.theme.KrugTheme

@Composable
fun EventAvatarUploadScreen(
    uiState: EventAvatarUploadUiState,
    avatarUri: Uri?,
    onSetAvatarUri: (Uri) -> Unit,
    onUploadAvatar: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AvatarPicker(
            currentAvatarUri = avatarUri,
            onAvatarUriChanged = onSetAvatarUri
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onUploadAvatar,
            enabled = avatarUri != null && uiState !is EventAvatarUploadUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is EventAvatarUploadUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Загрузить")
            }
        }

        TextButton(onClick = onSkip) {
            Text("Пропустить")
        }

        if (uiState is EventAvatarUploadUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true, name = "EventAvatarUpload – пусто")
@Composable
fun EventAvatarUploadScreenPreviewEmpty() {
    KrugTheme {
        EventAvatarUploadScreen(
            uiState = EventAvatarUploadUiState.Idle,
            avatarUri = null,
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
            uiState = EventAvatarUploadUiState.Loading,
            avatarUri = "android.resource://com.example.krug/drawable/ic_launcher_foreground".toUri(),
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
            uiState = EventAvatarUploadUiState.Error("Не удалось загрузить изображение"),
            avatarUri = null,
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkip = {}
        )
    }
}
