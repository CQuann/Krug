package com.example.krug.data.model.event

data class EventsListResponse(
    val items: List<Event>,
    val total: Int
)