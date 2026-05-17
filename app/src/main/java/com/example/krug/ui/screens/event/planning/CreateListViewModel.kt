package com.example.krug.ui.screens.event.planning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.planning.CreateItemListRequest
import com.example.krug.data.model.planning.CreateTaskListRequest
import com.example.krug.data.repository.PlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateListViewModel @Inject constructor(
    private val planningRepository: PlanningRepository
) : ViewModel() {

    // Флаг, устанавливаемый из UI
    private var isTask: Boolean = false

    fun init(isTaskList: Boolean) {
        isTask = isTaskList
    }

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _items = MutableStateFlow(listOf(""))
    val items: StateFlow<List<String>> = _items.asStateFlow()

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    private val _itemErrors = MutableStateFlow<Map<Int, String>>(emptyMap())
    val itemErrors: StateFlow<Map<Int, String>> = _itemErrors.asStateFlow()

    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    private val _createdEvent = MutableSharedFlow<Unit>()
    val createdEvent: SharedFlow<Unit> = _createdEvent.asSharedFlow()

    fun updateTitle(value: String) {
        _title.value = value
        _titleError.value = null
    }

    fun updateItem(index: Int, value: String) {
        val list = _items.value.toMutableList()
        if (index in list.indices) {
            list[index] = value
            _items.value = list
            val errors = _itemErrors.value.toMutableMap()
            errors.remove(index)
            _itemErrors.value = errors
        }
    }

    fun addItem() {
        _items.value += ""
    }

    fun removeItem(index: Int) {
        val list = _items.value.toMutableList()
        if (list.size > 1 && index in list.indices) {
            list.removeAt(index)
            _items.value = list
            val errors = _itemErrors.value.toMutableMap()
            errors.remove(index)
            val updated = mutableMapOf<Int, String>()
            errors.forEach { (i, msg) ->
                updated[if (i > index) i - 1 else i] = msg
            }
            _itemErrors.value = updated
        }
    }

    fun create(eventId: String) {
        if (!validate()) return

        val title = _title.value.trim()
        val items = _items.value.map { it.trim() }

        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            val result = if (isTask) {
                planningRepository.createTaskList(eventId, CreateTaskListRequest(title, items))
            } else {
                planningRepository.createItemList(eventId, CreateItemListRequest(title, items))
            }
            when (result) {
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
            _titleError.value = "Введите название"
            valid = false
        }
        val errors = mutableMapOf<Int, String>()
        _items.value.forEachIndexed { index, item ->
            if (item.isBlank()) {
                errors[index] = "Заполните пункт"
                valid = false
            }
        }
        _itemErrors.value = errors
        return valid
    }
}