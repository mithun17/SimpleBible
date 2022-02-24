package com.mithun.simplebible.data.repository

import com.mithun.simplebible.data.api.BibleApi
import com.mithun.simplebible.data.dao.BookmarksDao
import com.mithun.simplebible.data.dao.NotesDao
import com.mithun.simplebible.data.dao.VersesEntityDao
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.model.Items
import com.mithun.simplebible.data.model.Name
import com.mithun.simplebible.data.model.Type
import com.mithun.simplebible.ui.custom.TAG
import javax.inject.Inject

class VersesRepository @Inject constructor(
    val bibleApi: BibleApi,
    val versesEntityDao: VersesEntityDao,
    val bookmarksDao: BookmarksDao,
    val notesDao: NotesDao
) {

    suspend fun getAllVersesForChapter(bibleId: String, chapterId: String): List<VerseEntity> {
        // fetch verses from local DB
        var verses = versesEntityDao.getVersesForChapter(bibleId, chapterId)

        // previous and next chapters to this current chapter
        var prevPage: String? = null
        var nextPage: String? = null

        // if verses from DB is empty, fetch from network
        if (verses.isEmpty()) {
            // fetch chapter (aka) fetch verses for a chapter from network
            val chapter = bibleApi.getChapter(bibleId, chapterId).data
            // key - verseId(JHN.3.1). value - verse text
            val mapOfVerses = mutableMapOf<String, String>()
            // parse the text from json response. Since the json is not structured conveniently, we need to hand parse at different levels in the json nesting.
            chapter.content.forEach { content ->
                if (content.type == Type.TAG.value && content.name == Name.PARA.value) {
                    content.items.forEach { item ->
                        when (item.type) {
                            Type.TEXT.value -> {
                                parseRegularItem(item, mapOfVerses)
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
                                                    parseRegularItem(finalItem, mapOfVerses)
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

            if (chapter.previous != null) {
                // if previous chapter is intro, skip it and fetch the chapter before it
                prevPage = if (chapter.previous.id.split(".").last() == "intro") {
                    val prevChapter = bibleApi.getChapter(bibleId, chapter.previous.id).data
                    prevChapter.previous?.id
                } else {
                    chapter.previous.id
                }
            }

            if (chapter.next != null) {
                // if next chapter is intro, skip it and fetch the chapter after it
                nextPage = if (chapter.next.id.split(".").last() == "intro") {
                    val nextChapter = bibleApi.getChapter(bibleId, chapter.next.id).data
                    nextChapter.next?.id
                } else {
                    chapter.next.id
                }
            }

            val versesToBeInsertedToDB = mapOfVerses.map {
                val verseNumber = it.key.split(".").last()
                val verseText = it.value
                // Form a VerseEntity object that can be stored in the database
                VerseEntity(
                    id = it.key,
                    chapterId = chapter.id,
                    bibleId = chapter.bibleId,
                    reference = chapter.reference,
                    number = verseNumber,
                    text = verseText,
                    bookmarks = emptyList(),
                    notes = emptyList(),
                    prevChapterId = prevPage,
                    nextChapterId = nextPage
                )
            }.toList()
            versesEntityDao.insertVerses(versesToBeInsertedToDB)
            verses = versesEntityDao.getVersesForChapter(bibleId, chapterId)
        }
        return verses
    }

    private fun parseRegularItem(item: Items, mapOfVerses: MutableMap<String, String>) {
        val text = item.text
        item.attrs?.verseId?.let {
            val value = mapOfVerses[item.attrs.verseId] ?: ""
            mapOfVerses[item.attrs.verseId] = value + text
        }
    }

    /**
     * parse text which should be displayed in red font color
     *
     * Adds the verse to the passed map. eg: "...and saith unto him, <red>Follow me</red>"
     */
    private fun parseJesusItems(item: Items, mapOfVerses: MutableMap<String, String>) {
        item.items.forEach { finalItem ->
            if (finalItem.type == Type.TEXT.value) {
                val redText = StringBuilder()
                redText.append(TAG.RED.start())
                // only text type has verseId in it
                redText.append(finalItem.text)
                redText.append(TAG.RED.end())
                finalItem.attrs?.verseId?.let {
                    val value = mapOfVerses[finalItem.attrs?.verseId] ?: ""
                    mapOfVerses[finalItem.attrs?.verseId] = value + redText.toString()
                }
            } else {
                parseJesusItems(finalItem, mapOfVerses)
            }
        }
    }

    suspend fun saveBookmark(verseId: String, bookmark: Bookmark): Boolean {
        val bookmarkAdded = bookmarksDao.addBookmark(bookmark)
        if (bookmarkAdded > 0) {
            versesEntityDao.addBookmarkToVerse(verseId, bookmark.bibleId, bookmarkAdded.toString())
            return true
        }
        return false
    }

    suspend fun getVerseById(bibleId: String, verseId: String): VerseEntity =
        versesEntityDao.getVerseById(verseId, bibleId)

    suspend fun getVersesById(bibleId: String, verseIds: List<String>): List<VerseEntity> =
        versesEntityDao.getVersesById(verseIds, bibleId)
}
