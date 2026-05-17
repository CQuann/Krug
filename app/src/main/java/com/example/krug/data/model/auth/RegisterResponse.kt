package com.example.krug.data.model.auth

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    val token: String? = null,

    @SerializedName("user_id")
    val userId: String? = null,

    val error: String? = null
)