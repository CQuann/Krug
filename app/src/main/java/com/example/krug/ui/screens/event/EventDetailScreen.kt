package com.example.krug.ui.screens.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.event.Event
import com.example.krug.ui.components.DetailField
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.Constants
import com.example.krug.utils.DateUtils
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event?,
    uiState: EventDetailUiState,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали события") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            EventDetailUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is EventDetailUiState.Error -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Ошибка: ${uiState.message}", color = MaterialTheme.colorScheme.error)
            }
            EventDetailUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Большая аватарка
                    val avatarUrl = event?.let { "${Constants.BASE_URL}/event-avatars/${it.id}" }
                    Box(modifier = Modifier.size(120.dp)) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(R.drawable.ic_default_event_avatar)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = event?.title ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    DetailField("Местоположение", event?.location)
                    DetailField("Дата и время начала", DateUtils.formatFullDateTime(event?.startDateTime))
                    DetailField("Дата и время окончания", DateUtils.formatFullDateTime(event?.endDateTime))
                    DetailField("Описание", event?.description)
                    // Позже: поле Ссылка и участники
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "EventDetail – успех")
@Composable
fun EventDetailScreenPreview() {
    KrugTheme {
        EventDetailScreen(
            event = Event(
                id = "1",
                title = "Пикник",
                location = "ЦПКиО",
                startDateTime = "2026-05-10T15:00:00Z",
                endDateTime = "2026-05-10T18:00:00Z",
                description = null,
                color = "#FF5733",
                status = "active",
                createdBy = "",
                createdAt = ""
            ),
            uiState = EventDetailUiState.Success,
            onBackClick = {}
        )
    }
}