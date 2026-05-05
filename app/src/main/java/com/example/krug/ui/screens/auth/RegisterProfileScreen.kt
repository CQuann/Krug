package com.example.krug.ui.screens.auth

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.ui.components.DateTimePickerField
import com.example.krug.ui.theme.KrugTheme
import java.time.LocalDate

@Composable
fun RegisterProfileScreen(
    uiState: RegisterUiState,
    displayName: String,
    username: String,
    birthday: String,
    usernameAvailable: Boolean?,
    isCheckingUsername: Boolean,
    displayNameError: String?,
    usernameError: String?,
    onDisplayNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBirthdayChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onResetError: () -> Unit
) {
    val birthdayDate = remember(birthday) {
        if (birthday.isNotBlank()) LocalDate.parse(birthday) else null
    }

    LaunchedEffect(displayName, username) {
        onResetError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
            supportingText = { displayNameError?.let { Text(it) } },
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
                    usernameError != null -> Text(usernameError)
                    isCheckingUsername -> Text(
                        "Проверка...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    usernameAvailable == true -> Text("Доступен")
                    usernameAvailable == false -> Text("Уже занят")
                    else -> Text(" ")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        DateTimePickerField(
            label = "Дата рождения",
            date = birthdayDate,
            time = null,
            onDateSelected = { newDate ->
                onBirthdayChange(newDate?.toString() ?: "")
            },
            onTimeSelected = {},
            enableTime = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRegisterClick,
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
            displayNameError = null,
            usernameError = null,
            onDisplayNameChange = {},
            onUsernameChange = {},
            onBirthdayChange = {},
            onRegisterClick = {},
            onResetError = {}
        )
    }
}