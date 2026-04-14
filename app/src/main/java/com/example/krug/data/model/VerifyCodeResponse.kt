package com.example.krug.data.model

data class VerifyCodeResponse(
    val tempToken: String? = null,
    val token: String? = null,
    val isNewUser: Boolean = false,
    val error: String? = null
)