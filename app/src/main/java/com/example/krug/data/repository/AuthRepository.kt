package com.example.krug.data.repository

import android.net.Uri
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.UserData
import com.example.krug.data.model.auth.VerifyResult

interface AuthRepository {
    suspend fun requestCode(email: String): DataResult<Unit>
    suspend fun verifyCode(email: String, code: String): DataResult<VerifyResult>
    suspend fun register(userData: UserData): DataResult<Pair<String, String>>
    suspend fun checkUsername(username: String): DataResult<Boolean>
    suspend fun validateToken(): DataResult<Boolean>
    suspend fun logout(token: String): DataResult<Unit>
    suspend fun getUserData(): DataResult<UserData>
    suspend fun editUserData(userData: UserData): DataResult<Unit>
    suspend fun uploadAvatar(uri: Uri): DataResult<String>
}