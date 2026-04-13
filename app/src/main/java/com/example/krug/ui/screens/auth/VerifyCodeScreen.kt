package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun VerifyCodeScreen(
    navController: NavController,
    email: String
) {
    var code by rememberSaveable { mutableStateOf("") }
    // Потом буду получать ответом на отправку кода верификационного
    val isNewUser = true

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Код отправлен на $email", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Код из письма") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (isNewUser) {
                    // Новый пользователь: идём на регистрацию
                    // Временно токен = "fake_token"
                    navController.navigate("register_profile/$email/fake_token")
                } else {
                    // Существующий: сразу на главный
                    navController.navigate("main_app") {
                        popUpTo("login_email") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подтвердить")
        }
    }
}