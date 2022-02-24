package com.mithun.simplebible.data.database.model

import androidx.room.Entity
import com.mithun.simplebible.utilities.TABLE_VERSE

@Entity(tableName = TABLE_VERSE, primaryKeys = ["id", "bibleId"])
data class VerseEntity(
    val id: String,
    val chapterId: String,
    val bibleId: String,
    val reference: String,
    val number: String,
    val text: String,
    val notes: List<Long>,
    val bookmarks: List<Long>,
    val prevChapterId: String?,
    val nextChapterId: String?
)
