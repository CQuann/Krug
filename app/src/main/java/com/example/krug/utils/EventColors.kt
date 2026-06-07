package com.example.krug.utils

import androidx.annotation.DrawableRes
import com.example.krug.R

data class EventColor(val hex: String, val name: String, @DrawableRes val bgResId: Int)

object EventColors {
    val colors = listOf(
        EventColor("#FFE165", "Желтый", R.drawable.bg_event_yellow),
        EventColor("#ABFDFD", "Голубой", R.drawable.bg_event_blue),
        EventColor("#BAFF8D", "Салатовый", R.drawable.bg_event_green),
        EventColor("#FFC4BA", "Розовый", R.drawable.bg_event_pink),
        EventColor("#9297FF", "Фиолетовый", R.drawable.bg_event_violet),
        EventColor("#FF99B0", "Малиновый", R.drawable.bg_event_raspberry),
        EventColor("#AD8A76", "Терракотовый", R.drawable.bg_event_terracotta),
        EventColor("#FFB469", "Оранжевый", R.drawable.bg_event_orange)
    )
}