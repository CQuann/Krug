package com.example.krug.data.model.album

import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @SerializedName("photo_id") val photoId: Long,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("mime_type") val mimeType: String?,
    @SerializedName("file_size") val fileSize: Long?,
    @SerializedName("uploaded_by") val uploadedBy: Long,
    @SerializedName("created_at") val createdAt: String,
    val url: String   // относительный путь, например "/events/42/albums/1/photos/10"
)