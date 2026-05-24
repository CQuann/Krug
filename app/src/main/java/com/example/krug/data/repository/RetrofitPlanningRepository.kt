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
            if (response.success) Success(Unit)
            else Error(response.error ?: "Ошибка создания опроса")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }

    override suspend fun createItemList(eventId: String, request: CreateItemListRequest): DataResult<Unit> {
        return try {
            val response = api.createItemList(eventId, request)
            if (response.success) Success(Unit)
            else Error(response.error ?: "Ошибка создания списка вещей")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }

    override suspend fun createTaskList(eventId: String, request: CreateTaskListRequest): DataResult<Unit> {
        return try {
            val response = api.createTaskList(eventId, request)
            if (response.success) Success(Unit)
            else Error(response.error ?: "Ошибка создания списка задач")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }

    override suspend fun votePoll(eventId: String, pollId: String, optionIndexes: List<Int>): DataResult<Unit> {
        return try {
            val response = api.votePoll(eventId, pollId, VoteRequest(optionIndexes))
            if (response.success) Success(Unit)
            else Error(response.error ?: "Ошибка голосования")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }

    override suspend fun assignItem(
        eventId: String, type: String, moduleId: String, itemId: String, assign: Boolean
    ): DataResult<Unit> {
        return try {
            val response = api.assignItem(eventId, type, moduleId, itemId, AssignRequest(assign))
            if (response.success) Success(Unit)
            else Error(response.error ?: "Ошибка бронирования")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }

    override suspend fun completeTask(
        eventId: String, moduleId: String, itemId: String, completed: Boolean
    ): DataResult<Unit> {
        return try {
            val response = api.completeTask(eventId, moduleId, itemId, CompleteRequest(completed))
            if (response.success) Success(Unit)
            else Error(response.error ?: "Ошибка выполнения")
        } catch (e: Exception) {
            Error("Сетевая ошибка: ${e.message}")
        }
    }
}