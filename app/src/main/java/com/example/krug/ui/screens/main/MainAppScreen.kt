package com.example.krug.ui.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.event.Event
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.AvatarUrlProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainAppScreen(
    userId: String?,
    events: List<Event>,
    currentStatus: String,
    totalEvents: Int,
    isLoadingMore: Boolean,
    isRefreshing: Boolean,
    error: String?,
    onStatusChange: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onCreateEventClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onRefresh: () -> Unit,
    showJoinDialog: Boolean,
    pendingJoinEvent: Event?,
    onDismissJoinDialog: () -> Unit,
    onNavigateToJoinedEvent: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(error) {
        if (error != null) snackbarHostState.showSnackbar(error)
    }

    val avatarUrl = remember(userId) { if (userId != null) AvatarUrlProvider.build(userId) else null }
    val tabs = listOf("active" to "Активные", "archived" to "Архив")
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = if (currentStatus == "active") 0 else 1)

    LaunchedEffect(pagerState.currentPage) {
        val newStatus = tabs[pagerState.currentPage].first
        if (newStatus != currentStatus) {
            onStatusChange(newStatus)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Krug", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onEditProfileClick() }
                    ) {
                        if (avatarUrl != null) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Аватар",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                error = painterResource(R.drawable.ic_default_avatar)
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Аватар",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateEventClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Создать событие")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column {
                PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, (_, label) ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = { Text(label) }
                        )
                    }
                }

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { _ ->
                        if (events.isEmpty() && !isRefreshing) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Нет событий", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(events.size) { index ->
                                    val event = events[index]
                                    EventCard(event = event, onClick = { onEventClick(event.eventId) })
                                    if (index == events.size - 1 && !isLoadingMore && events.size < totalEvents) {
                                        LaunchedEffect(event.eventId) { onLoadMore() }
                                    }
                                }
                                if (isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Диалог присоединения к событию
            if (showJoinDialog) {
                AlertDialog(
                    onDismissRequest = onDismissJoinDialog,
                    title = { Text("Приглашение принято!") },
                    text = {
                        Column {
                            Text("Вы вступили в событие:")
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = pendingJoinEvent?.title ?: "Новое событие",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            pendingJoinEvent?.eventId?.let { onNavigateToJoinedEvent(it) }
                        }) { Text("Перейти к событию") }
                    },
                    dismissButton = {
                        TextButton(onClick = onDismissJoinDialog) { Text("Позже") }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "MainApp – активные события")
@Composable
fun MainAppActivePreview() {
    KrugTheme {
        MainAppScreen(
            userId = null,
            events = listOf(
                Event(eventId = "1", title = "Пикник", location = "Парк",
                    startDateTime = "2026-05-10T15:00Z", endDateTime = "2026-05-10T18:00Z",
                    color = "#3498DB", status = "active", description = ""),
                Event(eventId = "2", title = "Встреча", location = "Кафе",
                    startDateTime = "2026-06-01", color = "#FF5733", status = "active",
                    description = "", endDateTime = "")
            ),
            currentStatus = "active", totalEvents = 2, isLoadingMore = false,
            isRefreshing = false, error = null,
            onStatusChange = {}, onEventClick = {}, onLoadMore = {},
            onCreateEventClick = {}, onEditProfileClick = {}, onRefresh = {},
            showJoinDialog = false,
            pendingJoinEvent = null,
            onDismissJoinDialog = {},
            onNavigateToJoinedEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "MainApp – загрузка")
@Composable
fun MainAppLoadingPreview() {
    KrugTheme {
        MainAppScreen(
            userId = "1", events = emptyList(),
            currentStatus = "active", totalEvents = 0, isLoadingMore = false,
            isRefreshing = true, error = null,
            onStatusChange = {}, onEventClick = {}, onLoadMore = {},
            onCreateEventClick = {}, onEditProfileClick = {}, onRefresh = {},
            showJoinDialog = false,
            pendingJoinEvent = null,
            onDismissJoinDialog = {},
            onNavigateToJoinedEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "MainApp – диалог присоединения")
@Composable
fun MainAppJoinDialogPreview() {
    KrugTheme {
        MainAppScreen(
            userId = null,
            events = listOf(
                Event(eventId = "1", title = "Пикник", location = "Парк",
                    startDateTime = "2026-05-10T15:00Z", endDateTime = "2026-05-10T18:00Z",
                    color = "#3498DB", status = "active", description = "")
            ),
            currentStatus = "active", totalEvents = 1, isLoadingMore = false,
            isRefreshing = false, error = null,
            onStatusChange = {}, onEventClick = {}, onLoadMore = {},
            onCreateEventClick = {}, onEditProfileClick = {}, onRefresh = {},
            showJoinDialog = true,
            pendingJoinEvent = Event(eventId = "3", title = "Новое событие", location = "",
                startDateTime = null, endDateTime = null, color = "#FF5733", status = "active", description = ""),
            onDismissJoinDialog = {},
            onNavigateToJoinedEvent = {}
        )
    }
}