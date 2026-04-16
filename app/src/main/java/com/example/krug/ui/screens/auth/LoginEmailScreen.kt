package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.krug.ui.theme.KrugTheme

@Composable
fun LoginEmailScreen(
    email: String,
    uiState: LoginEmailUiState,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onResetError: () -> Unit
) {
    var localEmailError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { newEmail ->
                onEmailChange(newEmail)
                // Простейшая проверка для подсказки пользователю (не обязательная для отправки)
                localEmailError = if (newEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    "Введите корректный email"
                } else null
                onResetError()
            },
            label = { Text("Email") },
            isError = localEmailError != null || (uiState is LoginEmailUiState.Error),
            supportingText = {
                if (localEmailError != null) Text(localEmailError!!)
                else if (uiState is LoginEmailUiState.Error) Text(uiState.message)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSendCode,
            enabled = uiState !is LoginEmailUiState.Loading && localEmailError == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is LoginEmailUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Далее")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginEmailScreenPreview() {
    KrugTheme {
        LoginEmailScreen(
            email = "",
            uiState = LoginEmailUiState.Idle,
            onEmailChange = {},
            onSendCode = {},
            onResetError = {}
        )
    }
}