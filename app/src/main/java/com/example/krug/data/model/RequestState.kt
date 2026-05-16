package com.example.krug.data.model

sealed class RequestState {
    object Idle : RequestState()
    object Loading : RequestState()
    object Success : RequestState()
    data class Error(val message: String) : RequestState()
}