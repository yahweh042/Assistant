package io.github.merlin.assistant.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class LocalDataSource(
    private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val accountWrapperKey = stringPreferencesKey("accountWrapperKey")
    private val jewelSettingsKey = stringPreferencesKey("jewelSettingsKey")
    private val potSettingsKey = stringPreferencesKey("potSettingsKey")

    val accountWrapperFlow = context.dataStore.data.map {
        it[accountWrapperKey]
    }

    suspend fun setAccountWrapper(accountWrapper: String) {
        context.dataStore.edit {
            it[accountWrapperKey] = accountWrapper
        }
    }

    val jewelSettingsFlow = context.dataStore.data.map {
        it[jewelSettingsKey]
    }

    suspend fun setJewelSettings(jewelSettings: String) {
        context.dataStore.edit {
            it[jewelSettingsKey] = jewelSettings
        }
    }

    val potSettingsFlow = context.dataStore.data.map {
        it[potSettingsKey]
    }

    suspend fun setPotSettings(potSettings: String) {
        context.dataStore.edit {
            it[potSettingsKey] = potSettings
        }
    }

}