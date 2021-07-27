package com.mithun.simplebible.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mithun.simplebible.utilities.TABLE_NOTE
import java.util.Calendar

@Entity(tableName = TABLE_NOTE)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bibleId: String,
    val chapterId: String,
    val chapterName: String,
    val verses: List<Int>,
    val comment: String,
    val dateAdded: Calendar = Calendar.getInstance(),
    val dateUpdated: Calendar = Calendar.getInstance()
)
