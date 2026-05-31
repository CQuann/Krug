package com.example.krug.data.repository

import com.example.krug.data.model.ApiResponse
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.DataResult.*
import com.example.krug.data.network.PlanningApi
import com.example.krug.data.model.planning.*
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitPlanningRepository @Inject constructor(
    private val api: PlanningApi
) : PlanningRepository {

    private val gson = Gson()

    private fun parseErrorMessage(e: Exception): String {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val apiResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                apiResponse.error ?: "Ошибка ${e.code()}"
            } catch (_: Exception) {
                "Ошибка ${e.code()}: ${e.message()}"
            }
        } else {
            "Ошибка сети: ${e.message}"
        }
    }

    override suspend fun getPlanningModules(eventId: String): DataResult<PlanningModulesResponse> {
        return try {
            Success(api.getPlanningModules(eventId))
        } catch (e: Exception) {
            Error(parseErrorMessage(e))
        }
    }

    override suspend fun createPoll(eventId: String, request: CreatePollRequest): DataResult<PlanningModule> {
        return try {
            Success(api.createPoll(eventId, request))
        } catch (e: Exception) {
            Error(parseErrorMessage(e))
        }
    }

    override suspend fun createItemList(eventId: String, request: CreateItemListRequest): DataResult<PlanningModule> {
        return try {
            Success(api.createItemList(eventId, request))
        } catch (e: Exception) {
            Error(parseErrorMessage(e))
        }
    }

    override suspend fun createTaskList(eventId: String, request: CreateTaskListRequest): DataResult<PlanningModule> {
        return try {
            Success(api.createTaskList(eventId, request))
        } catch (e: Exception) {
            Error(parseErrorMessage(e))
        }
    }

    override suspend fun votePoll(eventId: String, pollId: String, optionIndexes: List<Int>): DataResult<Unit> {
        return try {
            val response = api.votePoll(eventId, pollId, VoteRequest(optionIndexes))
            if (response.success) Success(Unit)
            else Error(response.error ?: "Ошибка голосования")
        } catch (e: Exception) {
            Error(parseErrorMessage(e))
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
            Error(parseErrorMessage(e))
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
            Error(parseErrorMessage(e))
        }
    }
}