package com.example.krug.data.repository

import com.example.krug.data.model.DataResult
import com.example.krug.data.model.planning.CreatePollRequest
import com.example.krug.data.network.PlanningApi
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class RetrofitPlanningRepository @Inject constructor(
    private val api: PlanningApi
) : PlanningRepository {
    override suspend fun createPoll(request: CreatePollRequest): DataResult<Unit> {
        return try {
            val response = api.createPoll(request)
            if (response.isSuccessful) DataResult.Success(Unit)
            else DataResult.Error("Ошибка создания опроса: ${response.code()}")
        } catch (e: Exception) {
            DataResult.Error("Сетевая ошибка: ${e.message}")
        }
    }
}