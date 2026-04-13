package com.example.krug.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.krug.ui.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(3000)
        // проверить токен
        navController.navigate(Screen.LoginEmail.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}