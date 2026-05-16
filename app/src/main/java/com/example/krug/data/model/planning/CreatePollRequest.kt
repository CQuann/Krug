package com.example.krug.data.model.planning

data class CreatePollRequest(
    val event_id: String,
    val question: String,
    val options: List<String>,
    val multiple_choice: Boolean
)