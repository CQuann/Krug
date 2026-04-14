//package com.example.krug.ui.screens.auth
//
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.krug.data.repository.AuthRepository
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class LoginEmailViewModel(
//    private val authRepository: AuthRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow<LoginEmailUiState>(LoginEmailUiState.Idle)
//    val uiState = _uiState.asStateFlow()
//
//    private val _navigationEvent = MutableSharedFlow<String>() // будем передавать email
//    val navigationEvent = _navigationEvent.asSharedFlow()
//
//    fun sendCode(email: String) {
//        viewModelScope.launch {
//            _uiState.value = LoginEmailUiState.Loading
//            val result = authRepository.requestCode(email)
//            when (result) {
//                is AuthResult.Success -> {
//                    _uiState.value = LoginEmailUiState.Idle
//                    // Переход на экран ввода кода, передаём email
//                    _navigationEvent.emit(email)
//                }
//                is AuthResult.Error -> {
//                    _uiState.value = LoginEmailUiState.Error(result.message)
//                }
//            }
//        }
//    }
//
//    fun resetError() {
//        if (_uiState.value is LoginEmailUiState.Error) {
//            _uiState.value = LoginEmailUiState.Idle
//        }
//    }
//}
//
//sealed class LoginEmailUiState {
//    object Idle : LoginEmailUiState()
//    object Loading : LoginEmailUiState()
//    data class Error(val message: String) : LoginEmailUiState()
//}