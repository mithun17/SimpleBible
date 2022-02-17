package com.mithun.simplebible.data.repository

import android.util.Log
import com.mithun.simplebible.data.api.BibleApi
import com.mithun.simplebible.data.dao.BibleDao
import com.mithun.simplebible.data.dao.BooksDao
import com.mithun.simplebible.data.database.model.Bible
import com.mithun.simplebible.data.database.model.Book
import com.mithun.simplebible.utilities.ASV_BIBLE_ID
import com.mithun.simplebible.utilities.KJV_BIBLE_ID
import com.mithun.simplebible.utilities.TAMIL_BIBLE_ID
import javax.inject.Inject

class BibleRepository @Inject constructor(
    private val bibleApi: BibleApi,
    private val booksDao: BooksDao,
    private val bibleDao: BibleDao
) {

    suspend fun getBibles(): List<Bible> {
        var bibles = bibleDao.getBibles()
        if (bibles.isEmpty()) {
            val bibleResponse = bibleApi.getBibles().data
            try {
                val insertedIds = bibleDao.insertBibles(bibleResponse)
            } catch (ex: Exception) {
                Log.e("DB error", ex.message!!)
            }

            bibles = bibleDao.getBibles()
        }
        return bibles
    }

    suspend fun getBibleById(id: String): Bible? {
        var bible = bibleDao.getBibleById(id)
        if (bible == null) {
            bibleDao.insertBibles(bibleApi.getBibles().data)
            bible = bibleDao.getBibleById(id)
        }
        return bible
    }

    suspend fun getPresetBibles(): List<Bible> {
        val presets = listOf(KJV_BIBLE_ID, ASV_BIBLE_ID, TAMIL_BIBLE_ID)
        val bibles = mutableListOf<Bible>()
        presets.forEach { id ->
            getBibleById(id)?.let { bible ->
                bibles.add(bible)
            }
        }
        return bibles
    }

    suspend fun getBooks(bibleId: String): List<Book> {

        // first fetch from database. if it exists, then return that.
        var booksDb = booksDao.getBooks(bibleId)

        // if not, make a network call and store it in the database and then return the database
        if (booksDb.isEmpty()) {
            booksDao.insertBooks(bibleApi.getBooks(bibleId).data)
            booksDb = booksDao.getBooks(bibleId)
        }

        return booksDb
    }
}
