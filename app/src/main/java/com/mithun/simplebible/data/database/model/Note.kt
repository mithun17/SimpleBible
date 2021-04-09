package com.mithun.simplebible.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mithun.simplebible.utilities.TABLE_NOTE

@Entity(
    tableName = TABLE_NOTE
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val verses: List<Long>,
    val comment: String,
    val dateAdded: Long,
    val dateUpdated: Long
)
