// ui/screens/event/planning/CreatePollViewModel.kt
package com.example.krug.ui.screens.event.planning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.planning.CreatePollRequest
import com.example.krug.data.repository.PlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePollViewModel @Inject constructor(
    private val planningRepository: PlanningRepository
) : ViewModel() {

    // Поля формы
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _options = MutableStateFlow(listOf("", ""))
    val options: StateFlow<List<String>> = _options.asStateFlow()

    private val _multipleChoice = MutableStateFlow(false)
    val multipleChoice: StateFlow<Boolean> = _multipleChoice.asStateFlow()

    // Ошибки валидации
    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    private val _optionErrors = MutableStateFlow<Map<Int, String>>(emptyMap())
    val optionErrors: StateFlow<Map<Int, String>> = _optionErrors.asStateFlow()

    // Состояние запроса
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    // Событие успешного создания
    private val _createdEvent = MutableSharedFlow<Unit>()
    val createdEvent: SharedFlow<Unit> = _createdEvent.asSharedFlow()

    fun updateTitle(value: String) {
        _title.value = value
        _titleError.value = null
    }

    fun updateOption(index: Int, value: String) {
        val list = _options.value.toMutableList()
        if (index in list.indices) {
            list[index] = value
            _options.value = list
            val errors = _optionErrors.value.toMutableMap()
            errors.remove(index)
            _optionErrors.value = errors
        }
    }

    fun addOption() {
        _options.value += ""
    }

    fun removeOption(index: Int) {
        val list = _options.value.toMutableList()
        if (list.size > 2 && index in list.indices) {
            list.removeAt(index)
            _options.value = list
            val errors = _optionErrors.value.toMutableMap()
            errors.remove(index)
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

    fun createPoll(eventId: String) {
        if (!validate()) return

        val request = CreatePollRequest(
            title = _title.value.trim(),
            options = _options.value.map { it.trim() },
            multiple_choice = _multipleChoice.value
        )

        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = planningRepository.createPoll(eventId, request)) {
                is DataResult.Success -> {
                    _requestState.value = RequestState.Success
                    _createdEvent.emit(Unit)
                }
                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                }
            }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (_title.value.isBlank()) {
            _titleError.value = "Введите вопрос"
            valid = false
        }
        val errors = mutableMapOf<Int, String>()
        _options.value.forEachIndexed { index, option ->
            if (option.isBlank()) {
                errors[index] = "Заполните вариант"
                valid = false
            }
        }
        _optionErrors.value = errors
        return valid
    }
}