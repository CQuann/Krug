package com.example.krug.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.ApiResponse
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.auth.*
import com.example.krug.data.network.AuthApi
import com.example.krug.utils.ImageUtils
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitAuthRepository @Inject constructor(
    private val authApi: AuthApi,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val gson = Gson()

    private fun parseErrorMessage(e: Exception): String {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val apiResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                apiResponse.error ?: "Ошибка ${e.code()}"
            } catch (_: Exception) {
                "Ошибка ${e.code()}: ${e.message()}"
            }
        } else {
            "Ошибка сети: ${e.message}"
        }
    }

    override suspend fun requestCode(email: String): DataResult<Unit> {
        return try {
            val response = authApi.requestCode(EmailRequest(email))
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Неизвестная ошибка")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun verifyCode(email: String, code: String): DataResult<VerifyResult> {
        return try {
            val response = authApi.verifyCode(VerifyCodeRequest(email, code))
            if (response.error != null) return DataResult.Error(response.error)
            when {
                response.token != null && !response.isNewUser -> {
                    DataResult.Success(
                        VerifyResult.LoginSuccess(response.token, response.userId ?: "")
                    )
                }
                response.isNewUser -> DataResult.Success(VerifyResult.RegisterNeeded)
                else -> DataResult.Error("Неизвестный ответ сервера")
            }
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun register(userData: UserData): DataResult<Pair<String, String>> {
        return try {
            val request = RegisterRequest(
                RegisterUserData(
                    email = userData.email,
                    displayName = userData.displayName,
                    birthday = userData.birthday,
                    username = userData.username,
                    description = userData.description
                )
            )
            val response = authApi.register(request)
            Log.d("RetrofitAuthRepository", response.toString())
            if (response.token != null && response.userId != null) {
                DataResult.Success(Pair(response.token, response.userId))
            } else {
                DataResult.Error(response.error ?: "Ошибка регистрации")
            }
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun checkUsername(username: String): DataResult<Boolean> {
        return try {
            val response = authApi.checkUsername(CheckUsernameRequest(username))
            Log.d("RetrofitAuthRepository", response.success.toString())
            DataResult.Success(response.success)
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun validateToken(): DataResult<Boolean> {
        return try {
            val response = authApi.validateToken()
            if (response.success) DataResult.Success(true)
            else DataResult.Error(response.error ?: "Ошибка валидации токена")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun logout(): DataResult<Unit> {
        return try {
            val response = authApi.logout()
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Ошибка выхода")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun getUserData(): DataResult<UserData> {
        return try {
            val response = authApi.getUserData()
            Log.d("getUserData", response.toString())
            DataResult.Success(response.user)
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun editUserData(userData: UserData): DataResult<Unit> {
        return try {
            Log.d("editUserData", userData.toString())
            val response = authApi.editUserData(
                UserEditRequest(
                    username = userData.username,
                    displayName = userData.displayName,
                    birthday = userData.birthday,
                    description = userData.description
                )
            )
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Ошибка редактирования")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun uploadAvatar(uri: Uri): DataResult<Unit> {
        return try {
            val croppedFile = ImageUtils.cropToSquareFile(context, uri)
                ?: return DataResult.Error("Не удалось обработать фото")
            val compressedFile = ImageUtils.compressImage(croppedFile, 5 * 1024)
            val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", compressedFile.name, requestBody)
            val response = authApi.uploadAvatar(part)
            Log.d("Avatar upload", response.toString())
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Ошибка загрузки")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }
}