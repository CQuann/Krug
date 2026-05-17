package com.example.krug.ui.screens.event.planning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    fun getEventId(): String = eventId

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
}