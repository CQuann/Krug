package com.example.krug.ui.screens.event.createEvent

import java.time.LocalDate
import java.time.LocalTime

data class EventFormData(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val startDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endDate: LocalDate? = null,
    val endTime: LocalTime? = null,
    val color: String = "#FF5733"
)