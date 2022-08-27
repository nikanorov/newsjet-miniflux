package com.nikanorov.newsjetminiflux.data.settings

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class UserPreferences(
    val minifluxApiUrl: String,
    val minifluxApiToken: String
)

class UserPreferencesRepository constructor(private val dataStore: DataStore<Preferences>) {
    private val TAG: String = "miniflux-UserPrefRepo"

    private object PreferencesKeys {
        val MINIFLUX_API_URL = stringPreferencesKey("miniflux_api_url")
        val MINIFLUX_API_TOKEN = stringPreferencesKey("miniflux_api_token")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.e(TAG, "Error reading preferences.", e)
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun updateMinifluxApiUrl(apiUrl: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MINIFLUX_API_URL] = apiUrl
        }
    }

    suspend fun getMinifluxApiUrl(apiUrl: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MINIFLUX_API_URL] = apiUrl
        }
    }


    suspend fun updateMinifluxApiToken(apiToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MINIFLUX_API_TOKEN] = apiToken
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {

        val minifluxApiUrl = preferences[PreferencesKeys.MINIFLUX_API_URL] ?: ""
        val minifluxApiToken = preferences[PreferencesKeys.MINIFLUX_API_TOKEN] ?: ""

        return UserPreferences(
            minifluxApiUrl = minifluxApiUrl,
            minifluxApiToken = minifluxApiToken
        )
    }

}
