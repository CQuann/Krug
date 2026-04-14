package com.example.krug.data.repository

import com.example.krug.data.model.*
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
                response.tempToken != null && response.isNewUser -> {
                    AuthResult.Success(VerifyResult.RegisterNeeded(response.tempToken))
                }
                response.token != null && !response.isNewUser -> {
                    AuthResult.Success(VerifyResult.LoginSuccess(response.token))
                }
                else -> AuthResult.Error("Неизвестный ответ сервера")
            }
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun register(userData: UserData, tempToken: String): AuthResult<String> {
        return try {
            val response = authApi.register(RegisterRequest(userData, tempToken))
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
        return try {
            val response = authApi.validateToken(TokenValidateRequest(token))
            AuthResult.Success(response.success)
        } catch (e: Exception) {
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
}