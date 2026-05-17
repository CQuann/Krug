package com.example.krug.data.network

import com.example.krug.data.model.TokenValidateResponse
import com.example.krug.data.model.UserDataResponse
import com.example.krug.data.model.UserEditRequest
import com.example.krug.data.model.UserEditResponse
import com.example.krug.data.model.auth.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface AuthApi {
    @POST("auth/request_code")
    suspend fun requestCode(@Body request: EmailRequest): RequestCodeResponse

    @POST("auth/verify_code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): VerifyCodeResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/token_validate")
    suspend fun validateToken(): TokenValidateResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): LogoutResponse

    @POST("auth/check_username")
    suspend fun checkUsername(@Body request: CheckUsernameRequest): CheckUsernameResponse

    @GET("user/get-data")
    suspend fun getUserData(): UserDataResponse

    @POST("user/edit")
    suspend fun editUserData(@Body request: UserEditRequest): UserEditResponse

    @Multipart
    @POST("user/avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") token: String?,
        @Part avatar: MultipartBody.Part
    ): AvatarUploadResponse
}