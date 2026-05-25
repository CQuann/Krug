package com.example.krug.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateUtils {

    private val isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME   // "2025-05-10T15:00:00Z"

    /**
     * Преобразует дату и время в ISO‑строку для отправки на сервер.
     * Если время не задано – только дата (YYYY-MM-DD),
     * иначе дата‑время в UTC с секундами и суффиксом Z (YYYY-MM-DDTHH:MM:SSZ).
     */
    fun toIsoString(date: LocalDate?, time: LocalTime?): String? {
        return when {
            date == null -> null
            time == null -> date.toString()
            else -> {
                // Дополняем время секундами (00), если они не заданы
                val fullTime = time.withSecond(0)
                OffsetDateTime.of(date, fullTime, ZoneOffset.UTC).format(isoFormatter)
            }
        }
    }

    /**
     * Извлекает дату из ISO‑8601 строки (поддерживает форматы с зоной и без).
     */
    fun parseDate(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            LocalDate.parse(dateStr)
        } catch (e: DateTimeParseException) {
            try {
                OffsetDateTime.parse(dateStr).toLocalDate()
            } catch (e2: DateTimeParseException) {
                try {
                    LocalDateTime.parse(dateStr).toLocalDate()
                } catch (e3: DateTimeParseException) {
                    null
                }
            }
        }
    }

    /**
     * Извлекает время из ISO‑8601 строки.
     */
    fun parseTime(timeStr: String?): LocalTime? {
        if (timeStr.isNullOrBlank()) return null
        return try {
            LocalTime.parse(timeStr)
        } catch (e: DateTimeParseException) {
            try {
                OffsetDateTime.parse(timeStr).toLocalTime()
            } catch (e2: DateTimeParseException) {
                try {
                    LocalDateTime.parse(timeStr).toLocalTime()
                } catch (e3: DateTimeParseException) {
                    null
                }
            }
        }
    }

    /**
     * Форматирует дату и время для отображения пользователю.
     * На вход ожидается ISO-8601 строка.
     */
    fun formatFullDateTime(iso: String?): String? {
        val date = parseDate(iso) ?: return null
        val time = parseTime(iso)
        return buildString {
            append(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
            time?.let { append(" ${it.format(DateTimeFormatter.ofPattern("HH:mm"))}") }
        }.ifEmpty { null }
    }
}