package com.example.krug.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.event.Event
import com.example.krug.utils.Constants
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.DateUtils
import com.example.krug.utils.EventColors
import java.time.format.DateTimeFormatter

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    val bgRes = EventColors.colors.find { it.hex == event.color }?.bgResId
        ?: R.drawable.bg_event_blue
    var hasAvatar by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box {
            // Фон
            Image(
                painter = painterResource(bgRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().matchParentSize()
            )

            Row(
                modifier = Modifier.padding(12.dp).height(90.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Текстовая колонка
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = if (hasAvatar) 12.dp else 0.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!event.location.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = event.location,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    val dateStr = formatEventDate(event.startDateTime, event.endDateTime)
                    if (dateStr != null) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Аватарка события
                if (hasAvatar) {
                    Box(modifier = Modifier.size(90.dp).clip(RoundedCornerShape(16.dp))) {
                        AsyncImage(
                            model = "${Constants.BASE_URL}/event-avatars/${event.eventId}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onError = { hasAvatar = false }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Форматирует диапазон дат/времени в человекочитаемую строку (использует java.time).
 */
fun formatEventDate(start: String?, end: String?): String? {
    if (start == null) return null
    val startDt = DateUtils.parseDate(start)
    val startTm = DateUtils.parseTime(start)
    val endDt = end?.let { DateUtils.parseDate(it) }
    val endTm = end?.let { DateUtils.parseTime(it) }

    val dateFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timeFmt = DateTimeFormatter.ofPattern("HH:mm")

    val startStr = buildString {
        startDt?.let { append(it.format(dateFmt)) }
        startTm?.let { append(" ${it.format(timeFmt)}") }
    }
    if (endDt == null && endTm == null) return startStr.ifEmpty { null }

    val endStr = buildString {
        endDt?.let { append(it.format(dateFmt)) }
        endTm?.let { append(" ${it.format(timeFmt)}") }
    }
    return "$startStr - $endStr"
}


@Preview(showBackground = true, name = "EventCard – все поля")
@Composable
fun EventCardFullPreview() {
    KrugTheme {
        EventCard(
            event = Event(
                eventId = "1",
                title = "Пикник в парке",
                location = "ЦПКиО",
                startDateTime = "2026-05-10T15:00:00Z",
                endDateTime = "2026-05-10T18:00:00Z",
                color = "#ABFDFD",   // Голубой
                status = "active",
                description = null
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "EventCard – без локации и времени")
@Composable
fun EventCardMinimalPreview() {
    KrugTheme {
        EventCard(
            event = Event(
                eventId = "2",
                title = "Встреча выпускников",
                startDateTime = "2026-06-01",
                endDateTime = null,
                color = "#FFE165",   // Желтый
                status = "active",
                description = null,
                location = null
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "EventCard – только название")
@Composable
fun EventCardNoDatePreview() {
    KrugTheme {
        EventCard(
            event = Event(
                eventId = "3",
                title = "День рождения",
                color = "#FFB469",   // Оранжевый
                status = "archived",
                description = null,
                location = null,
                startDateTime = null,
                endDateTime = null
            ),
            onClick = {}
        )
    }
}