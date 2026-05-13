package com.example.krug.ui.screens.event.createEvent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.ui.components.ColorPickerField
import com.example.krug.ui.components.DateTimePickerField
import com.example.krug.ui.theme.KrugTheme
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    formData: EventFormData,
    uiState: CreateEventUiState,
    titleError: String?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onStartDateChange: (LocalDate?) -> Unit,
    onStartTimeChange: (LocalTime?) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    onEndTimeChange: (LocalTime?) -> Unit,
    onColorChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Новое событие", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = formData.title,
            onValueChange = onTitleChange,
            label = { Text("Название *") },
            isError = titleError != null,
            supportingText = titleError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formData.description,
            onValueChange = onDescriptionChange,
            label = { Text("Описание") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formData.location,
            onValueChange = onLocationChange,
            label = { Text("Местоположение") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Начало", style = MaterialTheme.typography.titleMedium)
        DateTimePickerField(
            date = formData.startDate,
            time = formData.startTime,
            onDateSelected = onStartDateChange,
            onTimeSelected = onStartTimeChange,
            enableTime = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Окончание", style = MaterialTheme.typography.titleMedium)
        DateTimePickerField(
            date = formData.endDate,
            time = formData.endTime,
            onDateSelected = onEndDateChange,
            onTimeSelected = onEndTimeChange,
            enableTime = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ColorPickerField(
            selectedColor = formData.color,
            onColorSelected = { onColorChange(it) },
            label = "Цвет события",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSaveClick,
            enabled = uiState !is CreateEventUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is CreateEventUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Создать")
            }
        }

        if (uiState is CreateEventUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true, name = "CreateEvent – пустая форма")
@Composable
fun CreateEventScreenPreviewEmpty() {
    KrugTheme {
        CreateEventScreen(
            formData = EventFormData(),
            uiState = CreateEventUiState.Idle,
            titleError = null,
            onTitleChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStartDateChange = {},
            onStartTimeChange = {},
            onEndDateChange = {},
            onEndTimeChange = {},
            onColorChange = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true, name = "CreateEvent – ошибки и данные")
@Composable
fun CreateEventScreenPreviewWithError() {
    KrugTheme {
        CreateEventScreen(
            formData = EventFormData(
                title = "",
                description = "Вечеринка",
                location = "Парк",
                startDate = LocalDate.now(),
                startTime = LocalTime.of(18, 0),
                color = "#FF5733"
            ),
            uiState = CreateEventUiState.Idle,
            titleError = "Введите название",
            onTitleChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStartDateChange = {},
            onStartTimeChange = {},
            onEndDateChange = {},
            onEndTimeChange = {},
            onColorChange = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true, name = "CreateEvent – загрузка")
@Composable
fun CreateEventScreenPreviewLoading() {
    KrugTheme {
        CreateEventScreen(
            formData = EventFormData(title = "Пикник"),
            uiState = CreateEventUiState.Loading,
            titleError = null,
            onTitleChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStartDateChange = {},
            onStartTimeChange = {},
            onEndDateChange = {},
            onEndTimeChange = {},
            onColorChange = {},
            onSaveClick = {}
        )
    }
}