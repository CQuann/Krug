package com.example.krug.ui.screens.auth

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.krug.ui.components.AvatarPicker

@Composable
fun AvatarUploadScreen(
    uiState: AvatarUploadUiState,
    avatarUri: Uri?,
    onSetAvatarUri: (Uri) -> Unit,
    onUploadAvatar: () -> Unit,
    onSkipAvatar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
            enabled = avatarUri != null && uiState !is AvatarUploadUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is AvatarUploadUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Загрузить")
            }
        }

        TextButton(onClick = onSkipAvatar) {
            Text("Пропустить")
        }

        if (uiState is AvatarUploadUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}