package com.example.krug.data.network

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
    suspend fun getEvent(@Path("id") id: String): Event

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: String,
        @Body request: UpdateEventRequest
    ): Event

    @PATCH("events/{id}/status")
    suspend fun updateEventStatus(
        @Path("id") id: String,
        @Body request: StatusUpdateRequest
    ): retrofit2.Response<Unit>

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): retrofit2.Response<Unit>

    @Multipart
    @POST("events/{id}/avatar")
    suspend fun uploadEventAvatar(
        @Path("id") eventId: String,
        @Part avatar: MultipartBody.Part
    ): retrofit2.Response<Unit>
}