// ui/screens/main/EventCard.kt
package com.example.krug.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.event.Event
import com.example.krug.utils.Constants
import androidx.core.graphics.toColorInt
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.DateUtils
import java.time.format.DateTimeFormatter

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent
            )
            .border(
                width = 2.dp,
                color = Color(event.color.toColorInt()),
                shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар события
            Box(modifier = Modifier.size(48.dp)) {
                AsyncImage(
                    model = "${Constants.BASE_URL}/event-avatars/${event.id}",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_default_event_avatar)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!event.location.isNullOrBlank()) {
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val dateStr = formatEventDate(event.startDateTime, event.endDateTime)
                if (dateStr != null) {
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Цветной маркер
            event.color.let { hex ->
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(hex.toColorInt()))
                )
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

// ---------- Previews ----------

@Preview(showBackground = true, name = "EventCard – все поля")
@Composable
fun EventCardFullPreview() {
    KrugTheme {
        EventCard(
            event = Event(
                id = "1",
                title = "Пикник в парке",
                location = "ЦПКиО",
                startDateTime = "2026-05-10T15:00:00Z",
                endDateTime = "2026-05-10T18:00:00Z",
                color = "#3498DB",
                status = "active",
                description = null,
                createdBy = "",
                createdAt = ""
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
                id = "2",
                title = "Встреча выпускников",
                startDateTime = "2026-06-01",
                endDateTime = null,
                color = "#FF5733",
                status = "active",
                description = null,
                location = null,
                createdBy = "",
                createdAt = ""
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
                id = "3",
                title = "День рождения",
                color = "#28B463",
                status = "archived",
                description = null,
                location = null,
                startDateTime = null,
                endDateTime = null,
                createdBy = "",
                createdAt = ""
            ),
            onClick = {}
        )
    }
}