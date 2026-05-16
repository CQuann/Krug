package com.example.krug.ui.screens.event.planning

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState

@Composable
fun EventPlanningScreen(
    viewModel: EventPlanningViewModel,
    uiState: EventPlanningViewModel.PlanningUiState,
    pollQuestion: String,
    pollOptions: List<String>,
    multipleChoice: Boolean,
    questionError: String?,
    optionErrors: Map<Int, String>,
    requestState: RequestState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            EventPlanningViewModel.PlanningUiState.Idle -> {
                // Пока пустой список
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Модули планирования пока не добавлены")
                }
            }
            EventPlanningViewModel.PlanningUiState.SelectType -> {
                SelectModuleTypeDialog(
                    onDismiss = { viewModel.onCancelCreate() }, // возврат к Idle
                    onSelectPoll = { viewModel.onSelectPollType() }
                )
            }
            EventPlanningViewModel.PlanningUiState.CreatePoll -> {
                CreatePollScreen(
                    question = pollQuestion,
                    options = pollOptions,
                    multipleChoice = multipleChoice,
                    questionError = questionError,
                    optionErrors = optionErrors,
                    requestState = requestState,
                    onQuestionChange = viewModel::updateQuestion,
                    onOptionChange = viewModel::updateOption,
                    onAddOption = viewModel::addOption,
                    onRemoveOption = viewModel::removeOption,
                    onMultipleChoiceToggle = viewModel::toggleMultipleChoice,
                    onCreatePoll = viewModel::createPoll,
                    onCancel = viewModel::onCancelCreate
                )
            }
        }

        // FAB для добавления (только когда не в режиме создания)
        if (uiState != EventPlanningViewModel.PlanningUiState.CreatePoll) {
            FloatingActionButton(
                onClick = { viewModel.onFabClick() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить модуль")
            }
        }
    }
}