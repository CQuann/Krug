package com.example.krug.data.network

import com.example.krug.data.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/request-code")
    suspend fun requestCode(@Body request: EmailRequest): RequestCodeResponse

    @POST("auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): VerifyCodeResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/token-validate")
    suspend fun validateToken(@Body request: TokenValidateRequest): TokenValidateResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): LogoutResponse

    @POST("auth/check_username")
    suspend fun checkUsername(@Body request: CheckUsernameRequest): CheckUsernameResponse
}