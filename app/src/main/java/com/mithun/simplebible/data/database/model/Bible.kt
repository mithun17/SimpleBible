package com.mithun.simplebible.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mithun.simplebible.utilities.TABLE_BIBLE

@Entity(tableName = TABLE_BIBLE)
data class Bible(
    @PrimaryKey
    val id: String,
    val dblId: String,
    val abbreviation: String,
    val abbreviationLocal: String?,
    val name: String?,
    val nameLocal: String?,
    val description: String?,
    val descriptionLocal: String?,
    val type: String?
)
