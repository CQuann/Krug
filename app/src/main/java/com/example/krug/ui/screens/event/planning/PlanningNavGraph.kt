package com.example.krug.ui.screens.event.planning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun PlanningNavGraph(eventId: String, onFullScreenMode: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val isCreateScreen = currentBackStackEntry?.destination?.route?.startsWith("create_") == true
    LaunchedEffect(isCreateScreen) {
        onFullScreenMode(isCreateScreen)
    }

    NavHost(
        navController = navController,
        startDestination = "planning_list/$eventId"
    ) {
        composable(
            route = "planning_list/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            val viewModel: EventPlanningViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val showTypeDialog by viewModel.showTypeDialog.collectAsStateWithLifecycle()
            val currentUserId = viewModel.getCurrentUserId()
            val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.loadModules()
            }

            PlanningListScreen(
                uiState = uiState,
                showTypeDialog = showTypeDialog,
                currentUserId = currentUserId,
                snackbarEvents = viewModel.snackbarEvents,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshModules() },
                onFabClick = { viewModel.onFabClick() },
                onDismissTypeDialog = { viewModel.dismissTypeDialog() },
                onSelectPoll = {
                    viewModel.dismissTypeDialog()
                    navController.navigate("create_poll/$eventId")
                },
                onSelectItemList = {
                    viewModel.dismissTypeDialog()
                    navController.navigate("create_item_list/$eventId")
                },
                onSelectTaskList = {
                    viewModel.dismissTypeDialog()
                    navController.navigate("create_task_list/$eventId")
                },
                onVotePoll = { pollId, indexes -> viewModel.votePoll(pollId, indexes) },
                onAssignItem = { type, mId, iId, assign -> viewModel.assignItem(type, mId, iId, assign) },
                onCompleteTask = { mId, iId, completed -> viewModel.completeTask(mId, iId, completed) }
            )
        }

        composable(
            route = "create_poll/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            val viewModel: CreatePollViewModel = hiltViewModel()
            val title by viewModel.title.collectAsStateWithLifecycle()
            val options by viewModel.options.collectAsStateWithLifecycle()
            val multipleChoice by viewModel.multipleChoice.collectAsStateWithLifecycle()
            val titleError by viewModel.titleError.collectAsStateWithLifecycle()
            val optionErrors by viewModel.optionErrors.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect {
                    navController.popBackStack()
                }
            }

            CreatePollScreen(
                title = title,
                options = options,
                multipleChoice = multipleChoice,
                titleError = titleError,
                optionErrors = optionErrors,
                requestState = requestState,
                snackbarEvents = viewModel.snackbarEvents,
                onQuestionChange = viewModel::updateTitle,
                onOptionChange = viewModel::updateOption,
                onAddOption = viewModel::addOption,
                onRemoveOption = viewModel::removeOption,
                onMultipleChoiceToggle = viewModel::toggleMultipleChoice,
                onCreatePoll = { viewModel.createPoll() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = "create_item_list/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            val viewModel: CreateListViewModel = hiltViewModel(key = "ItemList")
            LaunchedEffect(Unit) {
                viewModel.init(isTaskList = false)
                viewModel.navigationEvents.collect {
                    navController.popBackStack()
                }
            }
            val title by viewModel.title.collectAsStateWithLifecycle()
            val items by viewModel.items.collectAsStateWithLifecycle()
            val titleError by viewModel.titleError.collectAsStateWithLifecycle()
            val itemErrors by viewModel.itemErrors.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()

            CreateItemOrTaskScreen(
                isTask = false,
                title = title,
                items = items,
                titleError = titleError,
                itemErrors = itemErrors,
                requestState = requestState,
                onTitleChange = viewModel::updateTitle,
                onItemChange = viewModel::updateItem,
                onAddItem = viewModel::addItem,
                onRemoveItem = viewModel::removeItem,
                onCreate = { viewModel.create() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = "create_task_list/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            val viewModel: CreateListViewModel = hiltViewModel(key = "TaskList")
            LaunchedEffect(Unit) {
                viewModel.init(isTaskList = true)
                viewModel.navigationEvents.collect {
                    navController.popBackStack()
                }
            }
            val title by viewModel.title.collectAsStateWithLifecycle()
            val items by viewModel.items.collectAsStateWithLifecycle()
            val titleError by viewModel.titleError.collectAsStateWithLifecycle()
            val itemErrors by viewModel.itemErrors.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()

            CreateItemOrTaskScreen(
                isTask = true,
                title = title,
                items = items,
                titleError = titleError,
                itemErrors = itemErrors,
                requestState = requestState,
                onTitleChange = viewModel::updateTitle,
                onItemChange = viewModel::updateItem,
                onAddItem = viewModel::addItem,
                onRemoveItem = viewModel::removeItem,
                onCreate = { viewModel.create() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}