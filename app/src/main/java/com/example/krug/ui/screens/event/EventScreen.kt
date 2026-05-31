package com.example.krug.ui.screens.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.event.Event
import com.example.krug.ui.screens.event.planning.EventPlanningScreen
import com.example.krug.ui.screens.event.planning.EventPlanningViewModel
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.Constants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

// CompositionLocal для eventId – доступен всем вложенным компонентам
val LocalEventId = compositionLocalOf<String> { error("No EventId provided") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    event: Event?,
    requestState: RequestState,
    eventId: String,
    snackbarEvents: SharedFlow<String>,
    onHeaderClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    CompositionLocalProvider(LocalEventId provides eventId) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.clickable { onHeaderClick() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(36.dp)) {
                                AsyncImage(
                                    model = "${Constants.BASE_URL}/event-avatars/${event?.eventId}?t=${System.currentTimeMillis()}",
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = event?.title ?: "Загрузка...",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                when (requestState) {
                    RequestState.Loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                    is RequestState.Error -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("Ошибка: ${requestState.message}", color = MaterialTheme.colorScheme.error) }
                    RequestState.Idle, RequestState.Success -> {
                        var selectedTab by remember { mutableIntStateOf(1) }
                        val tabs = listOf("Чат", "Планирование", "Альбом")

                        SecondaryTabRow(selectedTabIndex = selectedTab) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(title) }
                                )
                            }
                        }

                        when (selectedTab) {
                            0 -> EmptyTabPlaceholder("Чат", Icons.AutoMirrored.Filled.Chat)
                            1 -> {
                                val planningViewModel: EventPlanningViewModel = hiltViewModel()
                                val planningUiState by planningViewModel.uiState.collectAsStateWithLifecycle()
                                val creationMode by planningViewModel.creationMode.collectAsStateWithLifecycle()
                                val showTypeDialog by planningViewModel.showTypeDialog.collectAsStateWithLifecycle()

                                EventPlanningScreen(
                                    uiState = planningUiState,
                                    creationMode = creationMode,
                                    showTypeDialog = showTypeDialog,
                                    currentUserId = planningViewModel.getCurrentUserId(),
                                    snackbarEvents = planningViewModel.snackbarEvents,
                                    onFabClick = { planningViewModel.onFabClick() },
                                    onDismissTypeDialog = { planningViewModel.dismissTypeDialog() },
                                    onSelectPoll = { planningViewModel.startCreatingPoll() },
                                    onSelectItemList = { planningViewModel.startCreatingItemList() },
                                    onSelectTaskList = { planningViewModel.startCreatingTaskList() },
                                    onCreationFinished = { planningViewModel.onCreationFinished() },
                                    onVotePoll = { pollId, indexes -> planningViewModel.votePoll(pollId, indexes) },
                                    onAssignItem = { type, mId, iId, assign -> planningViewModel.assignItem(type, mId, iId, assign) },
                                    onCompleteTask = { mId, iId, completed -> planningViewModel.completeTask(mId, iId, completed) }
                                )
                            }
                            2 -> EmptyTabPlaceholder("Альбом", Icons.Default.PhotoLibrary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTabPlaceholder(text: String, icon: ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "$text появится позже",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "EventScreen – успех")
@Composable
fun EventScreenPreview() {
    KrugTheme {
        CompositionLocalProvider(LocalEventId provides "1") {
            EventScreen(
                event = Event(
                    eventId = "1",
                    title = "Пикник",
                    color = "#FF5733",
                    status = "active",
                    description = null,
                    location = "Парк",
                    startDateTime = "2026-05-10T15:00Z",
                    endDateTime = "2026-05-10T18:00Z"
                ),
                requestState = RequestState.Idle,
                eventId = "1",
                snackbarEvents = MutableSharedFlow(),
                onHeaderClick = {},
                onBackClick = {}
            )
        }
    }
}