package com.example.krug.data.model.auth

import com.google.gson.annotations.SerializedName

// Запросы
data class EmailRequest(val email: String)
data class VerifyCodeRequest(val email: String, val code: String)
data class CheckUsernameRequest(val username: String)

data class RegisterUserData(
    val email: String,
    val username: String,
    val birthday: String?,
    @SerializedName("display_name") val displayName: String?,
    val description: String? = null
)

data class RegisterRequest(val user: RegisterUserData)

// Ответы
data class RegisterResponse(
    val token: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    val error: String? = null
)

data class VerifyCodeResponse(
    val token: String? = null,
    @SerializedName("is_new_user") val isNewUser: Boolean = false,
    @SerializedName("user_id") val userId: String? = null,
    val error: String? = null
)