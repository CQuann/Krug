package com.example.krug.data.model

data class UserData(
    val email: String,
    val display_name: String,
    val birthday: String?, // формат "YYYY-MM-DD", может быть null
    val username: String
)