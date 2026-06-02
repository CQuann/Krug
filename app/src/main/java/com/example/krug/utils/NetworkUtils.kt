package com.example.krug.utils

import com.example.krug.data.model.ApiResponse
import com.google.gson.Gson
import retrofit2.HttpException

object NetworkUtils {

    private val gson = Gson()

    /**
     * Преобразует исключение сети/Retrofit в читаемое сообщение для пользователя.
     */
    fun parseErrorMessage(e: Exception): String {
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
}