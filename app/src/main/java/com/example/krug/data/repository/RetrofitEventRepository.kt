package com.example.krug.data.repository

import android.content.Context
import android.net.Uri
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.event.CreateEventRequest
import com.example.krug.data.model.event.DetailedEvent
import com.example.krug.data.model.event.Event
import com.example.krug.data.model.event.EventsListResponse
import com.example.krug.data.model.event.StatusUpdateRequest
import com.example.krug.data.model.event.UpdateEventRequest
import com.example.krug.data.model.event.UpdateMemberPermissionsRequest
import com.example.krug.data.network.EventApi
import com.example.krug.utils.ImageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitEventRepository @Inject constructor(
    private val eventApi: EventApi,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : EventRepository {

    override suspend fun createEvent(request: CreateEventRequest): DataResult<Event> {
        return try {
            val response = eventApi.createEvent(request)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error("Ошибка: ${e.message}")
        }
    }

    override suspend fun getEvents(
        status: String,
        limit: Int,
        offset: Int
    ): DataResult<EventsListResponse> {
        return try {
            val response = eventApi.getEvents(status, limit, offset)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error("Ошибка загрузки событий: ${e.message}")
        }
    }

    override suspend fun getEvent(id: String): DataResult<DetailedEvent> {
        return try {
            val response = eventApi.getEvent(id)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error("Ошибка загрузки события: ${e.message}")
        }
    }

    override suspend fun updateEvent(id: String, request: UpdateEventRequest): DataResult<Event> {
        return try {
            val response = eventApi.updateEvent(id, request)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error("Ошибка обновления: ${e.message}")
        }
    }

    override suspend fun updateEventStatus(id: String, status: String): DataResult<Unit> {
        return try {
            val response = eventApi.updateEventStatus(id, StatusUpdateRequest(status))
            if (response.isSuccessful) DataResult.Success(Unit)
            else DataResult.Error("Ошибка изменения статуса: ${response.code()}")
        } catch (e: Exception) {
            DataResult.Error("Ошибка: ${e.message}")
        }
    }

    override suspend fun deleteEvent(id: String): DataResult<Unit> {
        return try {
            val response = eventApi.deleteEvent(id)
            if (response.isSuccessful) DataResult.Success(Unit)
            else DataResult.Error("Ошибка удаления: ${response.code()}")
        } catch (e: Exception) {
            DataResult.Error("Ошибка: ${e.message}")
        }
    }

    override suspend fun uploadEventAvatar(eventId: String, uri: Uri): DataResult<Unit> {
        return try {
            val croppedFile = ImageUtils.cropToSquareFile(context, uri)
                ?: return DataResult.Error("Не удалось обработать фото")
            val compressedFile = ImageUtils.compressImage(croppedFile, 5*1024)
            val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", compressedFile.name, requestBody)
            val response = eventApi.uploadEventAvatar(eventId, part)
            if (response.isSuccessful) DataResult.Success(Unit)
            else DataResult.Error("Ошибка загрузки: ${response.code()}")
        } catch (e: Exception) {
            DataResult.Error("Ошибка: ${e.message}")
        }
    }

    override suspend fun removeMember(eventId: String, userId: String): DataResult<Unit> {
        return try {
            val response = eventApi.removeMember(eventId, userId)
            if (response.isSuccessful) DataResult.Success(Unit)
            else DataResult.Error("Ошибка удаления участника: ${response.code()}")
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }

    override suspend fun updateMemberPermissions(eventId: String, userId: String, permissions: String): DataResult<Unit> {
        return try {
            val response = eventApi.updateMemberPermissions(eventId, userId,
                UpdateMemberPermissionsRequest(permissions)
            )
            if (response.isSuccessful) DataResult.Success(Unit)
            else DataResult.Error("Ошибка изменения прав: ${response.code()}")
        } catch (e: Exception) {
            DataResult.Error("Ошибка сети: ${e.message}")
        }
    }
}