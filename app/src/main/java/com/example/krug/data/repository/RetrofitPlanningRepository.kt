package com.example.krug.data.repository

import com.example.krug.data.model.DataResult
import com.example.krug.data.model.DataResult.*
import com.example.krug.data.network.PlanningApi
import com.example.krug.data.model.planning.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitPlanningRepository @Inject constructor(
    private val api: PlanningApi
) : PlanningRepository {

    override suspend fun getPlanningModules(eventId: String): DataResult<PlanningModulesResponse> {
        return try {
            val response = api.getPlanningModules(eventId)
            Success(response)
        } catch (e: Exception) {
            Error("Ошибка загрузки модулей: ${e.message}")
        }
    }

    override suspend fun createPoll(eventId: String, request: CreatePollRequest): DataResult<Unit> {
        return try {
            val response = api.createPoll(eventId, request)
            if (response.isSuccessful) Success(Unit)
            else Error("Ошибка создания опроса: ${response.code()}")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }

    override suspend fun createItemList(eventId: String, request: CreateItemListRequest): DataResult<Unit> {
        return try {
            val response = api.createItemList(eventId, request)
            if (response.isSuccessful) Success(Unit)
            else Error("Ошибка создания списка вещей: ${response.code()}")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }

    override suspend fun createTaskList(eventId: String, request: CreateTaskListRequest): DataResult<Unit> {
        return try {
            val response = api.createTaskList(eventId, request)
            if (response.isSuccessful) Success(Unit)
            else Error("Ошибка создания списка задач: ${response.code()}")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }
}