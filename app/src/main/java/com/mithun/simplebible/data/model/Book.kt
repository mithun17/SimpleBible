package com.mithun.simplebible.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mithun.simplebible.data.database.model.SmallChapter
import com.mithun.simplebible.utilities.TABLE_BOOK

@Entity(
    tableName = TABLE_BOOK
)
data class Book(
    @PrimaryKey
    val id: String,
    val bibleId: String,
    val abbreviation: String,
    val name: String,
    val nameLong: String,
    val chapters: List<SmallChapter>
)
