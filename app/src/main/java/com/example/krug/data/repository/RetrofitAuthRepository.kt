package com.example.krug.data.repository

import android.util.Log
import com.example.krug.data.model.*
import com.example.krug.data.model.auth.AuthResult
import com.example.krug.data.model.auth.CheckUsernameRequest
import com.example.krug.data.model.auth.EmailRequest
import com.example.krug.data.model.auth.LogoutRequest
import com.example.krug.data.model.auth.RegisterRequest
import com.example.krug.data.model.auth.VerifyCodeRequest
import com.example.krug.data.model.auth.VerifyResult
import com.example.krug.data.network.AuthApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitAuthRepository @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun requestCode(email: String): AuthResult<Unit> {
        return try {
            val response = authApi.requestCode(EmailRequest(email))
            if (response.success) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(response.error ?: "Неизвестная ошибка")
            }
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun verifyCode(email: String, code: String): AuthResult<VerifyResult> {
        return try {
            val response = authApi.verifyCode(VerifyCodeRequest(email, code))
            if (response.error != null) {
                return AuthResult.Error(response.error)
            }
            when {
                response.token != null && !response.isNewUser -> {
                    AuthResult.Success(VerifyResult.LoginSuccess(response.token))
                }
                response.isNewUser -> {
                    AuthResult.Success(VerifyResult.RegisterNeeded)
                }
                else -> AuthResult.Error("Неизвестный ответ сервера")
            }
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun register(userData: UserData): AuthResult<String> {
        return try {
            val request = RegisterRequest(userData)
            val response = authApi.register(request)
            if (response.token != null) {
                AuthResult.Success(response.token)
            } else {
                AuthResult.Error(response.error ?: "Ошибка регистрации")
            }
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun checkUsername(username: String): AuthResult<Boolean> {
        return try {
            val response = authApi.checkUsername(CheckUsernameRequest(username))
            AuthResult.Success(response.available)
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun validateToken(token: String): AuthResult<Boolean> {
        Log.d("AuthRepo", "validateToken called with token: $token")
        return try {
            val response = authApi.validateToken("Bearer $token")
            Log.d("AuthRepo", "Response success: ${response.success}")
            AuthResult.Success(response.success)
        } catch (e: Exception) {
            Log.e("AuthRepo", "Error: ${e.message}", e)
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun logout(token: String): AuthResult<Unit> {
        return try {
            val response = authApi.logout(LogoutRequest(token))
            if (response.success) AuthResult.Success(Unit)
            else AuthResult.Error(response.error ?: "Ошибка выхода")
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun getUserData(token: String): AuthResult<UserData> {
        return try {
            val response = authApi.getUserData("Bearer $token")
            AuthResult.Success(response.user)
        } catch (e: Exception) {
            AuthResult.Error("Ошибка получения данных: ${e.message}")
        }
    }

    override suspend fun editUserData(token: String, userData: UserData): AuthResult<Unit> {
        return try {
            val response = authApi.editUserData("Bearer $token", UserEditRequest(userData))
            if (response.success) AuthResult.Success(Unit)
            else AuthResult.Error(response.error ?: "Ошибка редактирования")
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }
}