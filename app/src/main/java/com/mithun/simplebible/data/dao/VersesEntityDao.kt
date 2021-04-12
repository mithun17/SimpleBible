package com.mithun.simplebible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.utilities.Converters

@Dao
interface VersesEntityDao {

    @Query("SELECT * FROM verses WHERE chapterId=:chapterId AND bibleId=:bibleId ORDER BY number ASC")
    suspend fun getVersesForChapter(bibleId: String, chapterId: String): List<VerseEntity>

    @Query("SELECT * FROM verses WHERE id=:verseId AND bibleId=:bibleId LIMIT 1")
    suspend fun getVerseById(verseId: String, bibleId: String): VerseEntity

    @Query("SELECT * FROM verses WHERE id IN (:verseIds) AND bibleId=:bibleId")
    suspend fun getVersesById(verseIds: List<String>, bibleId: String): List<VerseEntity>

    // TODO update query to not update the notes list
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    @Transaction
    suspend fun addNoteToVerse(verseId: String, bibleId: String, noteId: String) {
        val notes = Converters().toListOfStrings(getNotesFromVerse(verseId, bibleId)).toMutableList()
        // check if the noteId exists.
        // if it does, then don't add noteId to the list
        if (!notes.contains(noteId)) {
            // add element to list
            notes.add(noteId)
            // update verse with updated list
            updateVerseWithNotesList(verseId, bibleId, notes.toString())
        }
    }

    @Transaction
    suspend fun removeNoteFromVerse(verseId: String, bibleId: String, noteId: String) {
        val notes = Converters().toListOfStrings(getNotesFromVerse(verseId, bibleId)).toMutableList()
        // check if the noteId exists.
        // if it does, then remove noteId from the list and update the list.
        if (notes.contains(noteId)) {
            // remove element from list
            notes.remove(noteId)
            // update verse with updated list
            updateVerseWithNotesList(verseId, bibleId, notes.toString())
        }
    }

    @Query("SELECT notes FROM verses WHERE id=:verseId AND bibleId=:bibleId")
    suspend fun getNotesFromVerse(verseId: String, bibleId: String): String

    @Query("UPDATE verses SET notes=:listOfNoteIds WHERE id=:verseId AND bibleId=:bibleId")
    suspend fun updateVerseWithNotesList(verseId: String, bibleId: String, listOfNoteIds: String)

    @Transaction
    suspend fun addBookmarkToVerse(verseId: String, bibleId: String, bookmarkId: String) {
        val bookmarks = Converters().toListOfStrings(getBookmarksFromVerse(verseId, bibleId)).toMutableList()
        // check if the bookmarkId exists.
        // if it does, then don't add bookmarkId to the list
        if (!bookmarks.contains(bookmarkId)) {
            // remove element from list
            bookmarks.add(bookmarkId)
            // update verse with updated list
            updateVerseWithBookmarksList(verseId, bibleId, bookmarks.toString())
        }
    }

    @Transaction
    suspend fun removeBookmarkFromVerse(verseId: String, bibleId: String, bookmarkId: String) {
        val bookmarks = Converters().toListOfStrings(getBookmarksFromVerse(verseId, bibleId)).toMutableList()
        // check if the bookmarkId exists.
        // if it does, then remove bookmarkId from the list and update the list.
        if (bookmarks.contains(bookmarkId)) {
            // remove element from list
            bookmarks.remove(bookmarkId)
            // update verse with updated list
            updateVerseWithBookmarksList(verseId, bibleId, bookmarks.toString())
        }
    }

    @Query("SELECT bookmarks FROM verses WHERE id=:verseId AND bibleId=:bibleId")
    fun getBookmarksFromVerse(verseId: String, bibleId: String): String

    @Query("UPDATE verses SET bookmarks=:listOfBookmarkIds WHERE id=:verseId AND bibleId=:bibleId")
    suspend fun updateVerseWithBookmarksList(verseId: String, bibleId: String, listOfBookmarkIds: String)
}
