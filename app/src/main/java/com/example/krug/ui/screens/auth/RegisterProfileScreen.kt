package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.krug.ui.theme.KrugTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterProfileScreen(
    uiState: RegisterUiState,
    displayName: String,
    username: String,
    birthday: String,
    usernameAvailable: Boolean?,
    isCheckingUsername: Boolean,
    onDisplayNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBirthdayChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onResetError: () -> Unit
) {
    var displayNameError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    val formattedDate = remember(selectedDateMillis) {
        selectedDateMillis?.let {
            val date = Date(it)
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        } ?: ""
    }

    LaunchedEffect(displayName, username) {
        displayNameError = null
        usernameError = null
        onResetError()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Завершите регистрацию", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = onDisplayNameChange,
            label = { Text("Имя") },
            isError = displayNameError != null,
            supportingText = { if (displayNameError != null) Text(displayNameError!!) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { newUsername ->
                onUsernameChange(newUsername)
                if (newUsername.isNotBlank() && newUsername.length < 3) {
                    usernameError = "Слишком короткий никнейм (мин. 3 символа)"
                } else {
                    usernameError = null
                }
            },
            label = { Text("Никнейм") },
            isError = usernameError != null,
            supportingText = {
                when {
                    usernameError != null -> Text(usernameError!!)
                    isCheckingUsername -> Text("Проверка...", color = Color.Gray)
                    usernameAvailable == true -> Text("Доступен")
                    usernameAvailable == false -> Text("Уже занят")
                    else -> Text(" ")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = birthday,
            onValueChange = {},
            label = { Text("Дата рождения (ГГГГ-ММ-ДД)") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                var valid = true
                if (displayName.isBlank()) {
                    displayNameError = "Введите отображаемое имя"
                    valid = false
                }
                if (username.isBlank()) {
                    usernameError = "Введите никнейм"
                    valid = false
                } else if (username.length < 3) {
                    usernameError = "Слишком короткий никнейм"
                    valid = false
                } else if (usernameAvailable != true) {
                    usernameError = "Никнейм недоступен"
                    valid = false
                }
                if (valid) onRegisterClick()
            },
            enabled = uiState !is RegisterUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is RegisterUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Завершить регистрацию")
            }
        }

        if (uiState is RegisterUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        onBirthdayChange(formattedDate)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterProfileScreenPreview() {
    KrugTheme {
        RegisterProfileScreen(
            uiState = RegisterUiState.Idle,
            displayName = "",
            username = "",
            birthday = "",
            usernameAvailable = null,
            isCheckingUsername = false,
            onDisplayNameChange = {},
            onUsernameChange = {},
            onBirthdayChange = {},
            onRegisterClick = {},
            onResetError = {}
        )
    }
}