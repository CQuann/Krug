package com.example.krug.data.model

data class UserEditRequest(
    val avatar_url: String,
    val birthday: String?,
    val description: String,
    val display_name: String,
    val email: String,
    val username: String
)

