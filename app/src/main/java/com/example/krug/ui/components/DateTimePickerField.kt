package com.example.krug.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.ui.theme.KrugTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    label: String = "",
    date: LocalDate?,
    time: LocalTime?,
    onDateSelected: (LocalDate?) -> Unit,
    onTimeSelected: (LocalTime?) -> Unit,
    enableTime: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val dateText = date?.format(dateFormatter) ?: "Не выбрано"
    val timeText = time?.format(timeFormatter) ?: "Не выбрано"

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            border = BorderStroke(1.dp, if (enabled) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Строка даты
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (enabled) Modifier.clickable { showDatePicker = true }
                            else Modifier
                        )
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateText,
                        color = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        else if (date == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Выбрать дату",
                        tint = if (enabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                // Разделитель
                if (enableTime) {
                    HorizontalDivider(
                        modifier = Modifier
                            .alpha(0.5f)
                            .clip(MaterialTheme.shapes.medium),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Строка времени
                if (enableTime) {
                    val timeEnabled = date != null && enabled
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (timeEnabled) Modifier.clickable { showTimePicker = true }
                                else Modifier.alpha(if (enabled) 0.4f else 0.38f)
                            )
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeText,
                            color = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else if (time == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Выбрать время",
                            tint = if (timeEnabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
                        )
                    }
                }
            }
        }
    }

    // DatePickerDialog – открывается только если enabled
    if (showDatePicker && enabled) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    onDateSelected(millis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    })
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePickerDialog – открывается только если enabled
    if (showTimePicker && enableTime && enabled) {
        val timePickerState = rememberTimePickerState(
            initialHour = time?.hour ?: 0,
            initialMinute = time?.minute ?: 0,
            is24Hour = true
        )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Выберите время") },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}


@Preview(showBackground = true, name = "DateTime – только дата (пусто)")
@Composable
fun DateTimeOnlyEmptyPreview() {
    KrugTheme {
        DateTimePickerField(
            label = "Дата",
            date = null, time = null,
            onDateSelected = {}, onTimeSelected = {},
            enableTime = false
        )
    }
}

@Preview(showBackground = true, name = "DateTime – только дата (выбрано)")
@Composable
fun DateTimeOnlySelectedPreview() {
    KrugTheme {
        DateTimePickerField(
            label = "Дата",
            date = LocalDate.of(2026, 5, 15), time = null,
            onDateSelected = {}, onTimeSelected = {},
            enableTime = false,

        )
    }
}

@Preview(showBackground = true, name = "DateTime – дата+время (время заблокировано)")
@Composable
fun DateTimeWithTimeBlockedPreview() {
    KrugTheme {
        DateTimePickerField(
            label = "Начало",
            date = null, time = null,
            onDateSelected = {}, onTimeSelected = {},
            enableTime = true,
            enabled = false
        )
    }
}

@Preview(showBackground = true, name = "DateTime – дата+время (время активно)")
@Composable
fun DateTimeWithTimeActivePreview() {
    KrugTheme {
        DateTimePickerField(
            label = "Окончание",
            date = LocalDate.now(), time = LocalTime.of(18, 0),
            onDateSelected = {}, onTimeSelected = {},
            enableTime = true,

        )
    }
}