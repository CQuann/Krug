package com.example.krug.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.AuthResult
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 1. Состояние UI (экран может быть в одном из этих состояний)
    private val _uiState = MutableStateFlow<LoginEmailUiState>(LoginEmailUiState.Idle)
    val uiState: StateFlow<LoginEmailUiState> = _uiState.asStateFlow()

    // 2. Одноразовые события навигации (переход на другой экран)
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // 3. Метод, вызываемый UI при нажатии кнопки "Далее"
    fun sendCode(email: String) {
        viewModelScope.launch {
            // Сначала показываем состояние загрузки
            _uiState.value = LoginEmailUiState.Loading

            // Вызываем репозиторий (suspend функция)
            val result = authRepository.requestCode(email)

            // Обрабатываем результат
            when (result) {
                is AuthResult.Success -> {
                    // Успех: сбрасываем состояние загрузки
                    _uiState.value = LoginEmailUiState.Idle
                    // Отправляем событие навигации, передавая email
                    _navigationEvent.emit(email)
                }
                is AuthResult.Error -> {
                    // Ошибка: показываем сообщение
                    _uiState.value = LoginEmailUiState.Error(result.message)
                }
            }
        }
    }

    // 4. Метод для сброса ошибки (например, когда пользователь начинает редактировать email)
    fun resetError() {
        if (_uiState.value is LoginEmailUiState.Error) {
            _uiState.value = LoginEmailUiState.Idle
        }
    }
}

// 5. Sealed class, описывающий все возможные состояния UI
sealed class LoginEmailUiState {
    object Idle : LoginEmailUiState()          // ничего не происходит
    object Loading : LoginEmailUiState()       // идёт запрос
    data class Error(val message: String) : LoginEmailUiState() // ошибка с текстом
}