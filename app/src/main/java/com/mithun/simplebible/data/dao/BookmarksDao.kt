package com.mithun.simplebible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mithun.simplebible.data.database.model.Bookmark

@Dao
interface BookmarksDao {

    // get bookmarks sorted by last updated being at the top
    @Query("SELECT * FROM bookmarks ORDER BY dateUpdated DESC")
    suspend fun getBookmarks(): List<Bookmark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark): Long

    @Query("DELETE FROM bookmarks WHERE id=:bookmarkId")
    suspend fun deleteBookmark(bookmarkId: Long)
}
