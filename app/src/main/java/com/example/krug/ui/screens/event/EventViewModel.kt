package com.example.krug.ui.screens.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.event.Event
import com.example.krug.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    private val _uiState = MutableStateFlow<EventUiState>(EventUiState.Loading)
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    init { loadEvent() }

    fun loadEvent() {
        viewModelScope.launch {
            _uiState.value = EventUiState.Loading
            when (val result = eventRepository.getEvent(eventId)) {
                is DataResult.Success -> {
                    _event.value = result.data
                    _uiState.value = EventUiState.Success
                }
                is DataResult.Error -> {
                    _uiState.value = EventUiState.Error(result.message)
                }
            }
        }
    }
}

sealed class EventUiState {
    object Loading : EventUiState()
    object Success : EventUiState()
    data class Error(val message: String) : EventUiState()
}