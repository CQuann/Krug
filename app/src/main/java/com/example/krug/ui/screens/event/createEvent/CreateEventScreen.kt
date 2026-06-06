package com.example.krug.ui.screens.event.createEvent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.data.model.RequestState
import com.example.krug.ui.components.ColorPickerField
import com.example.krug.ui.components.DateTimePickerField
import com.example.krug.ui.theme.KrugTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    formData: EventFormData,
    requestState: RequestState,
    titleError: String?,
    snackbarEvents: SharedFlow<String>,
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarEvents.collect { msg -> snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                "Новое событие",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = formData.title,
                    onValueChange = onTitleChange,
                    label = { Text("Название *") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = titleError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.location,
                    onValueChange = onLocationChange,
                    label = { Text("Местоположение") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(2.dp))

                DateTimePickerField(
                    label = "Начало",
                    date = formData.startDate,
                    time = formData.startTime,
                    onDateSelected = onStartDateChange,
                    onTimeSelected = onStartTimeChange,
                    enableTime = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                DateTimePickerField(
                    label = "Окончание",
                    date = formData.endDate,
                    time = formData.endTime,
                    onDateSelected = onEndDateChange,
                    onTimeSelected = onEndTimeChange,
                    enableTime = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                ColorPickerField(
                    selectedColor = formData.color,
                    onColorSelected = { onColorChange(it) },
                    label = "Цвет события",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Surface(tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    if (requestState is RequestState.Error) {
                        Text(
                            text = requestState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Button(
                        onClick = onSaveClick,
                        enabled = requestState !is RequestState.Loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (requestState is RequestState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Создать", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

// Preview

@Preview(showBackground = true, name = "CreateEvent – пустая форма")
@Composable
fun CreateEventScreenPreviewEmpty() {
    KrugTheme {
        CreateEventScreen(
            formData = EventFormData(),
            requestState = RequestState.Idle,
            titleError = null,
            snackbarEvents = MutableSharedFlow(),
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
            requestState = RequestState.Idle,
            titleError = "Введите название",
            snackbarEvents = MutableSharedFlow(),
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
            requestState = RequestState.Loading,
            titleError = null,
            snackbarEvents = MutableSharedFlow(),
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