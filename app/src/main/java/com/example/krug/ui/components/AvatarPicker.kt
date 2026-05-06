package com.example.krug.ui.components

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.krug.R
import com.example.krug.ui.theme.KrugTheme
import java.io.File
import java.io.FileOutputStream

@Composable
fun AvatarPicker(
    currentAvatarUri: Uri?,
    onAvatarUriChanged: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onAvatarUriChanged(it) }
    }

    // Камера — сам лаунчер
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = bitmapToUri(context, bitmap)
            onAvatarUriChanged(uri)
        }
    }

    // Запрос разрешения CAMERA
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Выберите фото", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.size(120.dp)) {
            if (currentAvatarUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(currentAvatarUri),
                    contentDescription = "Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_default_avatar),
                    contentDescription = "Default avatar",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Галерея")
            }
            Button(onClick = {
                // Запрашиваем разрешение камеры
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text("Камера")
            }
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

@Preview(showBackground = true, name = "AvatarPicker – пусто")
@Composable
fun AvatarPickerPreviewEmpty() {
    KrugTheme {
        AvatarPicker(
            currentAvatarUri = null,
            onAvatarUriChanged = {}
        )
    }
}

@Preview(showBackground = true, name = "AvatarPicker – с изображением")
@Composable
fun AvatarPickerPreviewWithImage() {
    val fakeUri = "android.resource://com.example.krug/drawable/ic_launcher_foreground".toUri()
    KrugTheme {
        AvatarPicker(
            currentAvatarUri = fakeUri,
            onAvatarUriChanged = {}
        )
    }
}