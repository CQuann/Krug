package com.example.krug.data.network

import com.example.krug.data.model.TokenValidateResponse
import com.example.krug.data.model.UserDataResponse
import com.example.krug.data.model.UserEditRequest
import com.example.krug.data.model.UserEditResponse
import com.example.krug.data.model.auth.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface AuthApi {
    @POST("auth/request-code")
    suspend fun requestCode(@Body request: EmailRequest): RequestCodeResponse

    @POST("auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): VerifyCodeResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/token-validate")
    suspend fun validateToken(): TokenValidateResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): LogoutResponse

    @POST("auth/check-username")
    suspend fun checkUsername(@Body request: CheckUsernameRequest): CheckUsernameResponse

    @GET("user/get-data")
    suspend fun getUserData(): UserDataResponse

    @POST("user/edit")
    suspend fun editUserData(@Body request: UserEditRequest): UserEditResponse

    @Multipart
    @POST("user/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): AvatarUploadResponse
}