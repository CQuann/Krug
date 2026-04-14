package com.example.krug.data.repository

import com.example.krug.data.model.AuthResult
import com.example.krug.data.model.UserData
import com.example.krug.data.model.VerifyResult

interface AuthRepository {
    suspend fun requestCode(email: String): AuthResult<Unit>
    suspend fun verifyCode(email: String, code: String): AuthResult<VerifyResult>
    suspend fun register(userData: UserData, tempToken: String): AuthResult<String>
    suspend fun checkUsername(username: String): AuthResult<Boolean>
    suspend fun validateToken(token: String): AuthResult<Boolean>
    suspend fun logout(token: String): AuthResult<Unit>
}