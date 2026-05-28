package com.example.krug.data.model.auth

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("user_id") val userId: String? = null,
    val username: String,
    val email: String,
    @SerializedName("display_name") val displayName: String?,
    val birthday: String?,
    val description: String? = null
)

data class UserDataResponse(
    val user: UserData
)

data class UserEditRequest(
    val username: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    val birthday: String? = null,
    val description: String? = null
)