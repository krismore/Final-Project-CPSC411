package com.example.finalproject.ui

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "mood_prefs")

class MoodDataStore(private val context: Context) {

    companion object {
        private val LAST_MOOD = stringPreferencesKey("last_mood")
    }

    val lastMoodFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[LAST_MOOD] }

    suspend fun saveMood(mood: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_MOOD] = mood
        }
    }
}
