package com.example.krug.data.network

import com.example.krug.data.model.ApiResponse
import com.example.krug.data.model.auth.UserDataResponse
import com.example.krug.data.model.auth.UserEditRequest
import com.example.krug.data.model.auth.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface AuthApi {
    @POST("auth/request_code")
    suspend fun requestCode(@Body request: EmailRequest): ApiResponse

    @POST("auth/verify_code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): VerifyCodeResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/token_validate")
    suspend fun validateToken(): ApiResponse

    @POST("auth/logout")
    suspend fun logout(): ApiResponse

    @POST("auth/check_username")
    suspend fun checkUsername(@Body request: CheckUsernameRequest): ApiResponse

    @GET("user/get_data")
    suspend fun getUserData(): UserDataResponse

    @POST("user/edit")
    suspend fun editUserData(@Body request: UserEditRequest): ApiResponse

    @Multipart
    @POST("user/avatar")
    suspend fun uploadAvatar(
        @Part avatar: MultipartBody.Part
    ): ApiResponse
}