package com.example.krug.data.model.auth

data class LogoutResponse(
    val success: Boolean,
    val error: String? = null
)