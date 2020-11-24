package com.mithun.simplebible.data.repository

import android.content.Context
import com.mithun.simplebible.data.api.BibleApi
import com.mithun.simplebible.data.api.RetrofitBuilder
import com.mithun.simplebible.data.dao.BooksDao
import com.mithun.simplebible.data.database.SimpleBibleDB
import com.mithun.simplebible.data.model.Book
import com.mithun.simplebible.data.model.Items
import com.mithun.simplebible.data.model.Type
import com.mithun.simplebible.data.model.Verse
import com.mithun.simplebible.ui.custom.TAG

class BibleRepository constructor(
    val bibleApi: BibleApi,
    val booksDao: BooksDao
) {

    companion object {
        fun getInstance(context: Context) = BibleRepository(
            RetrofitBuilder.bibleApi,
            SimpleBibleDB.getInstance(context).booksDao()
        )
    }

    suspend fun getBibles() = RetrofitBuilder.bibleApi.getBibles().data

    suspend fun getBooks(bibleId: String) : List<Book> {

        // first fetch from database. if it exists, then return that.
        var booksDb = booksDao.getBooks()

        // if not, make a network call and store it in the database and then return the database
        if (booksDb.isEmpty()) {
            booksDao.insertBooks(bibleApi.getBooks(bibleId).data)
            booksDb = booksDao.getBooks()
        }

        return booksDb
    }

    suspend fun getChapterJson(bibleId: String, chapterId: String) = RetrofitBuilder.bibleApi.getChapterJson(bibleId, chapterId)

//    suspend fun getChapter(bibleId: String, chapterId: String) : Pair<String, List<Verse>> {
//
//        val chapter = RetrofitBuilder.bibleApi.getChapter(bibleId, chapterId).data
//
//        val mapOfVerses = mutableMapOf<String, String>()
//
//        chapter.content.forEach { content->
//
//            if (content.type== Type.TAG.value && content.name=="para") {
//                content.items.forEach { item->
//                    when(item.type) {
//                        Type.TEXT.value-> {
//                            val text = item.text
//                            val value = mapOfVerses[item.attrs.verseId] ?: ""
//                            mapOfVerses[item.attrs.verseId] = value+text
//                        }
//                        Type.TAG.value-> {
//                            item.attrs.style?.let {style->
//                                when(style) {
//                                    Type.WJ.value -> {
//                                        parseJesusItems(item, mapOfVerses)
//                                    }
//                                    Type.ADD.value -> {
//                                        item.items.forEach {finalItem->
//                                            if (finalItem.type==Type.TEXT.value) {
//                                                val text = finalItem.text
//                                                val value = mapOfVerses[finalItem.attrs.verseId] ?: ""
//                                                mapOfVerses[finalItem.attrs.verseId] = value+text
//                                            }
//                                        }
//
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
//
//        val verses = mapOfVerses.map {
//            val verseNumber = it.key.split(".").last()
//            Verse(verseNumber.toInt(), it.value)
//        }.toList()
//
//        return Pair(chapter.reference, verses)
//    }
//
//    private fun parseJesusItems(item: Items, mapOfVerses: MutableMap<String, String>) {
//        item.items.forEach {finalItem->
//            if (finalItem.type==Type.TEXT.value) {
//                var redText = TAG.RED.start()
//                // only text type has verseId in it
//                redText+=finalItem.text
//                redText+= TAG.RED.end()
//                val value = mapOfVerses[finalItem.attrs.verseId] ?: ""
//                mapOfVerses[finalItem.attrs.verseId] = value+redText
//            }
//            else {
//                parseJesusItems(finalItem, mapOfVerses)
//            }
//        }
//    }
}
