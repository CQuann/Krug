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

// ui/components/DateTimePickerField.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    label: String = "",
    date: LocalDate?,
    time: LocalTime?,
    onDateSelected: (LocalDate?) -> Unit,
    onTimeSelected: (LocalTime?) -> Unit,
    enableTime: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val dateText = date?.format(dateFormatter) ?: "Не выбрано"
    val timeText = time?.format(timeFormatter) ?: "Не выбрано"

    Column(modifier = modifier) {
        // Общая подпись
        if (label != "") {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Контейнер с общей границей
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                // Строка даты
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateText,
                        color = if (date == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Выбрать дату",
                        tint = MaterialTheme.colorScheme.primary
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
                    val timeEnabled = date != null
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (timeEnabled) Modifier.clickable { showTimePicker = true }
                                else Modifier.alpha(0.4f)
                            )
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeText,
                            color = if (time == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Выбрать время",
                            tint = if (timeEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }

    // DatePickerDialog
    if (showDatePicker) {
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

    // TimePickerDialog
    if (showTimePicker && enableTime) {
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
            enableTime = false
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
            enableTime = true
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
            enableTime = true
        )
    }
}