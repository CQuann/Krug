package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.krug.ui.Screen


@Composable
fun RegisterProfileScreen(
    navController: NavController,
    email: String,
    tempToken: String
) {
    var displayName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var birthday by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Имя") }
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username  = it},
            label = { Text("Никнейм") }
        )
        OutlinedTextField(
            value = birthday,
            onValueChange = { birthday = it },
            label = { Text("Дата рождения (ГГГГ-ММ-ДД)") }
        )
        Button(
            onClick = {
                // Отправка на сервер данных для регистрации, получение токена, переход на главный
                navController.navigate(Screen.MainApp.route) {
                    // Чтобы при нажатии назад в main выходил из приложения, нельза вернуться
                    popUpTo(Screen.LoginEmail.route) { inclusive = true }
                }
            }
        ) {
            Text("Завершить регистрацию")
        }
    }
}