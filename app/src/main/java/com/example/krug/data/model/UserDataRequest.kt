package com.example.krug.data.model

import com.google.gson.annotations.SerializedName

data class UserDataRequest(
    val email: String,


    val display_name: String,

    val birthday: String?, // формат "YYYY-MM-DD", может быть null
    val username: String
)