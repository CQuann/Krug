package com.example.krug.data.repository

import android.content.Context
import android.net.Uri
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.album.CreateAlbumRequest
import com.example.krug.data.model.album.AlbumResponse
import com.example.krug.data.model.album.AlbumWithPhotosResponse
import com.example.krug.data.model.album.PhotoResponse
import com.example.krug.data.network.AlbumApi
import com.example.krug.utils.ImageUtils
import com.example.krug.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitAlbumRepository @Inject constructor(
    private val api: AlbumApi,
    @ApplicationContext private val context: Context
) : AlbumRepository {

    override suspend fun createAlbum(eventId: String, title: String, description: String?): DataResult<AlbumResponse> {
        return try {
            val response = api.createAlbum(eventId, CreateAlbumRequest(title, description))
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(NetworkUtils.parseErrorMessage(e))
        }
    }

    override suspend fun getAlbums(eventId: String): DataResult<List<AlbumResponse>> {
        return try {
            DataResult.Success(api.getAlbums(eventId))
        } catch (e: Exception) {
            DataResult.Error(NetworkUtils.parseErrorMessage(e))
        }
    }

    override suspend fun getAlbum(eventId: String, albumId: Long): DataResult<AlbumWithPhotosResponse> {
        return try {
            DataResult.Success(api.getAlbum(eventId, albumId))
        } catch (e: Exception) {
            DataResult.Error(NetworkUtils.parseErrorMessage(e))
        }
    }

    override suspend fun uploadPhoto(eventId: String, albumId: Long, uri: Uri): DataResult<PhotoResponse> {
        return try {
            val file = ImageUtils.cropToSquareFile(context, uri)
                ?: return DataResult.Error("Не удалось обработать фото")
            val compressed = ImageUtils.compressPhoto(file)
            val requestBody = compressed.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("photo", compressed.name, requestBody)
            val response = api.uploadPhoto(eventId, albumId, part)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(NetworkUtils.parseErrorMessage(e))
        }
    }

    override suspend fun deleteAlbum(eventId: String, albumId: Long): DataResult<Unit> {
        return try {
            val response = api.deleteAlbum(eventId, albumId)
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Ошибка удаления альбома")
        } catch (e: Exception) {
            DataResult.Error(NetworkUtils.parseErrorMessage(e))
        }
    }

    override suspend fun deletePhoto(eventId: String, albumId: Long, photoId: Long): DataResult<Unit> {
        return try {
            val response = api.deletePhoto(eventId, albumId, photoId)
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Ошибка удаления фото")
        } catch (e: Exception) {
            DataResult.Error(NetworkUtils.parseErrorMessage(e))
        }
    }
}