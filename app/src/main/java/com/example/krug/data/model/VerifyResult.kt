package com.example.krug.data.model

sealed class VerifyResult {
    data class LoginSuccess(val token: String) : VerifyResult()
    data class RegisterNeeded(val tempToken: String) : VerifyResult()
}