package com.mithun.simplebible.data.repository

import com.mithun.simplebible.data.api.BibleApi
import com.mithun.simplebible.data.dao.NotesDao
import com.mithun.simplebible.data.dao.VersesEntityDao
import com.mithun.simplebible.data.database.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepository @Inject constructor(
    val bibleApi: BibleApi,
    val notesDao: NotesDao,
    val versesEntityDao: VersesEntityDao
) {

    suspend fun saveNote(note: Note) = notesDao.addNote(note)

    fun getNotes(): Flow<List<Note>> = notesDao.getNotes()

    suspend fun deleteNote(noteId: Long) {
        notesDao.deleteNote(noteId)
    }
}
