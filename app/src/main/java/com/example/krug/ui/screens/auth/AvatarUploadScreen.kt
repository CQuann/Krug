package com.example.krug.ui.screens.auth

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