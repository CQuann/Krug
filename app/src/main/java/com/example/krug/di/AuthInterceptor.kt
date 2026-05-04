package com.example.krug.di

import com.example.krug.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    // Эндпоинты, не требующие авторизации
    private val publicPaths = setOf(
        "/auth/request-code",
        "/auth/verify-code",
        "/auth/register",
        "/auth/check-username"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        if (publicPaths.contains(path)) {
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.cachedToken
        val newRequest = if (token.isNullOrBlank()) {
            originalRequest
        } else {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(newRequest)
    }
}