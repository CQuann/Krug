package com.example.krug.data.model.event

data class CreateEventRequest(
    val title: String,
    val location: String? = null,
    val description: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val color: String? = null
)