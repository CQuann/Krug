package com.example.krug.ui.components.planning

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.planning.PlanningModule
import com.example.krug.data.model.planning.PollData
import com.example.krug.ui.theme.KrugTheme

@Composable
fun PollModuleCard(
    module: PlanningModule,
    currentUserId: String?,
    onVote: (String, List<Int>) -> Unit
) {
    val pollData = module.data as? PollData ?: return
    val hasVoted = pollData.own_vote.isNotEmpty()
    var selectedIndexes by remember(hasVoted, pollData.own_vote) {
        mutableStateOf(pollData.own_vote.toMutableList())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(module.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            pollData.options.forEachIndexed { index, option ->
                val count = pollData.votes_count?.getOrNull(index) ?: 0
                val total = pollData.votes_count?.sum() ?: 0
                val progress = if (total > 0) count.toFloat() / total else 0f
                val isSelectedByUser = pollData.own_vote.contains(index)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (!hasVoted) {
                                Modifier.clickable {
                                    if (pollData.multiple_choice) {
                                        if (selectedIndexes.contains(index)) selectedIndexes.remove(index)
                                        else selectedIndexes.add(index)
                                        selectedIndexes = selectedIndexes.toMutableList()
                                    } else {
                                        selectedIndexes = mutableListOf(index)
                                    }
                                }
                            } else Modifier
                        )
                        .padding(vertical = 6.dp)
                        .background(
                            if (isSelectedByUser && hasVoted) MaterialTheme.colorScheme.primaryContainer
                            else if (selectedIndexes.contains(index) && !hasVoted) MaterialTheme.colorScheme.secondaryContainer
                            else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!hasVoted) {
                        if (pollData.multiple_choice) {
                            Checkbox(
                                checked = selectedIndexes.contains(index),
                                onCheckedChange = null
                            )
                        } else {
                            RadioButton(
                                selected = selectedIndexes.contains(index),
                                onClick = null
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    }

                    Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                        Text(option, style = MaterialTheme.typography.bodyLarge)
                        if (hasVoted) {
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isSelectedByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }

                    if (hasVoted) {
                        Spacer(Modifier.width(8.dp))
                        Text("$count", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }

            if (!hasVoted) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onVote(module.id, selectedIndexes) },
                    enabled = selectedIndexes.isNotEmpty(),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Проголосовать")
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Вы проголосовали",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
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
            currentUserId = "user1",
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
            currentUserId = "user1",
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
            currentUserId = "user1",
            onVote = { _, _ -> }
        )
    }
}