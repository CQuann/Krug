package com.example.krug.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CodeInputField(
    length: Int = 6,
    onCodeChanged: (String) -> Unit = {},   // вызывается при каждом изменении (для сброса ошибки)
    onCodeCompleted: (String) -> Unit       // вызывается когда код полностью введён
) {
    var code by remember { mutableStateOf("") }
    val focusRequesters = List(length) { remember { FocusRequester() } }

    // Автоматический переход фокуса при вводе и вызове onCodeCompleted
    LaunchedEffect(code) {
        val currentLength = code.length
        if (currentLength in 1..<length) {
            focusRequesters.getOrNull(currentLength)?.requestFocus()
        }
        if (currentLength == length) {
            onCodeCompleted(code)
        }
    }

    // Обработка изменения кода
    fun updateCode(newCode: String) {
        val filtered = newCode.filter { it.isDigit() }.take(length)
        if (filtered != code) {
            code = filtered
            onCodeChanged(code)  // сообщаем об изменении (можно сбросить ошибку)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until length) {
            val char = code.getOrNull(i)?.toString() ?: ""
            val isFocused = code.length == i

            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .then(if (isFocused) Modifier else Modifier) // можно добавить обводку при фокусе
                ) {
                    BasicTextField(
                        value = char,
                        onValueChange = { newChar ->
                            if (newChar.length <= 1 && (newChar.isEmpty() || newChar[0].isDigit())) {
                                val newCode = if (newChar.isNotEmpty()) {
                                    code.substring(0, i) + newChar + code.substring(i + 1)
                                } else {
                                    code.substring(0, i) + code.substring(i + 1)
                                }
                                updateCode(newCode)
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
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
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}