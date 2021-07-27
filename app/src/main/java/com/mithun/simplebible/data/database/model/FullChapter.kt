package com.mithun.simplebible.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mithun.simplebible.utilities.TABLE_CHAPTER

@Entity(tableName = TABLE_CHAPTER)
class FullChapter(
    @PrimaryKey
    val id: String,
    val bibleId: String,
    val bookId: String,
    val number: String,
    val reference: String,
    val verses: List<String>
)
