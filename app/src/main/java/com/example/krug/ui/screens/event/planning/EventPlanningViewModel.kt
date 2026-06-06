package com.example.krug.ui.screens.event.planning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.planning.PlanningModule
import com.example.krug.data.repository.PlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventPlanningViewModel @Inject constructor(
    private val planningRepository: PlanningRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    sealed class PlanningUiState {
        object Loading : PlanningUiState()
        data class Content(val modules: List<PlanningModule>) : PlanningUiState()
        data class Error(val message: String) : PlanningUiState()
    }

    private val _uiState = MutableStateFlow<PlanningUiState>(PlanningUiState.Loading)
    val uiState: StateFlow<PlanningUiState> = _uiState.asStateFlow()

    private val _showTypeDialog = MutableStateFlow(false)
    val showTypeDialog: StateFlow<Boolean> = _showTypeDialog.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun loadModules(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) _uiState.value = PlanningUiState.Loading
            when (val result = planningRepository.getPlanningModules(eventId)) {
                is DataResult.Success -> _uiState.value = PlanningUiState.Content(result.data.modules)
                is DataResult.Error -> {
                    _uiState.value = PlanningUiState.Error(result.message)
                    _snackbarEvents.emit(result.message)
                }
            }
        }
    }

    fun refreshModules() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadModules(isRefresh = true)
            _isRefreshing.value = false
        }
    }

    fun onFabClick() { _showTypeDialog.value = true }
    fun dismissTypeDialog() { _showTypeDialog.value = false }

    fun votePoll(pollId: String, optionIndexes: List<Int>) {
        viewModelScope.launch {
            when (val result = planningRepository.votePoll(eventId, pollId, optionIndexes)) {
                is DataResult.Success -> loadModules()
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }

    fun assignItem(type: String, moduleId: String, itemId: String, assign: Boolean) {
        viewModelScope.launch {
            when (val result = planningRepository.assignItem(eventId, type, moduleId, itemId, assign)) {
                is DataResult.Success -> loadModules()
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }

    fun completeTask(moduleId: String, itemId: String, completed: Boolean) {
        viewModelScope.launch {
            when (val result = planningRepository.completeTask(eventId, moduleId, itemId, completed)) {
                is DataResult.Success -> loadModules()
                is DataResult.Error -> _snackbarEvents.emit(result.message)
            }
        }
    }

    fun getCurrentUserId(): String? = sessionManager.cachedUserId
}