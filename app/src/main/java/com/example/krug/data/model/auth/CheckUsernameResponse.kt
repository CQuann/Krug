package com.example.krug.data.model.auth

import com.google.gson.annotations.SerializedName

data class CheckUsernameResponse(
    @SerializedName("success")
    val available: Boolean
)
