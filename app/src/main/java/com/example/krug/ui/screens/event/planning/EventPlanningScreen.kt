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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.krug.ui.components.planning.ItemListModuleCard
import com.example.krug.ui.components.planning.PollModuleCard
import com.example.krug.ui.screens.event.planning.EventPlanningViewModel.CreationMode
import com.example.krug.ui.screens.event.planning.EventPlanningViewModel.PlanningUiState

@Composable
fun EventPlanningScreen(
    viewModel: EventPlanningViewModel,
    uiState: PlanningUiState,
    creationMode: CreationMode,
    showTypeDialog: Boolean
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (creationMode == CreationMode.None) {
                FloatingActionButton(onClick = { viewModel.onFabClick() }) {
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
                                Button(onClick = { viewModel.loadModules() }) {
                                    Text("Повторить")
                                }
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
                                                currentUserId = viewModel.getCurrentUserId(),
                                                onVote = { pollId, indexes -> viewModel.votePoll(pollId, indexes) }
                                            )
                                            "item_list" -> ItemListModuleCard(
                                                module = module,
                                                currentUserId = viewModel.getCurrentUserId(),
                                                onAssign = { type, moduleId, itemId, assign ->
                                                    viewModel.assignItem(type, moduleId, itemId, assign)
                                                },
                                                onComplete = null
                                            )
                                            "task_list" -> ItemListModuleCard(
                                                module = module,
                                                currentUserId = viewModel.getCurrentUserId(),
                                                onAssign = { type, moduleId, itemId, assign ->
                                                    viewModel.assignItem(type, moduleId, itemId, assign)
                                                },
                                                onComplete = { moduleId, itemId, completed ->
                                                    viewModel.completeTask(moduleId, itemId, completed)
                                                }
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
                            viewModel.onCreationFinished()
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
                        onCreatePoll = { pollViewModel.createPoll(viewModel.getEventId()) },
                        onCancel = { viewModel.onCreationFinished() }
                    )
                }
                CreationMode.ItemList -> {
                    CreateListScreenWrapper(
                        isTask = false,
                        eventPlanningViewModel = viewModel
                    )
                }
                CreationMode.TaskList -> {
                    CreateListScreenWrapper(
                        isTask = true,
                        eventPlanningViewModel = viewModel
                    )
                }
            }
        }
    }

    if (showTypeDialog) {
        SelectModuleTypeDialog(
            onDismiss = { viewModel.dismissTypeDialog() },
            onSelectPoll = { viewModel.startCreatingPoll() },
            onSelectItemList = { viewModel.startCreatingItemList() },
            onSelectTaskList = { viewModel.startCreatingTaskList() }
        )
    }
}

@Composable
private fun CreateListScreenWrapper(
    isTask: Boolean,
    eventPlanningViewModel: EventPlanningViewModel
) {
    val listViewModel: CreateListViewModel = hiltViewModel(
        key = if (isTask) "TaskList" else "ItemList"
    )
    // Инициализируем тип списка
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
            eventPlanningViewModel.onCreationFinished()
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
        onCreate = { listViewModel.create(eventPlanningViewModel.getEventId()) },
        onCancel = { eventPlanningViewModel.onCreationFinished() }
    )
}