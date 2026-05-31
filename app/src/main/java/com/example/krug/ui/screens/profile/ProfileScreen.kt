package com.example.krug.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.auth.UserData
import com.example.krug.ui.components.DateTimePickerField
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
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
    avatarUrl: String?,
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
    onChangePhotoClick: () -> Unit,
    onSaveProfile: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ProfileViewModel.ProfileEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is ProfileViewModel.ProfileEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEditing) "Редактирование профиля" else "Профиль") },
                navigationIcon = {
                    IconButton(onClick = if (isEditing) onCancelEdit else onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = onEnterEditMode) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                        IconButton(onClick = onChangePhotoClick) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Изменить аватар")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (userData == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Прокручиваемое содержимое
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Аватар (только в режиме просмотра)
                    if (!isEditing) {
                        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                            if (avatarUrl != null) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Аватар",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(120.dp).clip(CircleShape),
                                    error = painterResource(R.drawable.ic_default_avatar)
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(120.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    ProfileField("Никнейм", username, isEditing, onUpdateUsername, Icons.Default.AlternateEmail, usernameError != null) {
                        when {
                            usernameError != null -> Text(usernameError!!)
                            isCheckingUsername -> Text("Проверка...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            usernameAvailable == true && isEditing -> Text("Доступен")
                            usernameAvailable == false && isEditing -> Text("Занят")
                        }
                    }
                    ProfileField("Имя", displayName, isEditing, onUpdateDisplayName, Icons.Default.Person)
                    if (!isEditing) ProfileField("Электронная почта", email, false, {}, Icons.Default.Email)

                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
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
                    }

                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        OutlinedTextField(
                            value = if (isEditing) description else description.ifEmpty { "Не указано" },
                            onValueChange = onUpdateDescription,
                            label = { Text("О себе") },
                            enabled = isEditing,
                            readOnly = !isEditing,
                            maxLines = 6,
                            trailingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            colors = outlineTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
                        )
                    }
                }

                // Кнопки действий
                if (isEditing) {
                    Button(
                        onClick = onSaveProfile,
                        enabled = requestState !is RequestState.Loading && usernameError == null && usernameAvailable == true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        shape = MaterialTheme.shapes.medium
                    ) { Text("Сохранить") }
                } else {
                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Выйти из аккаунта", fontWeight = FontWeight.Medium) }
                }

                if (requestState is RequestState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = requestState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String, value: String, isEditing: Boolean,
    onValueChange: (String) -> Unit, icon: ImageVector,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = if (isEditing) value else value.ifEmpty { "Не указано" },
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = isEditing,
        readOnly = !isEditing,
        isError = isError,
        supportingText = supportingText,
        trailingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        colors = outlineTextFieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun outlineTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    errorTextColor = MaterialTheme.colorScheme.error,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    errorBorderColor = MaterialTheme.colorScheme.error,
)

// Превью без изменений
@Preview(showBackground = true, name = "Профиль – обычный вид")
@Composable
fun ProfileScreenNormalPreview() {
    KrugTheme {
        ProfileScreen(
            userData = UserData(userId = "1", email = "user@example.com", username = "user123",
                displayName = "Иван", birthday = "2000-01-01", description = "Привет!"),
            isEditing = false,
            displayName = "Иван",
            username = "user123",
            email = "user@example.com",
            birthday = "2000-01-01",
            description = "Привет!",
            avatarUrl = null,
            usernameAvailable = null,
            isCheckingUsername = false,
            usernameError = null,
            requestState = RequestState.Idle,
            events = MutableSharedFlow(),
            onBackClick = {},
            onEnterEditMode = {},
            onCancelEdit = {},
            onUpdateDisplayName = {},
            onUpdateUsername = {},
            onUpdateBirthday = {},
            onUpdateDescription = {},
            onChangePhotoClick = {},
            onSaveProfile = {},
            onLogout = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, name = "Профиль – пустые поля")
@Composable
fun ProfileScreenEmptyFieldsPreview() {
    KrugTheme {
        ProfileScreen(
            userData = UserData(userId = "1", email = "user@example.com", username = "user123",
                displayName = null, birthday = null, description = null),
            isEditing = false,
            displayName = "",
            username = "user123",
            email = "user@example.com",
            birthday = "",
            description = "",
            avatarUrl = null,
            usernameAvailable = null,
            isCheckingUsername = false,
            usernameError = null,
            requestState = RequestState.Idle,
            events = MutableSharedFlow(),
            onBackClick = {},
            onEnterEditMode = {},
            onCancelEdit = {},
            onUpdateDisplayName = {},
            onUpdateUsername = {},
            onUpdateBirthday = {},
            onUpdateDescription = {},
            onChangePhotoClick = {},
            onSaveProfile = {},
            onLogout = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, name = "Профиль – редактирование")
@Composable
fun ProfileScreenEditingPreview() {
    KrugTheme {
        ProfileScreen(
            userData = UserData(userId = "1", email = "user@example.com", username = "user123",
                displayName = "Иван", birthday = "2000-01-01", description = "Привет!"),
            isEditing = true,
            displayName = "Иван",
            username = "user123",
            email = "user@example.com",
            birthday = "2000-01-01",
            description = "Привет!",
            avatarUrl = null,
            usernameAvailable = true,
            isCheckingUsername = false,
            usernameError = null,
            requestState = RequestState.Idle,
            events = MutableSharedFlow(),
            onBackClick = {},
            onEnterEditMode = {},
            onCancelEdit = {},
            onUpdateDisplayName = {},
            onUpdateUsername = {},
            onUpdateBirthday = {},
            onUpdateDescription = {},
            onChangePhotoClick = {},
            onSaveProfile = {},
            onLogout = {},
            onNavigateToLogin = {}
        )
    }
}