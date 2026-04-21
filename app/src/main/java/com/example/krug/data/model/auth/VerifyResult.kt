package com.example.krug.data.model.auth

sealed class VerifyResult {
    data class LoginSuccess(val token: String) : VerifyResult()
    object RegisterNeeded : VerifyResult()
}