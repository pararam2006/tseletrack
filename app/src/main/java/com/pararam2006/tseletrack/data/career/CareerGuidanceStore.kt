package com.pararam2006.tseletrack.data.career

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pararam2006.tseletrack.domain.career.CareerGuidanceSnapshot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class CareerGuidanceStore(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) {
    suspend fun load(): CareerGuidanceSnapshot {
        val rawValue = dataStore.data.map { it[snapshotKey] }.first()
            ?: return CareerGuidanceSnapshot()
        return try {
            json.decodeFromString(rawValue)
        } catch (_: SerializationException) {
            CareerGuidanceSnapshot()
        } catch (_: IllegalArgumentException) {
            CareerGuidanceSnapshot()
        }
    }

    suspend fun save(snapshot: CareerGuidanceSnapshot) {
        dataStore.edit { preferences ->
            preferences[snapshotKey] = json.encodeToString(snapshot)
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences -> preferences.remove(snapshotKey) }
    }

    private companion object {
        val snapshotKey = stringPreferencesKey("career_guidance_snapshot")
    }
}
