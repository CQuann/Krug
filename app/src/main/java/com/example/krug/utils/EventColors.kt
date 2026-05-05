package com.example.krug.utils

data class EventColor(val hex: String, val name: String)

object EventColors {
    val colors = listOf(
        EventColor("#FF5733", "Красный"),
        EventColor("#FF8D1A", "Оранжевый"),
        EventColor("#FFC300", "Желтый"),
        EventColor("#28B463", "Зеленый"),
        EventColor("#3498DB", "Голубой"),
        EventColor("#2980B9", "Синий"),
        EventColor("#8E44AD", "Фиолетовый"),
        EventColor("#FF69B4", "Розовый")
    )
}