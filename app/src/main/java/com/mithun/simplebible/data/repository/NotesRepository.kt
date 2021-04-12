package com.mithun.simplebible.data.repository

import com.mithun.simplebible.data.api.BibleApi
import com.mithun.simplebible.data.dao.NotesDao
import com.mithun.simplebible.data.dao.VersesEntityDao
import com.mithun.simplebible.data.database.model.Note
import com.mithun.simplebible.data.repository.data.FullNote
import javax.inject.Inject

class NotesRepository @Inject constructor(
    val bibleApi: BibleApi,
    val notesDao: NotesDao,
    val versesEntityDao: VersesEntityDao
) {

    suspend fun saveNote(note: Note) = notesDao.addNote(note)

    suspend fun getNotes(bibleId: String): List<FullNote> {
        val notes = notesDao.getNotes()

        val fullNotes = mutableListOf<FullNote>()
        notes.forEach { note ->
            val verseIds = note.verses.map { it -> "${note.chapterId}.$it" }.toList()
            val verses = versesEntityDao.getVersesById(verseIds, bibleId)
            val fullNote = FullNote(
                id = note.id,
                bibleId = note.bibleId,
                chapterId = note.chapterId,
                verseIds = note.verses,
                verses = verses,
                comment = note.comment,
                dateAdded = note.dateAdded,
                dateUpdated = note.dateUpdated
            )
            fullNotes.add(fullNote)
        }

        return fullNotes
    }
}
