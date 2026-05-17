package com.example.krug.ui.screens.event.planning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.ui.theme.KrugTheme

@Composable
fun SelectModuleTypeDialog(
    onDismiss: () -> Unit,
    onSelectPoll: () -> Unit,
    onSelectItemList: () -> Unit,
    onSelectTaskList: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите тип модуля") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectPoll(); onDismiss() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Poll, contentDescription = null)
                    Spacer(Modifier.width(15.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("Опрос", style = MaterialTheme.typography.bodyLarge)
                        Text("Голосование с вариантами ответа", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectItemList(); onDismiss() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null)
                    Spacer(Modifier.width(15.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("Список вещей", style = MaterialTheme.typography.bodyLarge)
                        Text("Каждый может взять ответственность за пункт", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTaskList(); onDismiss() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TaskAlt, contentDescription = null)
                    Spacer(Modifier.width(15.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("Список задач", style = MaterialTheme.typography.bodyLarge)
                        Text("Ответственные и отметки о выполнении", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        confirmButton = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SelectModuleTypeDialogPreview() {
    KrugTheme {
        SelectModuleTypeDialog(
            onDismiss = {},
            onSelectPoll = {},
            onSelectItemList = {},
            onSelectTaskList = {}
        )
    }
}