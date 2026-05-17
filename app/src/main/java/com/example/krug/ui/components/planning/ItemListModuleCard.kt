package com.example.krug.ui.components.planning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.planning.ItemListData
import com.example.krug.data.model.planning.PlanItem
import com.example.krug.data.model.planning.PlanningModule
import com.example.krug.data.model.planning.TaskListData
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.Constants

@Composable
fun ItemListModuleCard(
    module: PlanningModule,
    currentUserId: String?,
    onAssign: (String, String, String, Boolean) -> Unit,
    onComplete: ((String, String, Boolean) -> Unit)? = null
) {
    val (items, isTask) = when (val d = module.data) {
        is ItemListData -> d.items to false
        is TaskListData -> d.items to true
        else -> return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(module.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            items.forEach { item ->
                val assignedToMe = item.assigned_user_id == currentUserId
                val assignedToOther = item.assigned_user_id != null && !assignedToMe
                val isCompleted = item.completed == true

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        assignedToMe -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        assignedToOther -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surface
                    },
                    tonalElevation = if (assignedToMe || assignedToOther) 1.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Индикатор статуса (аватар или иконка)
                        if (assignedToOther) {
                            AsyncImage(
                                model = "${Constants.BASE_URL}/avatars/${item.assigned_user_id}.jpg",
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                                error = painterResource(R.drawable.ic_default_avatar)
                            )
                        } else {
                            Icon(
                                imageVector = when {
                                    isCompleted -> Icons.Default.CheckCircle
                                    assignedToMe -> Icons.Default.Face
                                    else -> Icons.Default.Circle
                                },
                                contentDescription = null,
                                tint = when {
                                    isCompleted -> MaterialTheme.colorScheme.primary
                                    assignedToMe -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                },
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))

                        // Текст и подпись
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = when {
                                    isCompleted -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                            if (assignedToMe) {
                                Text(
                                    text = "Взято вами",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (assignedToOther) {
                                Text(
                                    text = item.assigned_user_name ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Действия
                        if (assignedToMe) {
                            TextButton(
                                onClick = { onAssign(module.type, module.id, item.id, false) }
                            ) {
                                Text("Отказ", style = MaterialTheme.typography.labelMedium)
                            }
                            if (isTask && !isCompleted) {
                                TextButton(
                                    onClick = { onComplete?.invoke(module.id, item.id, true) }
                                ) {
                                    Text("Выполнить", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        } else if (!assignedToOther) {
                            TextButton(
                                onClick = { onAssign(module.type, module.id, item.id, true) }
                            ) {
                                Text("Взять", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "ItemList – элегантный стиль")
@Composable
fun ItemListElegantPreview() {
    KrugTheme {
        ItemListModuleCard(
            module = PlanningModule(
                id = "items1",
                type = "item_list",
                title = "Что принести",
                data = ItemListData(
                    items = listOf(
                        PlanItem(id = "1", text = "Мангал", assigned_user_id = null),
                        PlanItem(id = "2", text = "Уголь", assigned_user_id = "user1", assigned_user_name = "Петр"),
                        PlanItem(id = "3", text = "Спички", assigned_user_id = "user2", assigned_user_name = null)
                    )
                )
            ),
            currentUserId = "user2",
            onAssign = { _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "TaskList – элегантный стиль")
@Composable
fun TaskListElegantPreview() {
    KrugTheme {
        ItemListModuleCard(
            module = PlanningModule(
                id = "tasks1",
                type = "task_list",
                title = "Задачи",
                data = TaskListData(
                    items = listOf(
                        PlanItem(id = "1", text = "Купить билеты", assigned_user_id = null, completed = false),
                        PlanItem(id = "2", text = "Забронировать стол", assigned_user_id = "user1", assigned_user_name = "Иван", completed = true),
                        PlanItem(id = "3", text = "Позвонить", assigned_user_id = "user2", completed = false)
                    )
                )
            ),
            currentUserId = "user2",
            onAssign = { _, _, _, _ -> },
            onComplete = { _, _, _ -> }
        )
    }
}