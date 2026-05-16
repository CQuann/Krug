package com.example.krug.data.network

import com.example.krug.data.model.planning.CreatePollRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PlanningApi {
    @POST("planning/poll")
    suspend fun createPoll(@Body request: CreatePollRequest): Response<Unit>
}