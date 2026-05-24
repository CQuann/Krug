package com.example.krug.ui.screens.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.event.Event
import com.example.krug.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    // Данные события (только базовые, без деталей)
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    // Состояние экрана (загрузка, ошибка)
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Loading)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    // Сообщения для снекбара
    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    init { loadEvent() }

    fun loadEvent() {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = eventRepository.getEvent(eventId)) {
                is DataResult.Success -> {
                    _event.value = result.data.event
                    _requestState.value = RequestState.Idle
                }
                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                    _snackbarEvents.emit(result.message)
                }
            }
        }
    }
}