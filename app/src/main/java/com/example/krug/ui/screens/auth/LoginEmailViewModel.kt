package com.example.krug.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
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

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // Единое состояние экрана (загрузка, ошибка, idle)
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    // Навигационное событие (передаём email дальше)
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
        _emailError.value = when {
            newEmail.isBlank() -> "Введите email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() -> "Введите корректный email"
            else -> null
        }
    }

    fun sendCode() {
        val currentEmail = _email.value
        if (currentEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
            _emailError.value = "Введите корректный email"
            return
        }
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = authRepository.requestCode(currentEmail)) {
                is DataResult.Success -> {
                    _requestState.value = RequestState.Idle
                    _navigationEvent.emit(currentEmail)
                }
                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                }
            }
        }
    }

    fun resetError() {
        if (_requestState.value is RequestState.Error) {
            _requestState.value = RequestState.Idle
        }
    }
}