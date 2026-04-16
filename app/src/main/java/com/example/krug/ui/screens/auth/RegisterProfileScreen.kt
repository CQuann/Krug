package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
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

@Composable
fun RegisterProfileScreen(
    uiState: RegisterUiState,
    displayName: String,
    username: String,
    birthday: String,
    usernameAvailable: Boolean?,
    onDisplayNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBirthdayChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onResetError: () -> Unit
) {
    var displayNameError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isCheckingUsername by remember { mutableStateOf(false) }


    // Состояние для DatePicker
    val datePickerState = rememberDatePickerState()
    val selectedDateMillis = datePickerState.selectedDateMillis

    // Форматирование выбранной даты в "yyyy-MM-dd"
    val formattedDate = remember(selectedDateMillis) {
        selectedDateMillis?.let {
            val date = Date(it)
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        } ?: ""
    }

    // При выборе даты обновляем поле
    LaunchedEffect(formattedDate) {
        if (formattedDate.isNotBlank()) {
            onBirthdayChange(formattedDate)
            showDatePicker = false
        }
    }

    // Сброс ошибок при изменении полей
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
            onValueChange = onUsernameChange,
            label = { Text("Никнейм") },
            isError = usernameError != null,
            supportingText = {
                when {
                    usernameError != null -> Text(usernameError!!)
                    isCheckingUsername -> Text("Проверка...", color = Color.Gray)
                    usernameAvailable == true -> Text("✓ доступен", color = Color.Green)
                    usernameAvailable == false -> Text("✗ уже занят", color = Color.Red)
                    else -> Text(" ")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Поле даты рождения с кнопкой выбора
        OutlinedTextField(
            value = birthday,
            onValueChange = {},
            label = { Text("Дата рождения (ГГГГ-ММ-ДД)") },
            readOnly = true,
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Выбрать")
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
                text = (uiState as RegisterUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    // DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // При подтверждении дата уже обновится через LaunchedEffect
                    if (selectedDateMillis == null) showDatePicker = false
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
            onDisplayNameChange = {},
            onUsernameChange = {},
            onBirthdayChange = {},
            onRegisterClick = {},
            onResetError = {}
        )
    }
}