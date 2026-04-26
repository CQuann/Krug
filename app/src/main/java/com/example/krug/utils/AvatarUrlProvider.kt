package com.example.krug.utils

import com.example.krug.utils.Constants.BASE_URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvatarUrlProvider @Inject constructor() {

    companion object {
        fun build(userId: String): String = "$BASE_URL/avatars/$userId.jpg"
    }
}