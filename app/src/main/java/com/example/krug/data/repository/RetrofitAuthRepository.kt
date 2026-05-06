package com.example.krug.data.repository

import android.content.Context
import android.net.Uri
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.UserData
import com.example.krug.data.model.UserEditRequest
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
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override suspend fun requestCode(email: String): DataResult<Unit> {
        return try {
            val response = authApi.requestCode(EmailRequest(email))
            if (response.success) {
                DataResult.Success(Unit)
            } else {
                DataResult.Error(response.error ?: "Неизвестная ошибка")
            }
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun verifyCode(email: String, code: String): DataResult<VerifyResult> {
        return try {
            val response = authApi.verifyCode(VerifyCodeRequest(email, code))
            if (response.error != null) return DataResult.Error(response.error)
            when {
                response.token != null && !response.isNewUser -> {
                    DataResult.Success(
                        VerifyResult.LoginSuccess(
                            response.token,
                            response.userId ?: ""
                        )
                    )
                }

                response.isNewUser -> {
                    DataResult.Success(VerifyResult.RegisterNeeded)
                }

                else -> DataResult.Error("Неизвестный ответ сервера")
            }
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun register(userData: UserData): DataResult<Pair<String, String>> {
        return try {
            val request = RegisterRequest(userData)
            val response = authApi.register(request)
            if (response.token != null && response.userId != null) {
                DataResult.Success(Pair(response.token, response.userId))
            } else {
                DataResult.Error(response.error ?: "Ошибка регистрации")
            }
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun checkUsername(username: String): DataResult<Boolean> {
        return try {
            val response = authApi.checkUsername(CheckUsernameRequest(username))
            DataResult.Success(response.available)
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun validateToken(): DataResult<Boolean> {
        return try {
            val response = authApi.validateToken()
            DataResult.Success(response.success)
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun logout(token: String): DataResult<Unit> {
        return try {
            val response = authApi.logout(LogoutRequest(token))
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Ошибка выхода")
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun getUserData(): DataResult<UserData> {
        return try {
            val response = authApi.getUserData()
            DataResult.Success(response.user)
        } catch (e: Exception) {
            DataResult.Error("Ошибка получения данных: ${e.message}")
        }
    }

    override suspend fun editUserData(userData: UserData): DataResult<Unit> {
        return try {
            val response = authApi.editUserData(UserEditRequest(userData))
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Ошибка редактирования")
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun uploadAvatar(uri: Uri): DataResult<String> {
        return try {
            // 1. Обрезаем до квадрата
            val croppedFile = ImageUtils.cropToSquareFile(context, uri)
                ?: return DataResult.Error("Не удалось обработать фото")

            // 2. Сжимаем до 5 МБ
            val compressedFile = ImageUtils.compressImage(croppedFile, 5*1024)

            // 3. Отправляем
            val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", compressedFile.name, requestBody)
            val response = authApi.uploadAvatar(part)

            if (response.success) DataResult.Success("")
            else DataResult.Error(response.error ?: "Ошибка загрузки")
        } catch (e: Exception) {
            DataResult.Error("Ошибка: ${e.message}")
        }
    }
}