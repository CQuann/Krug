package com.example.krug.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.event.Event
import com.example.krug.data.repository.EventRepository
import com.example.krug.di.InviteTokenHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainAppViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val sessionManager: SessionManager,
    private val inviteTokenHolder: InviteTokenHolder
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

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

    private val _showJoinDialog = MutableStateFlow(false)
    val showJoinDialog: StateFlow<Boolean> = _showJoinDialog.asStateFlow()

    private val _pendingJoinEvent = MutableStateFlow<Event?>(null)
    val pendingJoinEvent: StateFlow<Event?> = _pendingJoinEvent.asStateFlow()
    private val _isJoining = MutableStateFlow(false)

    private var lastEvents: List<Event>? = null


    init {
        viewModelScope.launch {
            _userId.value = sessionManager.getUserId()
            loadEvents(reset = true)
            processPendingInvite()
            launch {
                inviteTokenHolder.tokenFlow.collect { token ->
                    if (_userId.value != null) {
                        joinEvent(token)
                    }
                }
            }
        }
    }

    fun processPendingInvite() {
        if (_userId.value != null) {
            val token = inviteTokenHolder.getAndClearToken()
            Log.d("InviteDebug", "processPendingInvite, token: $token")
            if (token != null) {
                joinEvent(token)
            }
        }
    }

    private fun joinEvent(inviteToken: String) {
        if (_isJoining.value) return
        _isJoining.value = true
        viewModelScope.launch {
            _isRefreshing.value = true
            when (val result = eventRepository.joinEvent(inviteToken)) {
                is DataResult.Success -> {
                    Log.d("MainAppViewModel", "Join event success: ${result.data}")
                    _pendingJoinEvent.value = result.data
                    _showJoinDialog.value = true
                    loadEvents(reset = true)
                }
                is DataResult.Error -> {
                    _error.value = result.message
                }
            }
            _isRefreshing.value = false
            _isJoining.value = false
        }
    }

    fun dismissJoinDialog() {
        _showJoinDialog.value = false
        _pendingJoinEvent.value = null
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
                _events.value = lastEvents ?: emptyList()
                _isRefreshing.value = true
            } else {
                _isLoadingMore.value = true
            }

            when (val result = eventRepository.getEvents(_currentStatus.value, pageSize, eventsOffset)) {
                is DataResult.Success -> {
                    val response = result.data
                    lastEvents = if (reset) response.events else lastEvents.orEmpty() + response.events
                    _events.value = lastEvents!!
                    _totalEvents.value = response.total
                    eventsOffset = lastEvents!!.size
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
        if (eventsOffset >= _totalEvents.value) return
        loadEvents(reset = false)
    }

    fun onRefresh() {
        if (_isRefreshing.value) return
        loadEvents(reset = true)
    }
}