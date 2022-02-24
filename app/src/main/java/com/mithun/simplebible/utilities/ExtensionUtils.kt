package com.mithun.simplebible.utilities

import android.view.View
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.repository.data.FullNote
import com.mithun.simplebible.ui.adapter.BookmarkItem
import com.mithun.simplebible.ui.model.Verse

object ExtensionUtils {

    const val RED_TAG_START = "<red>"
    const val RED_TAG_END = "</red>"

    const val NUMBER_TAG_START = "<number>"
    const val NUMBER_TAG_END = "</number>"

    /**
     * Convert multiple verses to a shareable text.
     *
     * ex: "For God so loved the world,
     *      that he gave his only begotten Son,
     *      that whosoever believeth in him should
     *      not perish, but have everlasting life.
     *                          - John 3:16"
     */
    fun Map<Int, String>.toCopyText(chapterName: String): String {
        val copyText = StringBuilder().apply {
            append(chapterName)
            appendLine()
        }
        forEach { entry ->
            with(copyText) {
                append("[${entry.key}] ")
                append(entry.value.replace(RED_TAG_START, "").replace(RED_TAG_END, ""))
                appendLine()
            }
        }
        return copyText.toString()
    }

    /**
     * Convert a Note along with its comments to a shareable text.
     *
     * ex: "John 3
     *      [16] For God so loved the world,
     *      that he gave his only begotten Son,
     *      that whosoever believeth in him should
     *      not perish, but have everlasting life.
     *
     *      Note
     *      Notes from John"
     */
    fun FullNote.toCopyText(resourceUtil: ResourcesUtil): String {
        val verses = this.verses.associateBy({ it.id.split(".").last().toInt() }, { it.text })
        val copyText = StringBuilder().apply {
            append(verses.toCopyText(this@toCopyText.chapterName))
        }
        with(copyText) {
            appendLine()
            append(resourceUtil.getString(R.string.moreOptionsNoteText))
            appendLine()
            append(this@toCopyText.comment)
            appendLine()
        }
        return copyText.toString()
    }

    /**
     * Convert a Bookmark to a shareable text.
     *
     * ex: "John 3
     *      [16] For God so loved the world,
     *      that he gave his only begotten Son,
     *      that whosoever believeth in him should
     *      not perish, but have everlasting life."
     */
    fun BookmarkItem.toCopyText(resourceUtil: ResourcesUtil): String {
        val verse = mapOf(verseId.split(".").last().toInt() to verse)
        val copyText = StringBuilder().apply {
            append(verse.toCopyText(this@toCopyText.chapterId))
        }
        return copyText.toString()
    }

    /**
     * Convert to a shareable text.
     *
     * ex: "For God so loved the world,
     *      that he gave his only begotten Son,
     *      that whosoever believeth in him should
     *      not perish, but have everlasting life.
     *                          - John 3:16"
     */
    fun VerseEntity.toShareText(): String {
        val shareText = StringBuilder()
        shareText.append(text.toRegularText())
        shareText.appendLine()
        shareText.append("-$reference:$number")
        return shareText.toString()
    }

    /**
     * Convert verse string to normal string.
     */
    fun String.toRegularText() = replace(RED_TAG_START, "").replace(RED_TAG_END, "")

    /**
     * Convert list of VerseEntity to list of Verse
     */
    fun List<VerseEntity>.toVerses(): List<Verse> {
        return map { verse ->
            Verse(verse.number.toInt(), verse.reference, verse.text, hasNotes = verse.notes.isNotEmpty(), isBookmarked = verse.bookmarks.isNotEmpty())
        }.toList().sortedBy { it.number }
    }
}

/**
 * hide view
 */
val View.gone: Unit
    get() {
        visibility = View.GONE
    }

/**
 * show view
 */
val View.visible: Unit
    get() {
        visibility = View.VISIBLE
    }
