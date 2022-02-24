package com.mithun.simplebible.utilities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mithun.simplebible.data.model.SmallChapter
import java.util.Calendar

class Converters {
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }

    @TypeConverter
    fun smallChaptersToString(smallChapter: List<SmallChapter>): String {
        return Gson().toJson(smallChapter)
    }

    @TypeConverter
    fun stringToSmallChapters(smallChaptersString: String): List<SmallChapter> {
        return Gson().fromJson(
            smallChaptersString,
            object :
                TypeToken<List<SmallChapter>>() {}.type
        )
    }

    @TypeConverter
    fun listOfStringsToJson(listOfStrings: List<String>): String {
        return Gson().toJson(listOfStrings)
    }

    @TypeConverter
    fun toListOfStrings(jsonString: String): List<String> {
        return Gson().fromJson<List<String>>(jsonString, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun LongToString(listOfLongs: List<Long>): String {
        return Gson().toJson(listOfLongs)
    }

    @TypeConverter
    fun stringToListOfLong(jsonString: String): List<Long> {
        return Gson().fromJson(jsonString, object : TypeToken<List<Long>>() {}.type)
    }

    @TypeConverter
    fun listOfIntsToJson(listOfLongs: List<Int>): String {
        return Gson().toJson(listOfLongs)
    }

    @TypeConverter
    fun stringToListOfInts(jsonString: String): List<Int> {
        return Gson().fromJson(jsonString, object : TypeToken<List<Int>>() {}.type)
    }
}
