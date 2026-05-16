package com.example.krug.ui.screens.event.planning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.ui.theme.KrugTheme

@Composable
fun SelectModuleTypeDialog(onDismiss: () -> Unit, onSelectPoll: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите тип модуля") },
        text = {
            Column {
                // Элемент опроса
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectPoll(); onDismiss() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Poll, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Опрос", style = MaterialTheme.typography.bodyLarge)
                        Text("Голосование с вариантами ответа", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                // Заглушки для других модулей
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp).alpha(0.5f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Список вещей", style = MaterialTheme.typography.bodyLarge)
                        Text("Скоро", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp).alpha(0.5f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TaskAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Список задач", style = MaterialTheme.typography.bodyLarge)
                        Text("Скоро", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {}, // не нужно
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun SelectModuleTypeDialogPreview() {
    KrugTheme {
        SelectModuleTypeDialog(onDismiss = {}, onSelectPoll = {})
    }
}