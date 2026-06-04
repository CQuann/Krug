package com.example.krug.ui.screens.event.planning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.krug.ui.components.planning.ItemListModuleCard
import com.example.krug.ui.components.planning.PollModuleCard
import com.example.krug.ui.screens.event.planning.EventPlanningViewModel.PlanningUiState
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun PlanningListScreen(
    uiState: PlanningUiState,
    showTypeDialog: Boolean,
    currentUserId: String?,
    snackbarEvents: SharedFlow<String>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onFabClick: () -> Unit,
    onDismissTypeDialog: () -> Unit,
    onSelectPoll: () -> Unit,
    onSelectItemList: () -> Unit,
    onSelectTaskList: () -> Unit,
    onVotePoll: (String, List<Int>) -> Unit,
    onAssignItem: (String, String, String, Boolean) -> Unit,
    onCompleteTask: (String, String, Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { msg -> snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить модуль")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
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