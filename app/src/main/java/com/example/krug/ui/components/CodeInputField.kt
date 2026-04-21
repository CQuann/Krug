package com.example.krug.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CodeInputField(
    length: Int = 6,
    onCodeChanged: (String) -> Unit = {},
    onCodeCompleted: (String) -> Unit
) {
    var fieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val code = fieldValue.text.take(length)

    LaunchedEffect(code) {
        onCodeChanged(code)
        if (code.length == length) {
            onCodeCompleted(code)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val shake = remember { Animatable(0f) }

    Box {
        // 🔥 Скрытый input (без выделения и курсора)
        BasicTextField(
            value = fieldValue,
            onValueChange = { newValue ->

                val oldText = fieldValue.text
                val newTextRaw = newValue.text

                val isDeleting = newTextRaw.length < oldText.length

                val digits = newTextRaw.filter { it.isDigit() }.take(length)

                val cursor = newValue.selection.start

                if (isDeleting) {
                    val deleteIndex = fieldValue.selection.start

                    if (deleteIndex < oldText.length) {
                        // ✅ удаляем ТЕКУЩИЙ символ
                        val newText = buildString {
                            append(oldText)
                            deleteCharAt(deleteIndex)
                        }

                        fieldValue = TextFieldValue(
                            text = newText,
                            selection = TextRange(deleteIndex)
                        )
                    } else {
                        // fallback (если в конце)
                        fieldValue = TextFieldValue(
                            text = digits,
                            selection = TextRange(cursor.coerceAtMost(digits.length))
                        )
                    }

                } else {
                    // обычный ввод / paste
                    fieldValue = TextFieldValue(
                        text = digits,
                        selection = TextRange(cursor.coerceIn(0, digits.length))
                    )
                }
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .alpha(0f), // полностью скрыт
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            repeat(length) { i ->
                val char = code.getOrNull(i)?.toString() ?: ""
                val cursorIndex = fieldValue.selection.start.coerceAtMost(length - 1)
                val isFocused = cursorIndex == i

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            focusRequester.requestFocus()
                            fieldValue = fieldValue.copy(
                                selection = TextRange(i)
                            )
                            keyboardController?.show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isFocused)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isFocused)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else null
                    ) {
                        Box(contentAlignment = Alignment.Center) {

                            Text(
                                text = char,
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// region Previews
@Preview(showBackground = true, name = "Empty")
@Composable
fun CodeInputFieldEmptyPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CodeInputField(length = 6, onCodeCompleted = {})
            }
        }
    }
}

@Preview(showBackground = true, name = "Interactive")
@Composable
fun CodeInputFieldInteractivePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CodeInputField(
                    length = 6,
                    onCodeChanged = { code -> println("Code: $code") },
                    onCodeCompleted = { code -> println("Completed: $code") }
                )
            }
        }
    }
}
// endregion