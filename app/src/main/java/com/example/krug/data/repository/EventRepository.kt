package com.example.krug.data.repository

import android.net.Uri
import com.example.krug.data.model.event.*
import com.example.krug.data.model.DataResult

interface EventRepository {
    suspend fun createEvent(request: CreateEventRequest): DataResult<Event>
    suspend fun getEvents(status: String, limit: Int, offset: Int): DataResult<EventsListResponse>
    suspend fun getEvent(id: String): DataResult<Event>
    suspend fun updateEvent(id: String, request: UpdateEventRequest): DataResult<Event>
    suspend fun updateEventStatus(id: String, status: String): DataResult<Unit>
    suspend fun deleteEvent(id: String): DataResult<Unit>
    suspend fun uploadEventAvatar(eventId: String, uri: Uri): DataResult<Unit>
}