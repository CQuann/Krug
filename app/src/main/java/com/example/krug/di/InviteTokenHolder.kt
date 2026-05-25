package com.example.krug.di

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InviteTokenHolder @Inject constructor() {
    private var pendingToken: String? = null

    fun setToken(token: String) { pendingToken = token }
    fun getAndClearToken(): String? = pendingToken.also { pendingToken = null }
    fun hasToken(): Boolean = pendingToken != null
}