package com.helmut.data.repository

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        private val NOTIFICATION_SOUND_URI = stringPreferencesKey("notification_sound_uri")
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    }

    val vibrationEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[VIBRATION_ENABLED] ?: true
    }

    val notificationEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_ENABLED] ?: true
    }

    val notificationSoundUri: Flow<Uri> = dataStore.data.map { preferences ->
        val uriString = preferences[NOTIFICATION_SOUND_URI]
        if (uriString != null) {
            Uri.parse(uriString)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun setNotificationSoundUri(uri: Uri) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_SOUND_URI] = uri.toString()
        }
    }
}
