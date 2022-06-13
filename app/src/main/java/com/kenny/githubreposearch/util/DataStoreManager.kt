package com.kenny.githubreposearch.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(Constant.DATA_STORE_FILE_NAME)

/**
 * Data store manager save custom UI setting and also for future token
 *
 * @constructor
 *
 * @param context
 */
class DataStoreManager @Inject constructor(context: Context) {

    private val settingsDataStore = context.dataStore

    /**
     * get setting value that does auto search enable, with default is true
     *
     * @return isAutoSearchEnable
     */
    suspend fun readAutoSearchEnable(): Boolean {
        return settingsDataStore.data.first().let {
            it[booleanPreferencesKey(Constant.IS_AUTO_SEARCH_ENABLE)]
        } ?: true
    }

    /**
     * write setting value change does auto search enable
     *
     * @param value
     */
    suspend fun writeAutoSearchEnable(value: Boolean) {
        settingsDataStore.edit { it[booleanPreferencesKey(Constant.IS_AUTO_SEARCH_ENABLE)] = value }
    }
}