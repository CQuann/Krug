package com.example.krug.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.UserData
import com.example.krug.data.model.event.Event
import com.example.krug.data.repository.AuthRepository
import com.example.krug.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainAppViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Данные пользователя
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    // События
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _currentStatus = MutableStateFlow("active")
    val currentStatus: StateFlow<String> = _currentStatus.asStateFlow()

    private val _totalEvents = MutableStateFlow(0)
    val totalEvents: StateFlow<Int> = _totalEvents.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var eventsOffset = 0
    private val pageSize = 20

    init {
        viewModelScope.launch {
            _userId.value = sessionManager.getUserId()
            loadUserData()
            loadEvents(reset = true)
        }
    }

    fun onStatusChange(status: String) {
        if (_currentStatus.value != status) {
            _currentStatus.value = status
            eventsOffset = 0
            _events.value = emptyList()
            loadEvents(reset = true)
        }
    }

    fun loadEvents(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                eventsOffset = 0
                _events.value = emptyList()
                _isRefreshing.value = true
            } else {
                _isLoadingMore.value = true
            }

            when (val result = eventRepository.getEvents(_currentStatus.value, pageSize, eventsOffset)) {
                is DataResult.Success -> {
                    val response = result.data
                    _events.value = if (reset) response.items else _events.value + response.items
                    _totalEvents.value = response.total
                    eventsOffset += response.items.size
                    _error.value = null
                }
                is DataResult.Error -> {
                    _error.value = result.message
                }
            }
            _isRefreshing.value = false
            _isLoadingMore.value = false
        }
    }

    fun loadMoreEvents() {
        if (_isLoadingMore.value || _isRefreshing.value) return
        if (eventsOffset >= _totalEvents.value) return // всё загружено
        loadEvents(reset = false)
    }

    fun loadUserData() {
        viewModelScope.launch {
            when (val result = authRepository.getUserData()) {
                is DataResult.Success -> _userData.value = result.data
                is DataResult.Error -> _error.value = result.message
            }
        }
    }
}