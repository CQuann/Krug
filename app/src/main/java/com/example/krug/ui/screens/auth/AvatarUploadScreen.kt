package com.example.krug.ui.screens.auth

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.krug.ui.theme.KrugTheme
import java.io.File
import java.io.FileOutputStream
import androidx.core.net.toUri

@Composable
fun AvatarUploadScreen(
    uiState: AvatarUploadUiState,
    avatarUri: Uri?,
    onSetAvatarUri: (Uri) -> Unit,
    onUploadAvatar: () -> Unit,
    onSkipAvatar: () -> Unit
) {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onSetAvatarUri(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = bitmapToUri(context, bitmap)
            onSetAvatarUri(uri)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(200.dp))

        Text("Добавьте фото профиля", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.size(120.dp)) {
            if (avatarUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(avatarUri),
                    contentDescription = "Avatar preview",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Placeholder",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text("Выбрать из галереи")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { cameraLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
                Text("Сделать фото")
            }
        }
        Spacer(modifier = Modifier.weight(1f))

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
                text = (uiState as AvatarUploadUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun bitmapToUri(context: android.content.Context, bitmap: Bitmap): Uri {
    val tempFile = File(context.cacheDir, "camera_avatar_${System.currentTimeMillis()}.jpg")
    FileOutputStream(tempFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}


@Preview(showBackground = true, name = "Empty state")
@Composable
fun AvatarUploadScreenEmptyPreview() {
    KrugTheme {
        AvatarUploadScreen(
            uiState = AvatarUploadUiState.Idle,
            avatarUri = null,
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkipAvatar = {}
        )
    }
}

@Preview(showBackground = true, name = "With selected avatar")
@Composable
fun AvatarUploadScreenWithAvatarPreview() {
    // Используем фиктивный URI (можно использовать ресурс из drawable или сетевую картинку)
    val fakeUri = "android.resource://com.example.krug/drawable/ic_launcher_foreground".toUri()
    KrugTheme {
        AvatarUploadScreen(
            uiState = AvatarUploadUiState.Idle,
            avatarUri = fakeUri,
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkipAvatar = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading state")
@Composable
fun AvatarUploadScreenLoadingPreview() {
    KrugTheme {
        AvatarUploadScreen(
            uiState = AvatarUploadUiState.Loading,
            avatarUri = null,
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkipAvatar = {}
        )
    }
}

@Preview(showBackground = true, name = "Error state")
@Composable
fun AvatarUploadScreenErrorPreview() {
    KrugTheme {
        AvatarUploadScreen(
            uiState = AvatarUploadUiState.Error("Не удалось загрузить фото"),
            avatarUri = null,
            onSetAvatarUri = {},
            onUploadAvatar = {},
            onSkipAvatar = {}
        )
    }
}