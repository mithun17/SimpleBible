package com.mithun.simplebible.data.repository.data

import com.mithun.simplebible.data.database.model.VerseEntity
import java.util.Calendar

data class FullNote(
    val id: Long = 0,
    val bibleId: String,
    val chapterId: String,
    val chapterName: String,
    val verseIds: List<Int>,
    val verses: List<VerseEntity>,
    val comment: String,
    val dateAdded: Calendar = Calendar.getInstance(),
    val dateUpdated: Calendar = Calendar.getInstance()
)
