package com.example.krug.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.krug.R
import com.example.krug.data.model.auth.UserData

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.example.krug.data.model.RequestState
import com.example.krug.ui.components.AvatarPicker
import com.example.krug.ui.components.DateTimePickerField
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userData: UserData?,
    isEditing: Boolean,
    displayName: String,
    username: String,
    email: String,
    birthday: String,
    description: String,
    avatarUri: Uri?,
    isEditingAvatar: Boolean,
    usernameAvailable: Boolean?,
    isCheckingUsername: Boolean,
    usernameError: String?,
    requestState: RequestState,
    events: SharedFlow<ProfileViewModel.ProfileEvent>,
    onBackClick: () -> Unit,
    onEnterEditMode: () -> Unit,
    onCancelEdit: () -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    onUpdateUsername: (String) -> Unit,
    onUpdateBirthday: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onStartAvatarEditing: () -> Unit,
    onSetAvatarUri: (Uri) -> Unit,
    onSaveProfile: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Сбор событий ViewModel
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ProfileViewModel.ProfileEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ProfileViewModel.ProfileEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = onEnterEditMode) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                        IconButton(onClick = onStartAvatarEditing) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Изменить аватар")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            userData == null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Аватар
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val currentImage = when {
                            avatarUri != null -> rememberAsyncImagePainter(avatarUri)
                            else -> painterResource(R.drawable.ic_default_avatar)
                        }
                        Image(
                            painter = currentImage,
                            contentDescription = "Аватар",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(120.dp).clip(CircleShape)
                        )
                        if (isEditingAvatar) {
                            AvatarPicker(
                                currentAvatarUri = avatarUri,
                                onAvatarUriChanged = { onSetAvatarUri(it) }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = onUpdateDisplayName,
                        label = { Text("Имя") },
                        enabled = isEditing,
                        readOnly = !isEditing,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = onUpdateUsername,
                        label = { Text("Никнейм") },
                        enabled = isEditing,
                        readOnly = !isEditing,
                        isError = usernameError != null,
                        supportingText = {
                            when {
                                usernameError != null -> Text(usernameError!!)
                                isCheckingUsername -> Text("Проверка...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                usernameAvailable == true && isEditing -> Text("Доступен")
                                usernameAvailable == false && isEditing -> Text("Занят")
                                else -> Text(" ")
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Электронная почта") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    DateTimePickerField(
                        label = "Дата рождения",
                        date = if (birthday.isNotBlank()) LocalDate.parse(birthday) else null,
                        time = null,
                        onDateSelected = { date -> onUpdateBirthday(date?.toString() ?: "") },
                        onTimeSelected = {},
                        enableTime = false,
                        enabled = isEditing,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = onUpdateDescription,
                        label = { Text("О себе") },
                        enabled = isEditing,
                        readOnly = !isEditing,
                        maxLines = 5,
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isEditing) {
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = onSaveProfile,
                            enabled = requestState !is RequestState.Loading && usernameError == null && usernameAvailable == true,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (requestState is RequestState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Text("Сохранить")
                            }
                        }
                        // Кнопка отмены редактирования
                        TextButton(onClick = onCancelEdit) {
                            Text("Отмена")
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Выйти из аккаунта", fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(32.dp))
                    }

                    if (requestState is RequestState.Error) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = (requestState as RequestState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}