package com.example.krug.data.network

import com.example.krug.data.model.*
import com.example.krug.data.model.auth.CheckUsernameRequest
import com.example.krug.data.model.auth.CheckUsernameResponse
import com.example.krug.data.model.auth.EmailRequest
import com.example.krug.data.model.auth.LogoutRequest
import com.example.krug.data.model.auth.LogoutResponse
import com.example.krug.data.model.auth.RegisterRequest
import com.example.krug.data.model.auth.RegisterResponse
import com.example.krug.data.model.auth.RequestCodeResponse
import com.example.krug.data.model.auth.VerifyCodeRequest
import com.example.krug.data.model.auth.VerifyCodeResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/request-code")
    suspend fun requestCode(@Body request: EmailRequest): RequestCodeResponse

    @POST("auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): VerifyCodeResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/token-validate")
    suspend fun validateToken(@Header("Authorization") token: String): TokenValidateResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): LogoutResponse

    @POST("auth/check-username")
    suspend fun checkUsername(@Body request: CheckUsernameRequest): CheckUsernameResponse

    @GET("user/get-data")
    suspend fun getUserData(@Header("Authorization") token: String): UserDataResponse

    @POST("user/edit")
    suspend fun editUserData(
        @Header("Authorization") token: String,
        @Body request: UserEditRequest
    ): UserEditResponse
}