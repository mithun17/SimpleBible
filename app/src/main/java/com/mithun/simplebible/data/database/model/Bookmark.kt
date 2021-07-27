package com.mithun.simplebible.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mithun.simplebible.utilities.TABLE_BOOKMARK
import java.util.Calendar

@Entity(tableName = TABLE_BOOKMARK)
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bibleId: String,
    val chapterId: String,
    val verseId: String,
    val dateAdded: Calendar = Calendar.getInstance(),
    val dateUpdated: Calendar = Calendar.getInstance()
)
