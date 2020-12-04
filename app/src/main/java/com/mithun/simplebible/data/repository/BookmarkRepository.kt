package com.mithun.simplebible.data.repository

import android.content.Context
import com.mithun.simplebible.data.dao.BookmarksDao
import com.mithun.simplebible.data.database.SimpleBibleDB
import com.mithun.simplebible.data.database.model.Bookmark

class BookmarkRepository constructor(
    private val bookmarksDao: BookmarksDao
) {

    companion object {
        fun getInstance(context: Context) = BookmarkRepository(
            SimpleBibleDB.getInstance(context).bookmarksDao()
        )
    }

    suspend fun getAllBookmarks(): List<Bookmark> {
        return bookmarksDao.getBookmarks()
    }
}
