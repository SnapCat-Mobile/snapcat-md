package com.snapcat.data.local.preferences

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class UserDataStore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    suspend fun saveUserData(userId: String?, username: String?, token: String?, email: String?) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_USER_ID] = userId ?: ""
            preferences[SESSION_USERNAME] = username ?: ""
            preferences[SESSION_TOKEN] = token ?: ""
            preferences[SESSION_EMAIL] = email ?: ""
        }
    }

    fun getUserData() = context.dataStore.data.map { preferences ->
        UserDataPreferences(
            userId = preferences[SESSION_USER_ID] ?: "",
            username = preferences[SESSION_USERNAME] ?: "",
            token = preferences[SESSION_TOKEN] ?: "",
            email = preferences[SESSION_EMAIL] ?: "",
        )
    }

    suspend fun deleteSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_USER_ID)
            preferences.remove(SESSION_USERNAME)
            preferences.remove(SESSION_TOKEN)
            preferences.remove(SESSION_EMAIL)
        }
    }

    companion object {
        val SESSION_USER_ID = stringPreferencesKey("session_user_id")
        val SESSION_USERNAME = stringPreferencesKey("session_username")
        val SESSION_TOKEN = stringPreferencesKey("session_token")
        val SESSION_EMAIL = stringPreferencesKey("session_email")

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: UserDataStore? = null

        fun getInstance(context: Context): UserDataStore {
            return instance ?: synchronized(this) {
                instance ?: UserDataStore(context).also { instance = it }
            }
        }
    }
}
