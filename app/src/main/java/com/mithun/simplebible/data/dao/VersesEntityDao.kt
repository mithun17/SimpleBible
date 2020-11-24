package com.mithun.simplebible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mithun.simplebible.data.database.model.VerseEntity

@Dao
interface VersesEntityDao {

    @Query("SELECT * FROM verses WHERE chapterId=:chapterId AND bibleId=:bibleId ORDER BY number ASC")
    suspend fun getVersesForChapter(bibleId: String, chapterId: String): List<VerseEntity>

    @Query("SELECT * FROM verses WHERE id=:verseId LIMIT 1")
    suspend fun getVerseById(verseId: String) : VerseEntity

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)
}
