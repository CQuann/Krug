package com.example.krug.ui.screens.event.planning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState
import com.example.krug.ui.theme.KrugTheme

@Composable
fun CreateItemOrTaskScreen(
    title: String,
    items: List<String>,
    titleError: String?,
    itemErrors: Map<Int, String>,
    requestState: RequestState,
    isTask: Boolean = false,
    onTitleChange: (String) -> Unit,
    onItemChange: (Int, String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    onCreate: () -> Unit,
    onCancel: () -> Unit
) {
    val screenTitle = if (isTask) "Список задач" else "Список вещей"
    val itemLabel = if (isTask) "Задача" else "Пункт"

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(screenTitle, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Название") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Text("Список:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            items.forEachIndexed { index, item ->
                OutlinedTextField(
                    value = item,
                    onValueChange = { onItemChange(index, it) },
                    placeholder = { Text("$itemLabel ${index + 1}") },
                    isError = itemErrors.containsKey(index),
                    supportingText = { itemErrors[index]?.let { Text(it) } },
                    trailingIcon = {
                        if (items.size > 1) {
                            IconButton(onClick = { onRemoveItem(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Кнопка добавления пункта
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FloatingActionButton(
                    onClick = onAddItem,
                    modifier = Modifier.size(40.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }

            if (requestState is RequestState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(requestState.message, color = MaterialTheme.colorScheme.error)
            }
        }

        // Низ с кнопками
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
                    onClick = onCreate,
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

@Preview(showBackground = true)
@Composable
fun CreateListEmptyPreview() {
    KrugTheme {
        CreateItemOrTaskScreen(
            title = "",
            items = listOf(""),
            titleError = null,
            itemErrors = emptyMap(),
            requestState = RequestState.Idle,
            isTask = false,
            onTitleChange = {},
            onItemChange = { _, _ -> },
            onAddItem = {},
            onRemoveItem = {},
            onCreate = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateListFilledPreview() {
    KrugTheme {
        CreateItemOrTaskScreen(
            title = "Для готовки",
            items = listOf("Картошка", "Маркошка", "Писька"),
            titleError = null,
            itemErrors = emptyMap(),
            requestState = RequestState.Idle,
            isTask = false,
            onTitleChange = {},
            onItemChange = { _, _ -> },
            onAddItem = {},
            onRemoveItem = {},
            onCreate = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTaskListFilledPreview() {
    KrugTheme {
        CreateItemOrTaskScreen(
            title = "Для готовки",
            items = listOf("Почистить картошку", "Порезать картошку", "Сварить картошку"),
            titleError = null,
            itemErrors = emptyMap(),
            requestState = RequestState.Idle,
            isTask = true,
            onTitleChange = {},
            onItemChange = { _, _ -> },
            onAddItem = {},
            onRemoveItem = {},
            onCreate = {},
            onCancel = {}
        )
    }
}