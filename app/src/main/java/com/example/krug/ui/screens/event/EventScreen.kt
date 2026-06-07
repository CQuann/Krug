package com.example.krug.ui.screens.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.event.Event
import com.example.krug.ui.screens.event.album.AlbumNavHost
import com.example.krug.ui.screens.event.planning.PlanningNavGraph
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.Constants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

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
    var isFullScreen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isFullScreen) {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onHeaderClick() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(40.dp)) {
                                AsyncImage(
                                    model = "${Constants.BASE_URL}/event-avatars/${eventId}",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
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
                ) {
                    Text(
                        "Ошибка: ${requestState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                RequestState.Idle, RequestState.Success -> {
                    val tabs = listOf("Чат", "План", "Альбом")
                    val pagerState = rememberPagerState(pageCount = { tabs.size })

                    if (!isFullScreen) {
                        SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                            tabs.forEachIndexed { index, title ->
                                Tab(

                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    text = { Text(title) }
                                )
                            }
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = !isFullScreen
                    ) { page ->
                        when (page) {
                            0 -> EmptyTabPlaceholder("Чат", Icons.AutoMirrored.Filled.Chat)
                            1 -> PlanningNavGraph(
                                eventId = eventId,
                                onFullScreenMode = { isFullScreen = it }
                            )
                            2 -> AlbumNavHost(
                                eventId = eventId,
                                onFullScreenMode = { isFullScreen = it }
                            )                        }
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