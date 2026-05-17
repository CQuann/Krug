package com.example.krug.ui.screens.event.planning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.planning.PlanningModule
import com.example.krug.data.repository.PlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventPlanningViewModel @Inject constructor(
    private val planningRepository: PlanningRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    fun getEventId(): String = eventId

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    sealed class PlanningUiState {
        object Loading : PlanningUiState()
        data class Content(val modules: List<PlanningModule>) : PlanningUiState()
        data class Error(val message: String) : PlanningUiState()
    }
    private val _uiState = MutableStateFlow<PlanningUiState>(PlanningUiState.Loading)
    val uiState: StateFlow<PlanningUiState> = _uiState.asStateFlow()

    sealed class CreationMode {
        object None : CreationMode()
        object Poll : CreationMode()
        object ItemList : CreationMode()
        object TaskList : CreationMode()
    }
    private val _creationMode = MutableStateFlow<CreationMode>(CreationMode.None)
    val creationMode: StateFlow<CreationMode> = _creationMode.asStateFlow()

    // Показ диалога выбора типа
    private val _showTypeDialog = MutableStateFlow(false)
    val showTypeDialog: StateFlow<Boolean> = _showTypeDialog.asStateFlow()

    init { loadModules() }

    fun loadModules() {
        viewModelScope.launch {
            _uiState.value = PlanningUiState.Loading
            when (val result = planningRepository.getPlanningModules(eventId)) {
                is DataResult.Success -> _uiState.value = PlanningUiState.Content(result.data.modules)
                is DataResult.Error -> _uiState.value = PlanningUiState.Error(result.message)
            }
        }
    }

    fun onFabClick() { _showTypeDialog.value = true }
    fun dismissTypeDialog() { _showTypeDialog.value = false }

    fun startCreatingPoll() {
        dismissTypeDialog()
        _creationMode.value = CreationMode.Poll
    }
    fun startCreatingItemList() {
        dismissTypeDialog()
        _creationMode.value = CreationMode.ItemList
    }
    fun startCreatingTaskList() {
        dismissTypeDialog()
        _creationMode.value = CreationMode.TaskList
    }

    fun onCreationFinished() {
        _creationMode.value = CreationMode.None
        loadModules() // обновить список после создания
    }

    fun votePoll(pollId: String, optionIndexes: List<Int>) {
        viewModelScope.launch {
            when (val result = planningRepository.votePoll(eventId, pollId, optionIndexes)) {
                is DataResult.Success -> loadModules()
                is DataResult.Error -> _errorMessage.emit(result.message)
            }
        }
    }

    fun assignItem(type: String, moduleId: String, itemId: String, assign: Boolean) {
        viewModelScope.launch {
            when (val result = planningRepository.assignItem(eventId, type, moduleId, itemId, assign)) {
                is DataResult.Success -> loadModules()
                is DataResult.Error -> _errorMessage.emit(result.message)
            }
        }
    }

    fun completeTask(moduleId: String, itemId: String, completed: Boolean) {
        viewModelScope.launch {
            when (val result = planningRepository.completeTask(eventId, moduleId, itemId, completed)) {
                is DataResult.Success -> loadModules()
                is DataResult.Error -> _errorMessage.emit(result.message)
            }
        }
    }
    fun getCurrentUserId(): String? = sessionManager.cachedUserId
}