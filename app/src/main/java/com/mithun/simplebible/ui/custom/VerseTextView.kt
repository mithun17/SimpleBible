package com.mithun.simplebible.ui.custom

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.mithun.simplebible.R
import com.mithun.simplebible.utilities.ExtensionUtils
import com.mithun.simplebible.utilities.VerseFormatter.toSpannedStyle

/**
 * Compound view to represent the verses
 */
class VerseTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    private var verseNumber: Int = 0
    private var verseText: String = ""

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VerseView,
            0, 0
        ).apply {

            try {
                verseNumber = getInteger(R.styleable.VerseView_verseNumber, 0)
                verseText = getString(R.styleable.VerseView_verseText) ?: ""
                setVerse(verseNumber, verseText)
            } finally {
                recycle()
            }
        }
    }

    fun setVerse(number: Int, verse: String, isBookmarked: Boolean = false, hasNotes: Boolean = false) {
        verseNumber = number
        verseText = verse
        val verseNumberText = "${TAG.NUMBER.start()}[$number]${TAG.NUMBER.end()}"
        val fullVerse = "$verseNumberText $verseText"
        // set the stylized string as text in this Text View
        setText(fullVerse.toSpannedStyle(context), BufferType.SPANNABLE)

        // indicate with a bookmark icon if verse was bookmarked
        if (isBookmarked) {
            setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_bookmark), null, null, null)
        }
    }

    // verse selection. show an underlined verse
    fun selectVerse() {
        paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    // reset the style. clear underline
    fun unselectVerse() {
        paintFlags = 0
    }
}

/**
 * To represent the number and red text in a verse string
 */
enum class TAG {
    NUMBER {
        override fun start() = ExtensionUtils.NUMBER_TAG_START
        override fun end() = ExtensionUtils.NUMBER_TAG_END
    },
    RED {
        override fun start() = ExtensionUtils.RED_TAG_START
        override fun end() = ExtensionUtils.RED_TAG_END
    };

    abstract fun start(): String
    abstract fun end(): String
}
