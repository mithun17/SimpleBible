package com.mithun.simplebible.utilities

import android.view.View
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.data.FullNote

object ExtensionUtils {

    fun Map<Int, String>.toCopyText(chapterName: String): String {
        val copyText = StringBuilder().apply {
            append(chapterName)
            appendLine()
        }

        forEach { entry ->
            with(copyText) {
                append("[${entry.key}] ")
                append(entry.value)
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
