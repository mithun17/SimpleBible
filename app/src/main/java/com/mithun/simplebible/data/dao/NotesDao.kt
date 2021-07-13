package com.mithun.simplebible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mithun.simplebible.data.database.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    // get notes sorted by last updated being at the top
    @Query("SELECT * FROM notes ORDER BY dateUpdated DESC")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id=:noteId LIMIT 1")
    fun getNoteById(noteId: Long): Flow<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: Note): Long

    @Query("DELETE FROM notes WHERE id=:noteId")
    suspend fun deleteNote(noteId: Long)
}
