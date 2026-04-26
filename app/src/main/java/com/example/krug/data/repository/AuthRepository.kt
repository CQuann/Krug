package com.example.krug.data.repository

import android.net.Uri
import com.example.krug.data.model.auth.AuthResult
import com.example.krug.data.model.UserData
import com.example.krug.data.model.auth.VerifyResult

interface AuthRepository {
    suspend fun requestCode(email: String): AuthResult<Unit>
    suspend fun verifyCode(email: String, code: String): AuthResult<VerifyResult>
    suspend fun register(userData: UserData): AuthResult<Pair<String, String>>
    suspend fun checkUsername(username: String): AuthResult<Boolean>
    suspend fun validateToken(token: String): AuthResult<Boolean>
    suspend fun logout(token: String): AuthResult<Unit>
    suspend fun getUserData(token: String): AuthResult<UserData>
    suspend fun editUserData(token: String, userData: UserData): AuthResult<Unit>

    suspend fun uploadAvatar(uri: Uri): AuthResult<String>
}