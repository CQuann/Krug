package com.example.krug.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.R
import com.example.krug.ui.theme.KrugTheme

@Composable
fun SplashScreen(onCheckAuth: () -> Unit) {
    LaunchedEffect(Unit) { onCheckAuth() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = (-40).dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Логотип",
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Название приложения
            Text(
                text = "КРУГ",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    KrugTheme {
        SplashScreen(onCheckAuth = {})
    }
}