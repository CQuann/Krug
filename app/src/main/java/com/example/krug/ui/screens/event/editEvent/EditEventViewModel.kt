package com.example.krug.ui.screens.event.editEvent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.event.UpdateEventRequest
import com.example.krug.data.repository.EventRepository
import com.example.krug.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    private val _startDate = MutableStateFlow<LocalDate?>(null)
    val startDate: StateFlow<LocalDate?> = _startDate.asStateFlow()
    private val _startTime = MutableStateFlow<LocalTime?>(null)
    val startTime: StateFlow<LocalTime?> = _startTime.asStateFlow()
    private val _endDate = MutableStateFlow<LocalDate?>(null)
    val endDate: StateFlow<LocalDate?> = _endDate.asStateFlow()
    private val _endTime = MutableStateFlow<LocalTime?>(null)
    val endTime: StateFlow<LocalTime?> = _endTime.asStateFlow()
    private val _color = MutableStateFlow("#FF5733")
    val color: StateFlow<String> = _color.asStateFlow()

    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<EditEventNavigation>()
    val navigationEvents: SharedFlow<EditEventNavigation> = _navigationEvents.asSharedFlow()

    init { loadEvent() }

    private fun loadEvent() {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = eventRepository.getEvent(eventId)) {
                is DataResult.Success -> {
                    val event = result.data.event
                    _title.value = event.title
                    _location.value = event.location ?: ""
                    _description.value = event.description ?: ""
                    _startDate.value = DateUtils.parseDate(event.startDateTime)
                    _startTime.value = DateUtils.parseTime(event.startDateTime)
                    _endDate.value = DateUtils.parseDate(event.endDateTime)
                    _endTime.value = DateUtils.parseTime(event.endDateTime)
                    _color.value = event.color
                    _requestState.value = RequestState.Idle
                }
                is DataResult.Error -> _requestState.value = RequestState.Error(result.message)
            }
        }
    }

    fun updateTitle(title: String) { _title.value = title; _titleError.value = null }
    fun updateLocation(location: String) { _location.value = location }
    fun updateDescription(description: String) { _description.value = description }
    fun updateStartDate(date: LocalDate?) { _startDate.value = date }
    fun updateStartTime(time: LocalTime?) { _startTime.value = time }
    fun updateEndDate(date: LocalDate?) { _endDate.value = date }
    fun updateEndTime(time: LocalTime?) { _endTime.value = time }
    fun updateColor(color: String) { _color.value = color }

    fun updateEvent() {
        if (_title.value.isBlank()) {
            _titleError.value = "Введите название"
            return
        }
        val request = UpdateEventRequest(
            title = _title.value.trim(),
            description = _description.value.ifBlank { null },
            location = _location.value.ifBlank { null },
            startDateTime = DateUtils.toIsoString(_startDate.value, _startTime.value),
            endDateTime = DateUtils.toIsoString(_endDate.value, _endTime.value),
            color = _color.value
        )
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = eventRepository.updateEvent(eventId, request)) {
                is DataResult.Success -> {
                    _navigationEvents.emit(EditEventNavigation.GoBack)
                }
                is DataResult.Error -> _requestState.value = RequestState.Error(result.message)
            }
        }
    }
}

sealed class EditEventNavigation {
    object GoBack : EditEventNavigation()
}