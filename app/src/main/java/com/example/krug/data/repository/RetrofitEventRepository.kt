package com.example.krug.data.repository

import android.content.Context
import android.net.Uri
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.event.*
import com.example.krug.data.network.EventApi
import com.example.krug.utils.ImageUtils
import com.example.krug.utils.NetworkUtils.parseErrorMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitEventRepository @Inject constructor(
    private val eventApi: EventApi,
    @ApplicationContext private val context: Context
) : EventRepository {

    override suspend fun createEvent(request: CreateEventRequest): DataResult<Event> {
        return try {
            val response = eventApi.createEvent(request)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
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
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun getEvent(id: String): DataResult<DetailedEvent> {
        return try {
            val response = eventApi.getEvent(id)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun updateEvent(id: String, request: UpdateEventRequest): DataResult<Event> {
        return try {
            val response = eventApi.updateEvent(id, request)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun updateEventStatus(id: String, status: String): DataResult<Unit> {
        return try {
            val response = eventApi.updateEventStatus(id, StatusUpdateRequest(status))
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Неизвестная ошибка")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun deleteEvent(id: String): DataResult<Unit> {
        return try {
            val response = eventApi.deleteEvent(id)
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Неизвестная ошибка")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun uploadEventAvatar(eventId: String, uri: Uri): DataResult<Unit> {
        return try {
            val croppedFile = ImageUtils.cropToSquareFile(context, uri)
                ?: return DataResult.Error("Не удалось обработать фото")
            val compressedFile = ImageUtils.compressPhoto(croppedFile)
            val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", compressedFile.name, requestBody)
            val response = eventApi.uploadEventAvatar(eventId, part)
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Неизвестная ошибка")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun removeMember(eventId: String, userId: String): DataResult<Unit> {
        return try {
            val response = eventApi.removeMember(eventId, userId)
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Неизвестная ошибка")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun updateMemberPermissions(
        eventId: String,
        userId: String,
        permissions: String
    ): DataResult<Unit> {
        return try {
            val response = eventApi.updateMemberPermissions(
                eventId, userId,
                UpdateMemberPermissionsRequest(permissions)
            )
            if (response.success) DataResult.Success(Unit)
            else DataResult.Error(response.error ?: "Неизвестная ошибка")
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }

    override suspend fun joinEvent(inviteToken: String): DataResult<Event> {
        return try {
            val response = eventApi.joinEvent(JoinEventRequest(inviteToken))
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(parseErrorMessage(e))
        }
    }
}