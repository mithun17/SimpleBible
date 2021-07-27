package com.mithun.simplebible.utilities

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.mithun.simplebible.R
import javax.inject.Inject

class Prefs @Inject constructor(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(
            "${context.packageName}_preferences",
            MODE_PRIVATE
        )
    }

    private fun <T : Any> get(key: String, default: T): T {
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

    private fun <T : Any> set(key: String, value: T) {
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

    /**
     * last stored bible version id
     */
    var selectedBibleVersionId: String
        get() = get(PREF_SELECTED_BIBLE_VERSION_ID, DEFAULT_BIBLE_ID)
        set(value) = set(PREF_SELECTED_BIBLE_VERSION_ID, value)

    /**
     * last stored bible version name
     */
    var selectedBibleVersionName: String
        get() = get(PREF_SELECTED_BIBLE_VERSION_NAME, DEFAULT_BIBLE_VERSION)
        set(value) = set(PREF_SELECTED_BIBLE_VERSION_NAME, value)

    /**
     * store last read chapter
     */
    var lastReadChapter: String
        get() = get(PREF_LAST_READ_CHAPTER_ID, kLastReadChapterDefaultId)
        set(value) = set(PREF_LAST_READ_CHAPTER_ID, value)

    /**
     * store night mode setting
     */
    var isNightMode: Boolean
        get() = get(context.getString(R.string.preference_theme_key), false)
        set(value) = set(context.getString(R.string.preference_theme_key), value)

    companion object {
        private const val PREF_SELECTED_BIBLE_VERSION_ID = "PrefSelectedBibleVersionId"
        private const val PREF_SELECTED_BIBLE_VERSION_NAME = "PrefSelectedBibleVersionName"
        private const val PREF_LAST_READ_CHAPTER_ID = "PrefLastReadChapterId"
        private const val kLastReadChapterDefaultId = "JHN.3"
    }
}
