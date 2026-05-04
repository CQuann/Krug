package com.example.krug.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object DateUtils {

    fun toIsoString(date: LocalDate?, time: LocalTime?): String? {
        return when {
            date == null -> null
            time == null -> date.toString()
            else -> LocalDateTime.of(date, time).toString()
        }
    }

    fun parseDate(dateStr: String?): LocalDate? {
        return dateStr?.let { LocalDate.parse(it) }
    }

    fun parseTime(timeStr: String?): LocalTime? {
        return timeStr?.let { LocalTime.parse(it) }
    }
}