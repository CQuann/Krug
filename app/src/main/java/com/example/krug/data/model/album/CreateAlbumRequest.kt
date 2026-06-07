package com.example.krug.data.model.album

data class CreateAlbumRequest(
    val title: String,
    val description: String? = null
)