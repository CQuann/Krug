package com.example.krug.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.UserData

@Composable
fun MainAppScreen(
    userData: UserData?,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            error != null -> {
                Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRefresh) {
                    Text("Повторить")
                }
            }
            userData != null -> {
                Text("Добро пожаловать, ${userData.display_name}!")
                Text("Username: ${userData.username}")
                Text("Email: ${userData.email}")
                userData.birthday?.let {
                    Text("Дата рождения: $it")
                }
            }
            else -> Text("Нет данных")
        }
    }
}