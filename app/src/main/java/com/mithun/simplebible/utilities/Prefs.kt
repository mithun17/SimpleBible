package com.mithun.simplebible.utilities

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import javax.inject.Inject

class Prefs @Inject constructor(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(
            "${context.packageName}_prefs",
            MODE_PRIVATE
        )
    }

    fun <T : Any> get(key: String, default: T): T {
        try {
            val type = default::class.java
            return when (default) {
                is String -> type.cast(sharedPreferences.getString(key, default))
                is Long -> type.cast(sharedPreferences.getLong(key, default))
                is Int -> type.cast(sharedPreferences.getInt(key, default))
                is Float -> type.cast(sharedPreferences.getFloat(key, default))
                is Boolean -> type.cast(sharedPreferences.getBoolean(key, default))
                else -> default
            } as T
        } catch (ex: IllegalStateException) {
            return default
        }
    }

    fun <T : Any> set(key: String, value: T) {
        if (key.isNotEmpty()) {
            val editor = sharedPreferences.edit()
            when (value) {
                is String -> editor.putString(key, value)
                is Long -> editor.putLong(key, value)
                is Int -> editor.putInt(key, value)
                is Float -> editor.putFloat(key, value)
                is Boolean -> editor.putBoolean(key, value)
            }
            editor.apply()
        }
    }

    var selectedBibleId: String
        get() = get(PREF_SELECTED_BIBLE_ID, KJV_BIBLE_ID)
        set(value) = set(PREF_SELECTED_BIBLE_ID, value)

    companion object {
        private const val PREF_SELECTED_BIBLE_ID = "PrefSelectedBibleId"
    }
}
