package com.example.krug.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    // Кэш токена для быстрого синхронного доступа (интерцептор)
    @Volatile
    var cachedToken: String? = null
        private set

    init {
        runBlocking {
            cachedToken = getToken()
        }
    }

    suspend fun saveToken(token: String) {
        cachedToken = token
        context.sessionDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    fun getTokenFlow(): Flow<String?> {
        return context.sessionDataStore.data.map { it[TOKEN_KEY] }
    }

    suspend fun getToken(): String? {
        return context.sessionDataStore.data.map { it[TOKEN_KEY] }.firstOrNull()
    }

    suspend fun clearToken() {
        cachedToken = null
        context.sessionDataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    suspend fun saveUserId(userId: String) {
        context.sessionDataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    fun getUserIdFlow(): Flow<String?> {
        return context.sessionDataStore.data.map { it[USER_ID_KEY] }
    }

    suspend fun getUserId(): String? {
        return context.sessionDataStore.data.map { it[USER_ID_KEY] }.firstOrNull()
    }

    suspend fun clearUserId() {
        context.sessionDataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
        }
    }

    // Полная очистка сессии
    suspend fun clearAll() {
        cachedToken = null
        context.sessionDataStore.edit { it.clear() }
    }
}