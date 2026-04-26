package com.example.krug.data.model.auth

data class RegisterResponse(
    val token: String? = null,
    val userId: String? = null,
    val error: String? = null
)