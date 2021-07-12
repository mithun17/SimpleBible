package com.mithun.simplebible.utilities

import android.view.View
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.repository.data.FullNote
import com.mithun.simplebible.ui.adapter.BookmarkItem

object ExtensionUtils {

    const val RED_TAG_START = "<red>"
    const val RED_TAG_END = "</red>"

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

    fun BookmarkItem.toCopyText(resourceUtil: ResourcesUtil): String {
        val verse = mapOf(verseId.split(".").last().toInt() to verse)
        val copyText = StringBuilder().apply {
            append(verse.toCopyText(this@toCopyText.chapterId))
        }
        return copyText.toString()
    }

    fun VerseEntity.toShareText(): String {
        val shareText = StringBuilder()
        shareText.append(text.toRegularText())
        shareText.appendLine()
        shareText.append("-$reference:$number")
        return shareText.toString()
    }

    fun String.toRegularText() = replace(RED_TAG_START, "").replace(RED_TAG_END, "")
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
