package com.example.krug.di

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InviteTokenHolder @Inject constructor() {
    private var pendingToken: String? = null

    private val _tokenFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val tokenFlow: SharedFlow<String> = _tokenFlow.asSharedFlow()

    fun setToken(token: String) {
        pendingToken = token
        _tokenFlow.tryEmit(token)
    }

    fun getAndClearToken(): String? = pendingToken.also { pendingToken = null }

    fun hasToken(): Boolean = pendingToken != null
}