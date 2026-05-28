package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState
import com.example.krug.ui.theme.KrugTheme

@Composable
fun LoginEmailScreen(
    email: String,
    requestState: RequestState,
    emailError: String?,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onResetError: () -> Unit
) {
    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Заголовок приклеен к верху
            Text(
                "Вход в Круг",
                style = MaterialTheme.typography.headlineMedium,
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
                Spacer(Modifier.weight(0.5f))
                Text(
                    "Введите email для получения кода",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { newEmail ->
                        onEmailChange(newEmail)
                        onResetError()
                    },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    isError = emailError != null || (requestState is RequestState.Error),
                    supportingText = {
                        when {
                            emailError != null -> Text(emailError, color = MaterialTheme.colorScheme.error)
                            requestState is RequestState.Error -> Text(requestState.message, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.weight(0.5f))
            }

            // Кнопка приклеена к низу
            Surface(tonalElevation = 8.dp) {
                Button(
                    onClick = onSendCode,
                    enabled = requestState !is RequestState.Loading && emailError == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (requestState is RequestState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Далее", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "LoginEmail – пусто")
@Composable
fun LoginEmailEmptyPreview() {
    KrugTheme {
        LoginEmailScreen(
            email = "",
            requestState = RequestState.Idle,
            emailError = null,
            onEmailChange = {},
            onSendCode = {},
            onResetError = {}
        )
    }
}

@Preview(showBackground = true, name = "LoginEmail – ошибка")
@Composable
fun LoginEmailErrorPreview() {
    KrugTheme {
        LoginEmailScreen(
            email = "not_an_email",
            requestState = RequestState.Idle,
            emailError = "Введите корректный email",
            onEmailChange = {},
            onSendCode = {},
            onResetError = {}
        )
    }
}