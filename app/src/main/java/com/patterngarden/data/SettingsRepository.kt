package com.patterngarden.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
    }

    val soundEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[SOUND_ENABLED] ?: true }
    val musicEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[MUSIC_ENABLED] ?: true }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SOUND_ENABLED] = enabled }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[MUSIC_ENABLED] = enabled }
    }
}
