package com.example.krug.data.model.event

data class Event(
    val id: String,
    val title: String,
    val description: String?,
    val location: String?,
    val startDateTime: String?,   // ISO 8601 string ("2025-05-10T15:00:00Z" или "2025-05-10")
    val endDateTime: String?,
    val color: String?,
    val createdBy: String,
    val createdAt: String,
    val status: String            // "active" или "archived"
)