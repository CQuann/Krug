package com.example.krug.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.UserData
import com.example.krug.utils.AvatarUrlProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    userId: String?,
    userData: UserData?,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    val avatarUrl = remember(userId) {
        if (userId != null) AvatarUrlProvider.build(userId) else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Krug") },
                actions = {
                    Box(modifier = Modifier.size(40.dp)) {
                        if (avatarUrl != null) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                error = painterResource(R.drawable.ic_default_avatar)
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // основное содержимое экрана (список событий и т.д.)
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text("Ошибка: $error")
                else -> Text("Добро пожаловать, ${userData?.display_name ?: ""}")
            }
        }
    }
}