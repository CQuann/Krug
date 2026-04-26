package com.example.krug.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.krug.data.local.TokenManager
import com.example.krug.data.model.*
import com.example.krug.data.model.auth.AuthResult
import com.example.krug.data.model.auth.CheckUsernameRequest
import com.example.krug.data.model.auth.EmailRequest
import com.example.krug.data.model.auth.LogoutRequest
import com.example.krug.data.model.auth.RegisterRequest
import com.example.krug.data.model.auth.VerifyCodeRequest
import com.example.krug.data.model.auth.VerifyResult
import com.example.krug.data.network.AuthApi
import com.example.krug.utils.ImageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitAuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
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
            if (response.error != null) return AuthResult.Error(response.error)
            when {
                response.token != null && !response.isNewUser -> {
                    AuthResult.Success(VerifyResult.LoginSuccess(response.token, response.userId ?: ""))
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

    override suspend fun register(userData: UserData): AuthResult<Pair<String, String>> {
        return try {
            val request = RegisterRequest(userData)
            val response = authApi.register(request)
            if (response.token != null && response.userId != null) {
                AuthResult.Success(Pair(response.token, response.userId))
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

    override suspend fun uploadAvatar(uri: Uri): AuthResult<String> {
        return try {
            val token = tokenManager.getToken() ?: return AuthResult.Error("Не авторизован")
            val file = ImageUtils.uriToFile(context, uri) ?: return AuthResult.Error("Не удалось получить файл")
            val compressedFile = ImageUtils.compressImage(file, 1024)
            val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", compressedFile.name, requestBody)
            val response = authApi.uploadAvatar("Bearer $token", part)
            if (response.success) {
                AuthResult.Success("")
            } else {
                val errorMsg = response.error ?: "Ошибка загрузки"
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            AuthResult.Error("Ошибка: ${e.message}")
        }
    }
}