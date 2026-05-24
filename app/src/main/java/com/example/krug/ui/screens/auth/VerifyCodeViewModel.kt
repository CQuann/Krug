package com.example.krug.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.auth.VerifyResult
import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Состояние экрана (загрузка, ошибка)
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    // Навигационные события
    private val _navigationEvents = MutableSharedFlow<VerifyNavigation>()
    val navigationEvents: SharedFlow<VerifyNavigation> = _navigationEvents.asSharedFlow()

    // Снекбар
    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    // Таймер обратного отсчёта (30 секунд)
    private val _canResend = MutableStateFlow(false)
    val canResend: StateFlow<Boolean> = _canResend.asStateFlow()

    private val _resendCooldown = MutableStateFlow(30)
    val resendCooldown: StateFlow<Int> = _resendCooldown.asStateFlow()

    private var timerJob: Job? = null

    init {
        startCooldown()
    }

    private fun startCooldown() {
        _canResend.value = false
        _resendCooldown.value = 30
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in 30 downTo 0) {
                _resendCooldown.value = i
                delay(1000L)
            }
            _canResend.value = true
        }
    }

    fun resendCode(email: String) {
        if (!_canResend.value) return
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            // Используем обычный requestCode вместо resend_code
            when (val result = authRepository.requestCode(email)) {
                is DataResult.Success -> {
                    _requestState.value = RequestState.Idle
                    _snackbarEvents.emit("Код отправлен повторно")
                    startCooldown()   // перезапускаем таймер
                }
                is DataResult.Error -> {
                    _requestState.value = RequestState.Error(result.message)
                }
            }
        }
    }

    fun verifyCode(email: String, code: String) {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            when (val result = authRepository.verifyCode(email, code)) {
                is DataResult.Success -> {
                    _requestState.value = RequestState.Idle
                    when (val verifyResult = result.data) {
                        is VerifyResult.LoginSuccess -> {
                            sessionManager.saveToken(verifyResult.token)
                            sessionManager.saveUserId(verifyResult.userId)
                            _navigationEvents.emit(VerifyNavigation.GoToMain)
                        }
                        is VerifyResult.RegisterNeeded -> {
                            _navigationEvents.emit(VerifyNavigation.GoToRegister(email))
                        }
                    }
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

sealed class VerifyNavigation {
    object GoToMain : VerifyNavigation()
    data class GoToRegister(val email: String) : VerifyNavigation()
}