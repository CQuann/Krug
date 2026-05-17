package com.example.krug.ui.screens.event.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState
import com.example.krug.ui.theme.KrugTheme

@Composable
fun CreatePollScreen(
    title: String,
    options: List<String>,
    multipleChoice: Boolean,
    questionError: String?,
    optionErrors: Map<Int, String>,
    requestState: RequestState,
    onQuestionChange: (String) -> Unit,
    onOptionChange: (Int, String) -> Unit,
    onAddOption: () -> Unit,
    onRemoveOption: (Int) -> Unit,
    onMultipleChoiceToggle: (Boolean) -> Unit,
    onCreatePoll: () -> Unit,
    onCancel: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Создание опроса", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onQuestionChange,
                label = { Text("Вопрос") },
                isError = questionError != null,
                supportingText = { questionError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Разрешить множественный выбор", modifier = Modifier.weight(1f))
                Switch(checked = multipleChoice, onCheckedChange = onMultipleChoiceToggle)
            }
            Spacer(Modifier.height(12.dp))

            Text("Варианты ответа:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            options.forEachIndexed { index, option ->
                OutlinedTextField(
                    value = option,
                    onValueChange = { onOptionChange(index, it) },
                    placeholder = { Text("Вариант ${index + 1}") },
                    isError = optionErrors.containsKey(index),
                    supportingText = { optionErrors[index]?.let { Text(it) } },
                    trailingIcon = {
                        if (options.size > 2) {
                            IconButton(onClick = { onRemoveOption(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Кнопка добавления варианта
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FloatingActionButton(
                    onClick = onAddOption,
                    modifier = Modifier.size(40.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить вариант")
                }
            }

            // Ошибка запроса
            if (requestState is RequestState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(requestState.message, color = MaterialTheme.colorScheme.error)
            }
        }

        Surface(tonalElevation = 8.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text("Отмена")
                }
                Button(
                    onClick = onCreatePoll,
                    enabled = requestState !is RequestState.Loading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (requestState is RequestState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Создать")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "CreatePoll – пустая форма")
@Composable
fun CreatePollEmptyPreview() {
    KrugTheme {
        CreatePollScreen(
            title = "",
            options = listOf("", ""),
            multipleChoice = false,
            questionError = null,
            optionErrors = emptyMap(),
            requestState = RequestState.Idle,
            onQuestionChange = {},
            onOptionChange = { _, _ -> },
            onAddOption = {},
            onRemoveOption = {},
            onMultipleChoiceToggle = {},
            onCreatePoll = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "CreatePoll – ошибки валидации")
@Composable
fun CreatePollErrorPreview() {
    KrugTheme {
        CreatePollScreen(
            title = "",
            options = listOf("Да", ""),
            multipleChoice = true,
            questionError = "Введите вопрос",
            optionErrors = mapOf(1 to "Заполните вариант"),
            requestState = RequestState.Idle,
            onQuestionChange = {},
            onOptionChange = { _, _ -> },
            onAddOption = {},
            onRemoveOption = {},
            onMultipleChoiceToggle = {},
            onCreatePoll = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "CreatePoll – загрузка")
@Composable
fun CreatePollLoadingPreview() {
    KrugTheme {
        CreatePollScreen(
            title = "Куда пойдём?",
            options = listOf("Парк", "Кафе", "Кино"),
            multipleChoice = false,
            questionError = null,
            optionErrors = emptyMap(),
            requestState = RequestState.Loading,
            onQuestionChange = {},
            onOptionChange = { _, _ -> },
            onAddOption = {},
            onRemoveOption = {},
            onMultipleChoiceToggle = {},
            onCreatePoll = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "CreatePoll – ошибка сети")
@Composable
fun CreatePollNetworkErrorPreview() {
    KrugTheme {
        CreatePollScreen(
            title = "Тест",
            options = listOf("A", "B"),
            multipleChoice = false,
            questionError = null,
            optionErrors = emptyMap(),
            requestState = RequestState.Error("Нет соединения"),
            onQuestionChange = {},
            onOptionChange = { _, _ -> },
            onAddOption = {},
            onRemoveOption = {},
            onMultipleChoiceToggle = {},
            onCreatePoll = {},
            onCancel = {}
        )
    }
}