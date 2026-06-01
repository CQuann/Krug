package com.example.krug.data.network

import com.example.krug.data.model.ApiResponse
import com.example.krug.data.model.event.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface EventApi {

    @POST("events")
    suspend fun createEvent(@Body request: CreateEventRequest): Event

    @GET("events")
    suspend fun getEvents(
        @Query("status") status: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): EventsListResponse

    @GET("events/{id}")
    suspend fun getEvent(@Path("id") id: String): DetailedEvent

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: String,
        @Body request: UpdateEventRequest
    ): Event

    @PATCH("events/{id}/status")
    suspend fun updateEventStatus(
        @Path("id") id: String,
        @Body request: StatusUpdateRequest
    ): ApiResponse

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): ApiResponse

    @Multipart
    @POST("events/{id}/avatar")
    suspend fun uploadEventAvatar(
        @Path("id") eventId: String,
        @Part avatar: MultipartBody.Part
    ): ApiResponse

    @DELETE("events/{eventId}/members/{userId}")
    suspend fun removeMember(
        @Path("eventId") eventId: String,
        @Path("userId") userId: String
    ): ApiResponse

    @PATCH("events/{eventId}/members/{userId}")
    suspend fun updateMemberPermissions(
        @Path("eventId") eventId: String,
        @Path("userId") userId: String,
        @Body body: UpdateMemberPermissionsRequest
    ): ApiResponse

    @POST("events/join")
    suspend fun joinEvent(@Body request: JoinEventRequest): Event
}