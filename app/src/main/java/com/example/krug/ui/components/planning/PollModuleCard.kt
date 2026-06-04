package com.example.krug.ui.components.planning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.planning.PlanningModule
import com.example.krug.data.model.planning.PollData
import com.example.krug.ui.theme.KrugTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PollModuleCard(
    module: PlanningModule,
    onVote: (String, List<Int>) -> Unit
) {
    val pollData = module.data as? PollData ?: return
    val hasVoted = pollData.own_vote.isNotEmpty()
    val selectedIndexes = remember { mutableStateListOf<Int>().apply { addAll(pollData.own_vote) } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            pollData.options.forEachIndexed { index, option ->
                val count = pollData.votes_count?.getOrNull(index) ?: 0
                val total = pollData.votes_count?.sum() ?: 0
                val progress = if (total > 0) count.toFloat() / total else 0f
                val isSelectedByUser = pollData.own_vote.contains(index)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (!hasVoted) {
                                Modifier.clickable {
                                    if (pollData.multiple_choice) {
                                        if (selectedIndexes.contains(index))
                                            selectedIndexes.remove(index)
                                        else
                                            selectedIndexes.add(index)
                                    } else {
                                        selectedIndexes.clear()
                                        selectedIndexes.add(index)
                                    }
                                }
                            } else Modifier
                        ),
                    color = when {
                        isSelectedByUser && hasVoted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        selectedIndexes.contains(index) && !hasVoted -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!hasVoted) {
                            // Иконка, имитирующая чекбокс или радиокнопку
                            Icon(
                                imageVector = if (pollData.multiple_choice) {
                                    if (selectedIndexes.contains(index)) Icons.Filled.CheckBox
                                    else Icons.Filled.CheckBoxOutlineBlank
                                } else {
                                    if (selectedIndexes.contains(index)) Icons.Filled.RadioButtonChecked
                                    else Icons.Filled.RadioButtonUnchecked
                                },
                                contentDescription = null,
                                tint = if (selectedIndexes.contains(index)) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (hasVoted) {
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (isSelectedByUser) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }

                        if (hasVoted) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (!hasVoted) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onVote(module.id, selectedIndexes.toList()) },
                    enabled = selectedIndexes.isNotEmpty(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Проголосовать")
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Вы проголосовали",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "PollCard – не голосовал")
@Composable
fun PollCardNotVotedPreview() {
    KrugTheme {
        PollModuleCard(
            module = PlanningModule(
                id = "poll1",
                type = "poll",
                title = "Куда идём?",
                data = PollData(
                    options = listOf("Парк", "Кафе", "Кино"),
                    multiple_choice = false,
                    votes_count = listOf(0, 0, 0),
                    own_vote = emptyList()
                )
            ),
            onVote = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "PollCard – множественный выбор не голосовал")
@Composable
fun PollCardMultipleNotVotedPreview() {
    KrugTheme {
        PollModuleCard(
            module = PlanningModule(
                id = "poll2",
                type = "poll",
                title = "Что взять?",
                data = PollData(
                    options = listOf("Зонт", "Крем", "Вода"),
                    multiple_choice = true,
                    votes_count = listOf(0, 0, 0),
                    own_vote = emptyList()
                )
            ),
            onVote = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "PollCard – проголосовал")
@Composable
fun PollCardVotedPreview() {
    KrugTheme {
        PollModuleCard(
            module = PlanningModule(
                id = "poll3",
                type = "poll",
                title = "Куда идём?",
                data = PollData(
                    options = listOf("Парк", "Кафе", "Кино"),
                    multiple_choice = false,
                    votes_count = listOf(5, 3, 1),
                    own_vote = listOf(0)
                )
            ),
            onVote = { _, _ -> }
        )
    }
}