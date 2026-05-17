package com.example.krug.data.network

import com.example.krug.data.model.planning.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlanningApi {

    @GET("events/{eventId}/planning")
    suspend fun getPlanningModules(
        @Path("eventId") eventId: String
    ): PlanningModulesResponse

    @POST("events/{eventId}/planning/poll")
    suspend fun createPoll(
        @Path("eventId") eventId: String,
        @Body request: CreatePollRequest
    ): Response<Unit>

    @POST("events/{eventId}/planning/items")
    suspend fun createItemList(
        @Path("eventId") eventId: String,
        @Body request: CreateItemListRequest
    ): Response<Unit>

    @POST("events/{eventId}/planning/tasks")
    suspend fun createTaskList(
        @Path("eventId") eventId: String,
        @Body request: CreateTaskListRequest
    ): Response<Unit>

    // Голосование в опросе
    @POST("events/{eventId}/planning/poll/{pollId}/vote")
    suspend fun votePoll(
        @Path("eventId") eventId: String,
        @Path("pollId") pollId: String,
        @Body request: VoteRequest
    ): Response<PlanningModule>

    // Бронирование / отказ от бронирования (вещи и задачи)
    @POST("events/{eventId}/planning/{type}/{moduleId}/items/{itemId}/assign")
    suspend fun assignItem(
        @Path("eventId") eventId: String,
        @Path("type") type: String,        // "items" или "tasks"
        @Path("moduleId") moduleId: String,
        @Path("itemId") itemId: String,
        @Body request: AssignRequest
    ): Response<PlanningModule>

    // Отметка о выполнении задачи
    @POST("events/{eventId}/planning/tasks/{moduleId}/items/{itemId}/complete")
    suspend fun completeTask(
        @Path("eventId") eventId: String,
        @Path("moduleId") moduleId: String,
        @Path("itemId") itemId: String,
        @Body request: CompleteRequest
    ): Response<PlanningModule>
}