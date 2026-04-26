package com.example.krug.data.model.auth

data class VerifyCodeResponse(
    val token: String? = null,
    val isNewUser: Boolean = false,
    val userId: String? = null,
    val error: String? = null
)