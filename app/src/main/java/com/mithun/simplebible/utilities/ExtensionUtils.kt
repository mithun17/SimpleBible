package com.mithun.simplebible.utilities

import android.view.View
import com.mithun.simplebible.data.model.Verse
import java.lang.StringBuilder

object ExtensionUtils {

    fun Map<Int, Verse>.toCopyText(chapterName: String): String {

        val copyText = StringBuilder().apply {
            append(chapterName)
            appendLine()
        }

        forEach { entry ->
            with(copyText) {
                append("[${entry.key}] ")
                append(entry.value.text)
                appendLine()
            }
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
