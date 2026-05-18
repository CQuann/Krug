package com.example.krug.ui.screens.main

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.UserData
import com.example.krug.data.model.event.Event
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.AvatarUrlProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    userId: String?,
    userData: UserData?,
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
    onRefresh: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (error != null) snackbarHostState.showSnackbar(error)
    }

    val avatarUrl = remember(userId) { if (userId != null) AvatarUrlProvider.build(userId) else null }

    val tabs = listOf("active" to "Активные", "archived" to "Архив")
    var selectedTabIndex by remember(currentStatus) { mutableIntStateOf(if (currentStatus == "active") 0 else 1) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Krug") },
                actions = {
                    Box(modifier = Modifier.size(40.dp)) {
                        if (avatarUrl != null) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .clickable{onEditProfileClick()},
                                error = painterResource(R.drawable.ic_default_avatar)
                            )
                        } else {
                            Icon(Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable{onEditProfileClick()})
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateEventClick) {
                Icon(Icons.Default.Add, contentDescription = "Создать событие")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, (status, label) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            onStatusChange(status)
                        },
                        text = { Text(label) }
                    )
                }
            }

            PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh) {
                if (events.isEmpty() && !isRefreshing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Нет событий")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(events.size) { index ->
                            val event = events[index]
                            EventCard(event = event, onClick = { onEventClick(event.id) })

                            // Автоматическая подгрузка при достижении последнего элемента
                            if (index == events.size - 1 && !isLoadingMore && events.size < totalEvents) {
                                LaunchedEffect(event.id) {
                                    onLoadMore()
                                }
                            }
                        }
                        if (isLoadingMore) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "MainApp – активные события")
@Composable
fun MainAppActivePreview() {
    KrugTheme {
        MainAppScreen(
//            userId = "123",
//            userData = UserData(email = "test@test.com", display_name = "Иван", birthday = null, username = "ivan"),
            userId = null,
            userData = null,
            events = listOf(
                Event(
                    id = "1",
                    title = "Пикник",
                    location = "Парк",
                    startDateTime = "2026-05-10T15:00Z",
                    endDateTime = "2026-05-10T18:00Z",
                    color = "#3498DB",
                    status = "active",
                    description = ""
                ),
                Event(
                    id = "2",
                    title = "Встреча",
                    location = "Кафе",
                    startDateTime = "2026-06-01",
                    color = "#FF5733",
                    status = "active",
                    description = "",
                    endDateTime = "",
                )
            ),
            currentStatus = "active",
            totalEvents = 2,
            isLoadingMore = false,
            isRefreshing = false,
            error = null,
            onStatusChange = {},
            onEventClick = {},
            onLoadMore = {},
            onCreateEventClick = {},
            onEditProfileClick = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "MainApp – архив пуст")
@Composable
fun MainAppArchiveEmptyPreview() {
    KrugTheme {
        MainAppScreen(
            userId = null,
            userData = null,
            events = emptyList(),
            currentStatus = "archived",
            totalEvents = 0,
            isLoadingMore = false,
            isRefreshing = false,
            error = null,
            onStatusChange = {},
            onEventClick = {},
            onLoadMore = {},
            onCreateEventClick = {},
            onEditProfileClick = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "MainApp – загрузка")
@Composable
fun MainAppLoadingPreview() {
    KrugTheme {
        MainAppScreen(
            userId = "1",
            userData = null,
            events = emptyList(),
            currentStatus = "active",
            totalEvents = 0,
            isLoadingMore = false,
            isRefreshing = true,
            error = null,
            onStatusChange = {},
            onEventClick = {},
            onLoadMore = {},
            onCreateEventClick = {},
            onEditProfileClick = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "MainApp – ошибка")
@Composable
fun MainAppErrorPreview() {
    KrugTheme {
        MainAppScreen(
            userId = null,
            userData = null,
            events = emptyList(),
            currentStatus = "active",
            totalEvents = 0,
            isLoadingMore = false,
            isRefreshing = false,
            error = "Сетевая ошибка",
            onStatusChange = {},
            onEventClick = {},
            onLoadMore = {},
            onCreateEventClick = {},
            onEditProfileClick = {},
            onRefresh = {}
        )
    }
}