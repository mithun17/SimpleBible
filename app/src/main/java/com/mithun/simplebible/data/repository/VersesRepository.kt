package com.mithun.simplebible.data.repository

import com.mithun.simplebible.data.api.BibleApi
import com.mithun.simplebible.data.dao.BookmarksDao
import com.mithun.simplebible.data.dao.NotesDao
import com.mithun.simplebible.data.dao.VersesEntityDao
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.model.Items
import com.mithun.simplebible.data.model.Type
import com.mithun.simplebible.data.model.Verse
import com.mithun.simplebible.ui.custom.TAG
import javax.inject.Inject

class VersesRepository @Inject constructor(
    val bibleApi: BibleApi,
    val versesEntityDao: VersesEntityDao,
    val bookmarksDao: BookmarksDao,
    val notesDao: NotesDao
) {

//    companion object {
//        fun getInstance(context: Context) = VersesRepository(
//            RetrofitBuilder.bibleApi,
//            SimpleBibleDB.getInstance(context).versesEntityDao(),
//            SimpleBibleDB.getInstance(context).bookmarksDao(),
//            SimpleBibleDB.getInstance(context).notesDao()
//        )
//    }

    suspend fun getVerses(bibleId: String, chapterId: String): List<Verse> {

        var verses = versesEntityDao.getVersesForChapter(bibleId, chapterId)

        if (verses.isEmpty()) {
            val chapter = bibleApi.getChapter(bibleId, chapterId).data

            val mapOfVerses = mutableMapOf<String, String>()

            chapter.content.forEach { content ->

                if (content.type == Type.TAG.value && content.name == "para") {
                    content.items.forEach { item ->
                        when (item.type) {
                            Type.TEXT.value -> {
                                val text = item.text
                                item.attrs?.verseId?.let {
                                    val value = mapOfVerses[item.attrs.verseId] ?: ""
                                    mapOfVerses[item.attrs.verseId] = value + text
                                }
                            }
                            Type.TAG.value -> {
                                item.attrs?.style?.let { style ->
                                    when (style) {
                                        Type.WJ.value -> {
                                            parseJesusItems(item, mapOfVerses)
                                        }
                                        else -> {
                                            item.items.forEach { finalItem ->
                                                if (finalItem.type == Type.TEXT.value) {
                                                    val text = finalItem.text
                                                    finalItem.attrs?.verseId?.let {
                                                        val value = mapOfVerses[finalItem.attrs.verseId] ?: ""
                                                        mapOfVerses[finalItem.attrs.verseId] = value + text
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val versesToBeInsertedToDB = mapOfVerses.map {
                val verseNumber = it.key.split(".").last()
                val verseText = it.value

                VerseEntity(
                    id = it.key,
                    chapterId = chapter.id,
                    bibleId = chapter.bibleId,
                    number = verseNumber,
                    text = verseText,
                    bookmarks = emptyList(),
                    notes = emptyList()
                )
            }.toList()

            versesEntityDao.insertVerses(versesToBeInsertedToDB)

            verses = versesEntityDao.getVersesForChapter(bibleId, chapterId)
        }

        val result = verses.map { verse ->
            Verse(verse.number.toInt(), verse.text, hasNotes = verse.notes.isNotEmpty(), isBookmarked = verse.bookmarks.isNotEmpty())
        }.toList().sortedBy { it.number }

        return result
    }

    private fun parseJesusItems(item: Items, mapOfVerses: MutableMap<String, String>) {
        item.items.forEach { finalItem ->
            if (finalItem.type == Type.TEXT.value) {
                var redText = TAG.RED.start()
                // only text type has verseId in it
                redText += finalItem.text
                redText += TAG.RED.end()
                finalItem.attrs?.verseId?.let {
                    val value = mapOfVerses[finalItem.attrs?.verseId] ?: ""
                    mapOfVerses[finalItem.attrs?.verseId] = value + redText
                }
            } else {
                parseJesusItems(finalItem, mapOfVerses)
            }
        }
    }

    suspend fun saveBookmark(verseId: String, bookmark: Bookmark): Boolean {
        val bookmarkAdded = bookmarksDao.addBookmark(bookmark)
        if (bookmarkAdded> 0) {
            versesEntityDao.addBookmarkToVerse(verseId, bookmark.bibleId, bookmarkAdded.toString())
            return true
        }
        return false
    }

    suspend fun getVerseById(bibleId: String, verseId: String): VerseEntity = versesEntityDao.getVerseById(verseId, bibleId)
}