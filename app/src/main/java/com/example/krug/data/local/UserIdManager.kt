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
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userIdDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_id")

@Singleton
class UserIdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    suspend fun saveUserId(userId: String) {
        context.userIdDataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    fun getUserIdFlow(): Flow<String?> {
        return context.userIdDataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }
    }

    suspend fun getUserId(): String? {
        return context.userIdDataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }.firstOrNull()
    }

    suspend fun clearUserId() {
        context.userIdDataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
        }
    }
}