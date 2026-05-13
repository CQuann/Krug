package com.example.krug.ui.screens.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.event.Event
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    event: Event?,
    uiState: EventUiState,
    onHeaderClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.clickable { onHeaderClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(36.dp)) {
                            val avatarUrl = event?.let { "${Constants.BASE_URL}/event-avatars/${it.id}" }
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.ic_default_event_avatar)
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
            when (uiState) {
                EventUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is EventUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                }
                EventUiState.Success -> {
                    var selectedTab by remember { mutableIntStateOf(1) } // 1 = Планирование
                    val tabs = listOf("Чат", "Планирование", "Альбом")

                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    when (selectedTab) {
                        0 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Чат пока недоступен")
                        }
                        1 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Планирование")  // позже здесь будет функционал
                        }
                        2 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Альбом пока недоступен")
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, name = "EventScreen – успех")
@Composable
fun EventScreenPreview() {
    KrugTheme {
        EventScreen(
            event = Event(
                id = "1",
                title = "Пикник",
                color = "#FF5733",
                status = "active",
                description = null,
                location = "Парк",
                startDateTime = "2026-05-10T15:00Z",
                endDateTime = "2026-05-10T18:00Z",
                createdBy = "",
                createdAt = ""
            ),
            uiState = EventUiState.Success,
            onHeaderClick = {},
            onBackClick = {}
        )
    }
}