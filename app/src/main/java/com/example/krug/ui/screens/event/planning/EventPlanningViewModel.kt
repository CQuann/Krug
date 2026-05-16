package com.example.krug.ui.screens.event.planning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.planning.CreatePollRequest
import com.example.krug.data.repository.PlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EventPlanningViewModel @Inject constructor(
    private val planningRepository: PlanningRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    // ------------------ UI состояние экрана ------------------
    sealed class PlanningUiState {
        object Idle : PlanningUiState() // пока нет модулей, просто заглушка
        object SelectType : PlanningUiState() // открыт диалог выбора типа модуля
        object CreatePoll : PlanningUiState() // открыта форма создания опроса
    }

    private val _uiState = MutableStateFlow<PlanningUiState>(PlanningUiState.Idle)
    val uiState: StateFlow<PlanningUiState> = _uiState.asStateFlow()

    // Поля формы опроса
    private val _pollQuestion = MutableStateFlow("")
    val pollQuestion: StateFlow<String> = _pollQuestion.asStateFlow()

    private val _pollOptions = MutableStateFlow(listOf("", "")) // минимум 2
    val pollOptions: StateFlow<List<String>> = _pollOptions.asStateFlow()

    private val _multipleChoice = MutableStateFlow(false)
    val multipleChoice: StateFlow<Boolean> = _multipleChoice.asStateFlow()

    // ------------------ Ошибки валидации ------------------
    private val _questionError = MutableStateFlow<String?>(null)
    val questionError: StateFlow<String?> = _questionError.asStateFlow()

    private val _optionErrors = MutableStateFlow<Map<Int, String>>(emptyMap())
    val optionErrors: StateFlow<Map<Int, String>> = _optionErrors.asStateFlow()

    // Состояние сетевого запроса
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()
    private val _events = MutableSharedFlow<PlanningEvent>()
    val events: SharedFlow<PlanningEvent> = _events.asSharedFlow()

    // Действия пользователя

    fun onFabClick() {
        _uiState.value = PlanningUiState.SelectType
    }

    fun onSelectPollType() {
        _uiState.value = PlanningUiState.CreatePoll
        // каждый раз при открытии формы начинаем с чистого состояния
        clearForm()
    }

    fun onCancelCreate() {
        _uiState.value = PlanningUiState.Idle
        clearForm()
    }

    // Редактирование вопроса
    fun updateQuestion(q: String) {
        _pollQuestion.value = q
        _questionError.value = null
    }

    // Изменение текста варианта
    fun updateOption(index: Int, text: String) {
        val list = _pollOptions.value.toMutableList()
        if (index in list.indices) {
            list[index] = text
            _pollOptions.value = list
            // снять ошибку для этого поля
            val errors = _optionErrors.value.toMutableMap()
            errors.remove(index)
            _optionErrors.value = errors
        }
    }

    fun addOption() {
        _pollOptions.value += ""
    }

    fun removeOption(index: Int) {
        val list = _pollOptions.value.toMutableList()
        if (list.size > 2 && index in list.indices) {
            list.removeAt(index)
            _pollOptions.value = list
            // перестроить ошибки
            val errors = _optionErrors.value.toMutableMap()
            errors.remove(index)
            // для индексов > index уменьшаем ключ
            val updated = mutableMapOf<Int, String>()
            errors.forEach { (i, msg) ->
                updated[if (i > index) i - 1 else i] = msg
            }
            _optionErrors.value = updated
        }
    }

    fun toggleMultipleChoice(value: Boolean) {
        _multipleChoice.value = value
    }

    fun createPoll() {
        if (!validateFields()) return

        val question = _pollQuestion.value.trim()
        val options = _pollOptions.value.map { it.trim() }

        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            val request = CreatePollRequest(
                event_id = eventId,
                question = question,
                options = options,
                multiple_choice = _multipleChoice.value
            )
            when (val result = planningRepository.createPoll(request)) {
                is DataResult.Success -> {
                    _requestState.value = RequestState.Success
                    _events.emit(PlanningEvent.PollCreated)
                    _uiState.value = PlanningUiState.Idle
                    clearForm()
                }
                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                }
            }
        }
    }

    fun clearRequestState() {
        _requestState.value = RequestState.Idle
    }

    // ---------------- Вспомогательные методы ----------------

    private fun validateFields(): Boolean {
        var valid = true
        if (_pollQuestion.value.isBlank()) {
            _questionError.value = "Введите вопрос"
            valid = false
        }
        val errors = mutableMapOf<Int, String>()
        _pollOptions.value.forEachIndexed { index, option ->
            if (option.isBlank()) {
                errors[index] = "Заполните вариант"
                valid = false
            }
        }
        _optionErrors.value = errors
        return valid
    }

    private fun clearForm() {
        _pollQuestion.value = ""
        _pollOptions.value = listOf("", "")
        _multipleChoice.value = false
        _questionError.value = null
        _optionErrors.value = emptyMap()
        _requestState.value = RequestState.Idle
    }
}

sealed class PlanningEvent {
    object PollCreated : PlanningEvent()
}