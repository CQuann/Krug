package com.example.krug.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CodeInputField(
    length: Int = 6,
    onCodeChanged: (String) -> Unit = {},
    onCodeCompleted: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    val focusRequesters = List(length) { remember { FocusRequester() } }

    fun updateCode(newCode: String) {
        val filtered = newCode.filter { it.isDigit() }.take(length)
        if (filtered != code) {
            code = filtered
            onCodeChanged(code)
        }
    }

    // Переход фокуса вперёд и вызов завершения
    LaunchedEffect(code) {
        val currentLength = code.length
        if (currentLength in 1 until length) {
            focusRequesters.getOrNull(currentLength)?.requestFocus()
        }
        if (currentLength == length) {
            onCodeCompleted(code)
        }
    }

    // Переход фокуса назад при удалении (Backspace)
    LaunchedEffect(code) {
        delay(10)
        val currentLength = code.length
        if (currentLength < length && currentLength >= 0) {
            if (currentLength > 0) {
                focusRequesters.getOrNull(currentLength - 1)?.requestFocus()
            } else {
                focusRequesters.firstOrNull()?.requestFocus()
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until length) {
            val char = code.getOrNull(i)?.toString() ?: ""
            val isFocused = code.length == i

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f) // гарантирует квадратную форму
                    .clip(RoundedCornerShape(12.dp))
                    .then(if (isFocused) Modifier else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxSize() // теперь квадрат
                ) {
                    BasicTextField(
                        value = char,
                        onValueChange = { newChar ->
                            if (newChar.length <= 1 && (newChar.isEmpty() || newChar[0].isDigit())) {
                                val newCode = buildString {
                                    if (newChar.isNotEmpty()) {
                                        append(code.substring(0, i))
                                        append(newChar)
                                        append(code.substring(i + 1))
                                    } else {
                                        append(code.substring(0, i))
                                        append(code.substring(i + 1))
                                    }
                                }
                                updateCode(newCode)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequesters[i]),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (i == length - 1) ImeAction.Done else ImeAction.Next
                        ),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CodeInputFieldPreview() {
    CodeInputField(length = 6, onCodeCompleted = {})
}