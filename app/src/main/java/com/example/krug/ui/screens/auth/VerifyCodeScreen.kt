package com.example.krug.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState
import com.example.krug.ui.components.CodeInputField
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun VerifyCodeScreen(
    email: String,
    requestState: RequestState,
    canResend: Boolean,
    resendCooldown: Int,
    snackbarEvents: SharedFlow<String>,
    onCodeCompleted: (String) -> Unit,
    onResendCode: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Код подтверждения",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Мы отправили код на $email",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(36.dp))

            CodeInputField(
                length = 6,
                onCodeChanged = { /* ошибка сбрасывается во ViewModel */ },
                onCodeCompleted = onCodeCompleted
            )

            Spacer(Modifier.height(24.dp))

            if (requestState is RequestState.Error) {
                Text(
                    text = requestState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
            }

            if (requestState is RequestState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(16.dp))

            // Кнопка повторной отправки
            if (canResend) {
                TextButton(onClick = onResendCode) {
                    Text(
                        "Отправить код повторно",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                Text(
                    text = "Повторная отправка через ${resendCooldown} сек.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// --------------- Preview ---------------

@Preview(showBackground = true, name = "VerifyCode – ожидание ввода")
@Composable
fun VerifyCodeIdlePreview() {
    KrugTheme {
        VerifyCodeScreen(
            email = "test@example.com",
            requestState = RequestState.Idle,
            canResend = false,
            resendCooldown = 15,
            snackbarEvents = MutableSharedFlow(),
            onCodeCompleted = {},
            onResendCode = {}
        )
    }
}

@Preview(showBackground = true, name = "VerifyCode – ошибка")
@Composable
fun VerifyCodeErrorPreview() {
    KrugTheme {
        VerifyCodeScreen(
            email = "test@example.com",
            requestState = RequestState.Error("Неверный код"),
            canResend = false,
            resendCooldown = 8,
            snackbarEvents = MutableSharedFlow(),
            onCodeCompleted = {},
            onResendCode = {}
        )
    }
}

@Preview(showBackground = true, name = "VerifyCode – загрузка")
@Composable
fun VerifyCodeLoadingPreview() {
    KrugTheme {
        VerifyCodeScreen(
            email = "test@example.com",
            requestState = RequestState.Loading,
            canResend = false,
            resendCooldown = 3,
            snackbarEvents = MutableSharedFlow(),
            onCodeCompleted = {},
            onResendCode = {}
        )
    }
}

@Preview(showBackground = true, name = "VerifyCode – можно отправить повторно")
@Composable
fun VerifyCodeCanResendPreview() {
    KrugTheme {
        VerifyCodeScreen(
            email = "test@example.com",
            requestState = RequestState.Idle,
            canResend = true,
            resendCooldown = 0,
            snackbarEvents = MutableSharedFlow(),
            onCodeCompleted = {},
            onResendCode = {}
        )
    }
}