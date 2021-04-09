package com.mithun.simplebible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mithun.simplebible.data.database.model.FullChapter

@Dao
interface ChaptersDao {

    @Query("SELECT * FROM chapters WHERE id=:id LIMIT 1")
    suspend fun getChapterById(id: String): FullChapter

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapterById(fullChapter: FullChapter)
}
