package com.pararam2006.tseletrack.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenStore(
    private val dataStore: DataStore<Preferences>,
) {
    private val tokenKey = stringPreferencesKey("access_token")

    suspend fun getToken(): String? = dataStore.data
        .map { preferences -> preferences[tokenKey] }
        .first()

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences -> preferences[tokenKey] = token }
    }

    suspend fun clear() {
        dataStore.edit { preferences -> preferences.remove(tokenKey) }
    }
}
