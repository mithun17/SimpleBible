package com.mithun.simplebible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mithun.simplebible.data.database.model.Note

@Dao
interface NotesDao {

    // get notes sorted by last updated being at the top
    @Query("SELECT * FROM notes ORDER BY dateUpdated DESC")
    suspend fun getNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: Note)

    @Query("DELETE FROM notes WHERE id=:noteId")
    suspend fun deleteNote(noteId: Long)
}
