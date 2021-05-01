package com.mithun.simplebible.data.repository

import com.mithun.simplebible.data.dao.BookmarksDao
import com.mithun.simplebible.data.dao.VersesEntityDao
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.ui.adapter.BookmarkItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkRepository @Inject constructor(
    private val bookmarksDao: BookmarksDao,
    private val versesEntityDao: VersesEntityDao
) {

    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarksDao.getBookmarks()
    }

    suspend fun deleteBookmark(bookmark: BookmarkItem): Boolean {
        // delete bookmark
        val deletedBookmarks = bookmarksDao.deleteBookmark(bookmark.id)
        if (deletedBookmarks > 0) {
            // remove bookmark from the verse
            versesEntityDao.removeBookmarkFromVerse(bookmark.verseId, bookmark.bibleId, bookmark.id.toString())
            return true
        }
        return false
    }
}
