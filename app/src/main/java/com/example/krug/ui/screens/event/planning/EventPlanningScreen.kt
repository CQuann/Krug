package com.example.krug.ui.screens.event.planning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.krug.data.model.planning.ItemListData
import com.example.krug.data.model.planning.PlanItem
import com.example.krug.data.model.planning.PlanningModule
import com.example.krug.data.model.planning.PollData
import com.example.krug.data.model.planning.TaskListData
import com.example.krug.ui.components.planning.ItemListModuleCard
import com.example.krug.ui.components.planning.PollModuleCard
import com.example.krug.ui.screens.event.LocalEventId
import com.example.krug.ui.screens.event.planning.EventPlanningViewModel.CreationMode
import com.example.krug.ui.screens.event.planning.EventPlanningViewModel.PlanningUiState
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun EventPlanningScreen(
    uiState: PlanningUiState,
    creationMode: CreationMode,
    showTypeDialog: Boolean,
    currentUserId: String?,
    snackbarEvents: SharedFlow<String>,
    onFabClick: () -> Unit,
    onDismissTypeDialog: () -> Unit,
    onSelectPoll: () -> Unit,
    onSelectItemList: () -> Unit,
    onSelectTaskList: () -> Unit,
    onCreationFinished: () -> Unit,
    onVotePoll: (String, List<Int>) -> Unit,
    onAssignItem: (String, String, String, Boolean) -> Unit,
    onCompleteTask: (String, String, Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val eventId = LocalEventId.current

    LaunchedEffect(Unit) {
        snackbarEvents.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (creationMode == CreationMode.None) {
                FloatingActionButton(onClick = onFabClick) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить модуль")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (creationMode) {
                CreationMode.None -> {
                    when (uiState) {
                        is PlanningUiState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is PlanningUiState.Error -> {
                            Column(
                                Modifier.fillMaxSize().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(uiState.message, color = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                        is PlanningUiState.Content -> {
                            if (uiState.modules.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Модули планирования пока не добавлены")
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(uiState.modules) { module ->
                                        when (module.type) {
                                            "poll" -> PollModuleCard(
                                                module = module,
                                                currentUserId = currentUserId,
                                                onVote = { pollId, indexes -> onVotePoll(pollId, indexes) }
                                            )
                                            "item_list" -> ItemListModuleCard(
                                                module = module,
                                                currentUserId = currentUserId,
                                                onAssign = { type, mId, iId, assign -> onAssignItem(type, mId, iId, assign) },
                                                onComplete = null
                                            )
                                            "task_list" -> ItemListModuleCard(
                                                module = module,
                                                currentUserId = currentUserId,
                                                onAssign = { type, mId, iId, assign -> onAssignItem(type, mId, iId, assign) },
                                                onComplete = { mId, iId, completed -> onCompleteTask(mId, iId, completed) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                CreationMode.Poll -> {
                    val pollViewModel: CreatePollViewModel = hiltViewModel()
                    val pollTitle by pollViewModel.title.collectAsStateWithLifecycle()
                    val pollOptions by pollViewModel.options.collectAsStateWithLifecycle()
                    val multipleChoice by pollViewModel.multipleChoice.collectAsStateWithLifecycle()
                    val titleError by pollViewModel.titleError.collectAsStateWithLifecycle()
                    val optionErrors by pollViewModel.optionErrors.collectAsStateWithLifecycle()
                    val requestState by pollViewModel.requestState.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit) {
                        pollViewModel.navigationEvents.collect {
                            onCreationFinished()
                        }
                    }

                    CreatePollScreen(
                        title = pollTitle,
                        options = pollOptions,
                        multipleChoice = multipleChoice,
                        titleError = titleError,
                        optionErrors = optionErrors,
                        requestState = requestState,
                        snackbarEvents = pollViewModel.snackbarEvents,
                        onQuestionChange = pollViewModel::updateTitle,
                        onOptionChange = pollViewModel::updateOption,
                        onAddOption = pollViewModel::addOption,
                        onRemoveOption = pollViewModel::removeOption,
                        onMultipleChoiceToggle = pollViewModel::toggleMultipleChoice,
                        onCreatePoll = { pollViewModel.createPoll(eventId) },
                        onCancel = { onCreationFinished() }
                    )
                }
                CreationMode.ItemList -> {
                    CreateListScreenWrapper(
                        isTask = false,
                        onCreationFinished = onCreationFinished
                    )
                }
                CreationMode.TaskList -> {
                    CreateListScreenWrapper(
                        isTask = true,
                        onCreationFinished = onCreationFinished
                    )
                }
            }
        }
    }

    if (showTypeDialog) {
        SelectModuleTypeDialog(
            onDismiss = onDismissTypeDialog,
            onSelectPoll = onSelectPoll,
            onSelectItemList = onSelectItemList,
            onSelectTaskList = onSelectTaskList
        )
    }
}

@Composable
private fun CreateListScreenWrapper(
    isTask: Boolean,
    onCreationFinished: () -> Unit
) {
    val eventId = LocalEventId.current
    val listViewModel: CreateListViewModel = hiltViewModel(
        key = if (isTask) "TaskList" else "ItemList"
    )
    LaunchedEffect(Unit) {
        listViewModel.init(isTaskList = isTask)
    }

    val title by listViewModel.title.collectAsStateWithLifecycle()
    val items by listViewModel.items.collectAsStateWithLifecycle()
    val titleError by listViewModel.titleError.collectAsStateWithLifecycle()
    val itemErrors by listViewModel.itemErrors.collectAsStateWithLifecycle()
    val requestState by listViewModel.requestState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        listViewModel.navigationEvents.collect {
            onCreationFinished()
        }
    }

    CreateItemOrTaskScreen(
        title = title,
        items = items,
        titleError = titleError,
        itemErrors = itemErrors,
        requestState = requestState,
        isTask = isTask,
        onTitleChange = listViewModel::updateTitle,
        onItemChange = listViewModel::updateItem,
        onAddItem = listViewModel::addItem,
        onRemoveItem = listViewModel::removeItem,
        onCreate = { listViewModel.create(eventId) },
        onCancel = { onCreationFinished() }
    )
}

@Preview(showBackground = true, name = "Planning – список модулей")
@Composable
fun EventPlanningWithModulesPreview() {
    KrugTheme {
        CompositionLocalProvider(LocalEventId provides "test_event") {
            EventPlanningScreen(
                uiState = PlanningUiState.Content(
                    modules = listOf(
                        PlanningModule(
                            id = "1", type = "poll", title = "Куда пойдём?",
                            data = PollData(
                                options = listOf("Парк", "Кафе", "Кино"),
                                multiple_choice = false,
                                votes_count = listOf(5, 3, 1),
                                own_vote = listOf(0)
                            )
                        ),
                        PlanningModule(
                            id = "2", type = "item_list", title = "Что принести",
                            data = ItemListData(
                                items = listOf(
                                    PlanItem(id = "1", text = "Мангал", assigned_user_id = null),
                                    PlanItem(
                                        id = "2",
                                        text = "Уголь",
                                        assigned_user_id = "user1",
                                        assigned_user_name = "Петр"
                                    )
                                )
                            )
                        ),
                        PlanningModule(
                            id = "3", type = "task_list", title = "Задачи",
                            data = TaskListData(
                                items = listOf(
                                    PlanItem(
                                        id = "1",
                                        text = "Купить билеты",
                                        assigned_user_id = "user1",
                                        assigned_user_name = "Петр",
                                        completed = false
                                    ),
                                    PlanItem(
                                        id = "2",
                                        text = "Забронировать стол",
                                        assigned_user_id = "user2",
                                        assigned_user_name = "Иван",
                                        completed = true
                                    )
                                )
                            )
                        )
                    )
                ),
                creationMode = CreationMode.None,
                showTypeDialog = false,
                currentUserId = "user1",
                snackbarEvents = MutableSharedFlow(),
                onFabClick = {},
                onDismissTypeDialog = {},
                onSelectPoll = {},
                onSelectItemList = {},
                onSelectTaskList = {},
                onCreationFinished = {},
                onVotePoll = { _, _ -> },
                onAssignItem = { _, _, _, _ -> },
                onCompleteTask = { _, _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true, name = "Planning – диалог выбора типа")
@Composable
fun EventPlanningDialogPreview() {
    KrugTheme {
        CompositionLocalProvider(LocalEventId provides "test_event") {
            EventPlanningScreen(
                uiState = PlanningUiState.Content(emptyList()),
                creationMode = CreationMode.None,
                showTypeDialog = true,
                currentUserId = "user1",
                snackbarEvents = MutableSharedFlow(),
                onFabClick = {},
                onDismissTypeDialog = {},
                onSelectPoll = {},
                onSelectItemList = {},
                onSelectTaskList = {},
                onCreationFinished = {},
                onVotePoll = { _, _ -> },
                onAssignItem = { _, _, _, _ -> },
                onCompleteTask = { _, _, _ -> }
            )
        }
    }
}