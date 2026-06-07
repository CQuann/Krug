package com.example.krug.data.network

import com.example.krug.data.model.ApiResponse
import com.example.krug.data.model.album.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface AlbumApi {
    @POST("events/{eventId}/albums")
    suspend fun createAlbum(
        @Path("eventId") eventId: String,
        @Body request: CreateAlbumRequest
    ): AlbumResponse

    @GET("events/{eventId}/albums")
    suspend fun getAlbums(
        @Path("eventId") eventId: String
    ): List<AlbumResponse>

    @GET("events/{eventId}/albums/{albumId}")
    suspend fun getAlbum(
        @Path("eventId") eventId: String,
        @Path("albumId") albumId: Long
    ): AlbumWithPhotosResponse

    @Multipart
    @POST("events/{eventId}/albums/{albumId}/photos")
    suspend fun uploadPhoto(
        @Path("eventId") eventId: String,
        @Path("albumId") albumId: Long,
        @Part photo: MultipartBody.Part
    ): PhotoResponse

    @DELETE("events/{eventId}/albums/{albumId}")
    suspend fun deleteAlbum(
        @Path("eventId") eventId: String,
        @Path("albumId") albumId: Long
    ): ApiResponse

    @DELETE("events/{eventId}/albums/{albumId}/photos/{photoId}")
    suspend fun deletePhoto(
        @Path("eventId") eventId: String,
        @Path("albumId") albumId: Long,
        @Path("photoId") photoId: Long
    ): ApiResponse
}