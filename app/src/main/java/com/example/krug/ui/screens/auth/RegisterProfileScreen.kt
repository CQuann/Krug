package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState
import com.example.krug.ui.components.DateTimePickerField
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDate

@Composable
fun RegisterProfileScreen(
    requestState: RequestState,
    displayName: String,
    username: String,
    birthday: String,
    description: String,
    usernameAvailable: Boolean?,
    isCheckingUsername: Boolean,
    displayNameError: String?,
    usernameError: String?,
    snackbarEvents: SharedFlow<String>,
    onDisplayNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBirthdayChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { msg -> snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Заголовок приклеен к верху
            Text(
                "Завершите регистрацию",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            // Прокручиваемое содержимое
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text("Имя") },
                    isError = displayNameError != null,
                    supportingText = displayNameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Никнейм") },
                    isError = usernameError != null,
                    supportingText = {
                        when {
                            usernameError != null -> Text(usernameError, color = MaterialTheme.colorScheme.error)
                            isCheckingUsername -> Text("Проверка...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            usernameAvailable == true -> Text("Доступен", color = MaterialTheme.colorScheme.primary)
                            usernameAvailable == false -> Text("Занят", color = MaterialTheme.colorScheme.error)
                            else -> Text("")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))

                DateTimePickerField(
                    label = "",
                    date = birthday.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) },
                    time = null,
                    onDateSelected = { onBirthdayChange(it?.toString() ?: "") },
                    onTimeSelected = {},
                    enableTime = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("О себе") },
                    maxLines = 3,
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            }

            // Кнопка приклеена к низу
            Surface(tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    if (requestState is RequestState.Error) {
                        Text(
                            text = requestState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Button(
                        onClick = onRegisterClick,
                        enabled = requestState !is RequestState.Loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (requestState is RequestState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Завершить регистрацию", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

// --------------- Preview ---------------

@Preview(showBackground = true, name = "Register – пустая форма")
@Composable
fun RegisterProfileEmptyPreview() {
    KrugTheme {
        RegisterProfileScreen(
            requestState = RequestState.Idle,
            displayName = "",
            username = "",
            birthday = "",
            description = "",
            usernameAvailable = null,
            isCheckingUsername = false,
            displayNameError = null,
            usernameError = null,
            snackbarEvents = MutableSharedFlow(),
            onDisplayNameChange = {},
            onUsernameChange = {},
            onBirthdayChange = {},
            onDescriptionChange = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Register – ошибки")
@Composable
fun RegisterProfileErrorPreview() {
    KrugTheme {
        RegisterProfileScreen(
            requestState = RequestState.Idle,
            displayName = "",
            username = "ab",
            birthday = "2020-01-01",
            description = "Привет!",
            usernameAvailable = false,
            isCheckingUsername = false,
            displayNameError = "Введите имя",
            usernameError = "Никнейм занят",
            snackbarEvents = MutableSharedFlow(),
            onDisplayNameChange = {},
            onUsernameChange = {},
            onBirthdayChange = {},
            onDescriptionChange = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Register – загрузка")
@Composable
fun RegisterProfileLoadingPreview() {
    KrugTheme {
        RegisterProfileScreen(
            requestState = RequestState.Loading,
            displayName = "Иван",
            username = "ivan123",
            birthday = "2020-01-01",
            description = "",
            usernameAvailable = true,
            isCheckingUsername = false,
            displayNameError = null,
            usernameError = null,
            snackbarEvents = MutableSharedFlow(),
            onDisplayNameChange = {},
            onUsernameChange = {},
            onBirthdayChange = {},
            onDescriptionChange = {},
            onRegisterClick = {}
        )
    }
}