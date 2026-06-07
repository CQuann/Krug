package com.example.krug.data.model.album

import com.google.gson.annotations.SerializedName

data class AlbumWithPhotosResponse(
    @SerializedName("album_id") val albumId: Long,
    @SerializedName("event_id") val eventId: String,
    val title: String,
    val description: String?,
    @SerializedName("created_by") val createdBy: Long,
    @SerializedName("created_at") val createdAt: String,
    val photos: List<PhotoResponse>
)