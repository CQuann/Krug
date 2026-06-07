package com.example.krug.data.repository

import android.net.Uri
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.album.AlbumResponse
import com.example.krug.data.model.album.AlbumWithPhotosResponse
import com.example.krug.data.model.album.PhotoResponse

interface AlbumRepository {
    suspend fun createAlbum(eventId: String, title: String, description: String?): DataResult<AlbumResponse>
    suspend fun getAlbums(eventId: String): DataResult<List<AlbumResponse>>
    suspend fun getAlbum(eventId: String, albumId: Long): DataResult<AlbumWithPhotosResponse>
    suspend fun uploadPhoto(eventId: String, albumId: Long, uri: Uri): DataResult<PhotoResponse>
    suspend fun deleteAlbum(eventId: String, albumId: Long): DataResult<Unit>
    suspend fun deletePhoto(eventId: String, albumId: Long, photoId: Long): DataResult<Unit>
}