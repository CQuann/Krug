package com.example.krug.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateUtils {

    /**
     * Преобразует дату и время в ISO‑строку для отправки на сервер.
     * Если время не задано – только дата (YYYY-MM-DD),
     * иначе полное локальное дата‑время (YYYY-MM-DDTHH:MM:SS).
     */
    fun toIsoString(date: LocalDate?, time: LocalTime?): String? {
        return when {
            date == null -> null
            time == null -> date.toString()
            else -> LocalDateTime.of(date, time).toString()
        }
    }

    /**
     * Извлекает дату из ISO‑8601 строки, содержащей как только дату, так и дату‑время с зоной.
     * Например: "2026-05-10", "2026-05-10T15:00:00", "2026-05-10T15:00:00Z",
     * "2026-05-10T15:00:00+03:00" – всегда вернёт LocalDate.
     */
    fun parseDate(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            // Пробуем как дату
            LocalDate.parse(dateStr)
        } catch (e: DateTimeParseException) {
            try {
                // Пробуем как OffsetDateTime (с зоной)
                OffsetDateTime.parse(dateStr).toLocalDate()
            } catch (e2: DateTimeParseException) {
                try {
                    // Пробуем как LocalDateTime (без зоны)
                    LocalDateTime.parse(dateStr).toLocalDate()
                } catch (e3: DateTimeParseException) {
                    null
                }
            }
        }
    }

    /**
     * Извлекает время из ISO‑8601 строки.
     * Например: "2026-05-10T15:00:00", "15:00:00", "2026-05-10T15:00:00Z" – вернёт LocalTime.
     * Если передан только дата – вернёт null.
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

    fun formatFullDateTime(iso: String?): String? {
        val date = parseDate(iso) ?: return null
        val time = parseTime(iso)
        return buildString {
            append(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
            time?.let { append(" ${it.format(DateTimeFormatter.ofPattern("HH:mm"))}") }
        }.ifEmpty { null }
    }
}