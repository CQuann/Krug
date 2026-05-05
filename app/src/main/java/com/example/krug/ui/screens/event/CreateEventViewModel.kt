package com.example.krug.ui.screens.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.event.CreateEventRequest
import com.example.krug.data.repository.EventRepository
import com.example.krug.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _formData = MutableStateFlow(EventFormData())
    val formData: StateFlow<EventFormData> = _formData.asStateFlow()

    private val _uiState = MutableStateFlow<CreateEventUiState>(CreateEventUiState.Idle)
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<CreateEventNavigation>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun updateTitle(title: String) {
        _formData.value = _formData.value.copy(title = title)
        _titleError.value = null
    }

    fun updateDescription(desc: String) {
        _formData.value = _formData.value.copy(description = desc)
    }

    fun updateLocation(loc: String) {
        _formData.value = _formData.value.copy(location = loc)
    }

    fun updateStartDate(date: java.time.LocalDate?) {
        _formData.value = _formData.value.copy(startDate = date)
    }

    fun updateStartTime(time: java.time.LocalTime?) {
        _formData.value = _formData.value.copy(startTime = time)
    }

    fun updateEndDate(date: java.time.LocalDate?) {
        _formData.value = _formData.value.copy(endDate = date)
    }

    fun updateEndTime(time: java.time.LocalTime?) {
        _formData.value = _formData.value.copy(endTime = time)
    }

    fun updateColor(color: String) {
        _formData.value = _formData.value.copy(color = color)
    }

    fun createEvent() {
        val data = _formData.value
        if (data.title.isBlank()) {
            _titleError.value = "Введите название"
            return
        }

        val request = CreateEventRequest(
            title = data.title,
            description = data.description.ifBlank { null },
            location = data.location.ifBlank { null },
            startDateTime = DateUtils.toIsoString(data.startDate, data.startTime),
            endDateTime = DateUtils.toIsoString(data.endDate, data.endTime),
            color = data.color
        )

        viewModelScope.launch {
            _uiState.value = CreateEventUiState.Loading
            when (val result = eventRepository.createEvent(request)) {
                is DataResult.Success -> {
                    _uiState.value = CreateEventUiState.Idle
                    _navigationEvent.emit(CreateEventNavigation.GoToEventAvatarUpload(result.data.id))
                }

                is DataResult.Error -> {
                    _uiState.value = CreateEventUiState.Error(result.message)
                }
            }
        }
    }
}

sealed class CreateEventUiState {
    object Idle : CreateEventUiState()
    object Loading : CreateEventUiState()
    data class Error(val message: String) : CreateEventUiState()
}

sealed class CreateEventNavigation {
    data class GoToEventAvatarUpload(val eventId: String) : CreateEventNavigation()
}