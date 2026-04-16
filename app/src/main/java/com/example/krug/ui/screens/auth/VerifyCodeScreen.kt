package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
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
fun VerifyCodeScreen(
    navController: NavController,
    email: String,
    viewModel: VerifyCodeViewModel = hiltViewModel()
) {
    var code by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    // Обработка навигации
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { navigation ->
            when (navigation) {
                is VerifyNavigation.GoToMain -> {
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.LoginEmail.route) { inclusive = true }
                    }
                }
                is VerifyNavigation.GoToRegister -> {
                    navController.navigate(
                        Screen.RegisterProfile.passArgs(navigation.email, navigation.tempToken)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Код подтверждения отправлен на $email")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { newCode ->
                code = newCode
                codeError = if (code.isBlank()) {
                    "Введите код"
                } else {
                    null
                }
                viewModel.resetError()
            },
            label = { Text("Код из письма") },
            isError = codeError != null || (uiState is VerifyCodeUiState.Error),
            supportingText = {
                if (codeError != null) {
                    Text(codeError!!)
                } else if (uiState is VerifyCodeUiState.Error) {
                    Text((uiState as VerifyCodeUiState.Error).message)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.verifyCode(email, code)
            },
            enabled = uiState !is VerifyCodeUiState.Loading && codeError == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is VerifyCodeUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Подтвердить")
            }
        }
    }
}