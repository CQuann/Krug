package com.example.krug.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream

@Composable
fun PhotoPicker(
    currentUri: Uri?,
    onUriSelected: (Uri) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = bitmapToUri(context, bitmap)
            onUriSelected(uri)
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onUriSelected(it) }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().height(150.dp)
        ) {
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFF580B4), Color(0xFFF9DA7E),
                    Color(0xFFFF8202), Color(0xFF64D7AC)
                )
            )
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(shape)
                    .clickable { showMenu = true }
                    .border(width = 5.dp, brush = gradientBrush, shape = shape),
                contentAlignment = Alignment.Center
            ) {
                if (currentUri != null) {
                    AsyncImage(
                        model = currentUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = "Выбрать фото",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
                }
            }
            if (currentUri == null && !showMenu) {
                Text(
                    "Нажмите, чтобы выбрать фото",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        if (showMenu) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showMenu = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showMenu = false },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(

                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(16.dp)
                            .offset(y=140.dp),
                        shape = RoundedCornerShape(15.dp),
                        tonalElevation = 8.dp
                    ) {
                        Column {
                            // Камера
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMenu = false
                                        cameraLauncher.launch(null)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Камера", style = MaterialTheme.typography.bodyLarge)
                                Icon(Icons.Default.CameraAlt, contentDescription = null)
                            }
                            HorizontalDivider()
                            // Галерея
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMenu = false
                                        galleryLauncher.launch("image/*")
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Галерея", style = MaterialTheme.typography.bodyLarge)
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun bitmapToUri(context: android.content.Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}