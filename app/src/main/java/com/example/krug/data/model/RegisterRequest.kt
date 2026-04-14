package com.example.krug.data.model

data class RegisterRequest(
    val user: UserData,
    val tempToken: String
)