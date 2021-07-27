package com.mithun.simplebible.data.database.model

import androidx.room.Entity
import com.mithun.simplebible.data.model.SmallChapter
import com.mithun.simplebible.utilities.TABLE_BOOK

@Entity(tableName = TABLE_BOOK, primaryKeys = ["id", "bibleId"])
data class Book(
    val id: String,
    val bibleId: String,
    val abbreviation: String,
    val name: String,
    val nameLong: String,
    val chapters: List<SmallChapter>
)
