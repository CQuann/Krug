package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.ui.components.CodeInputField
import com.example.krug.ui.theme.KrugTheme

@Composable
fun VerifyCodeScreen(
    email: String,
    uiState: VerifyCodeUiState,
    onCodeCompleted: (String) -> Unit,
    onResetError: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Код подтверждения отправлен на $email")
        Spacer(modifier = Modifier.height(24.dp))

        CodeInputField(
            length = 6,
            onCodeChanged = { onResetError() },
            onCodeCompleted = onCodeCompleted
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState is VerifyCodeUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (uiState is VerifyCodeUiState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyCodeScreenPreview() {
    KrugTheme {
        VerifyCodeScreen(
            email = "test@example.com",
            uiState = VerifyCodeUiState.Idle,
            onCodeCompleted = {},
            onResetError = {}
        )
    }
}
