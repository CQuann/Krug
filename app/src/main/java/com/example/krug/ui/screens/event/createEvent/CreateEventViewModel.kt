package com.example.krug.ui.screens.event.createEvent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.event.CreateEventRequest
import com.example.krug.data.repository.EventRepository
import com.example.krug.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _formData = MutableStateFlow(EventFormData())
    val formData: StateFlow<EventFormData> = _formData.asStateFlow()

    // Единое состояние операции (загрузка, ошибка)
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    // Навигационные события (переход к загрузке аватара)
    private val _navigationEvents = MutableSharedFlow<CreateEventNavigation>()
    val navigationEvents: SharedFlow<CreateEventNavigation> = _navigationEvents.asSharedFlow()

    // События для снекбара (ошибки)
    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    fun updateTitle(title: String) {
        _formData.value = _formData.value.copy(title = title)
        _titleError.value = null
    }

    fun updateDescription(desc: String) { _formData.value = _formData.value.copy(description = desc) }
    fun updateLocation(loc: String) { _formData.value = _formData.value.copy(location = loc) }
    fun updateStartDate(date: LocalDate?) { _formData.value = _formData.value.copy(startDate = date) }
    fun updateStartTime(time: LocalTime?) { _formData.value = _formData.value.copy(startTime = time) }
    fun updateEndDate(date: LocalDate?) { _formData.value = _formData.value.copy(endDate = date) }
    fun updateEndTime(time: LocalTime?) { _formData.value = _formData.value.copy(endTime = time) }
    fun updateColor(color: String) { _formData.value = _formData.value.copy(color = color) }

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
            _requestState.value = RequestState.Loading
            when (val result = eventRepository.createEvent(request)) {
                is DataResult.Success -> {
                    _requestState.value = RequestState.Idle
                    _navigationEvents.emit(CreateEventNavigation.GoToEventAvatarUpload(result.data.eventId))
                }
                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                }
            }
        }
    }
}

sealed class CreateEventNavigation {
    data class GoToEventAvatarUpload(val eventId: String) : CreateEventNavigation()
}