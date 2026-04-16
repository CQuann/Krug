package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.krug.ui.Screen

@Composable
fun LoginEmailScreen(
    navController: NavController,
    viewModel: LoginEmailViewModel = hiltViewModel()
) {
    // Локальное состояние для текстового поля
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Подписка на состояние ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Обработка навигационных событий (одноразовых)
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { email ->
            navController.navigate(Screen.VerifyCode.passArgs(email))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Поле ввода email
        OutlinedTextField(
            value = email,
            onValueChange = { newEmail ->
                email = newEmail
                emailError = if (newEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    "Введите корректный email"
                } else {
                    null
                }
                viewModel.resetError()
            },
            label = { Text("Email") },
            isError = emailError != null || (uiState is LoginEmailUiState.Error),
            supportingText = {
                if (emailError != null) {
                    Text(emailError!!)
                } else if (uiState is LoginEmailUiState.Error) {
                    Text((uiState as LoginEmailUiState.Error).message)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка отправки
        Button(
            onClick = {
                viewModel.sendCode(email)
            },
            enabled = uiState !is LoginEmailUiState.Loading && emailError == null,
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