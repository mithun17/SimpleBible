package com.mithun.simplebible.ui.custom

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import com.mithun.simplebible.R


class VerseTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    private var verseNumber: Int = 0
    private var verseText: String = ""

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VerseView,
            0, 0).apply {

            try {
                verseNumber = getInteger(R.styleable.VerseView_verseNumber, 0)
                verseText = getString(R.styleable.VerseView_verseText) ?: ""
                setVerse(verseNumber, verseText)
            } finally {
                recycle()
            }
        }
    }

    fun setVerse(number: Int, verse: String) {
        verseNumber = number
        verseText = verse
        val verseNumberText = "${TAG.NUMBER.start()}[$number]${TAG.NUMBER.end()}"
        val fullVerse = "$verseNumberText $verseText"

        setText(fullVerse.toSpannedStyle(context),BufferType.SPANNABLE)
    }

}

private fun String.toSpannedStyle(context: Context): SpannableStringBuilder {
    var temp = this
    val spannableStringBuilder = SpannableStringBuilder()

    // Verse number style
    val startIndexOfNumberTag = temp.indexOf(TAG.NUMBER.start())
    temp = temp.replaceFirst(TAG.NUMBER.start(),"")
    val endIndexOfNumberTag = temp.indexOf(TAG.NUMBER.end())
    temp = temp.replaceFirst(TAG.NUMBER.end(),"")
    spannableStringBuilder.append(temp.substring(startIndexOfNumberTag, endIndexOfNumberTag))
    spannableStringBuilder.setSpan(TextAppearanceSpan(context, R.style.Overline), startIndexOfNumberTag, endIndexOfNumberTag, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    while (temp.contains(TAG.RED.start())) {
        val startIndexOfRedTag = temp.indexOf(TAG.RED.start())

        if (startIndexOfRedTag!=0) {

            val startIndexOfNormalText = spannableStringBuilder.length
            spannableStringBuilder.append(temp.substring(spannableStringBuilder.length, startIndexOfRedTag))
            // Normal text
            spannableStringBuilder.setSpan(TextAppearanceSpan(context, R.style.Body1), startIndexOfNormalText, startIndexOfRedTag, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // indices of red tag
        temp = temp.replaceFirst(TAG.RED.start(), "")
        val endIndexOfRedTag = temp.indexOf(TAG.RED.end())
        temp = temp.replaceFirst(TAG.RED.end(), "")

        spannableStringBuilder.append(temp.substring(startIndexOfRedTag, endIndexOfRedTag))

        // Red Text
        spannableStringBuilder.setSpan(TextAppearanceSpan(context, R.style.Body1_GodText), startIndexOfRedTag, endIndexOfRedTag, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    if (temp.length>spannableStringBuilder.length) {
        val startIndexOfRemaingText = spannableStringBuilder.length
        spannableStringBuilder.append(temp.substring(startIndexOfRemaingText, temp.lastIndex))
        spannableStringBuilder.setSpan(TextAppearanceSpan(context, R.style.Body1), startIndexOfRemaingText, temp.lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return spannableStringBuilder
}

enum class TAG {
    NUMBER{
        override fun start() = "<number>"
        override fun end() = "</number>"
    },
    RED{
        override fun start() = "<red>"
        override fun end() = "</red>"
    };

    abstract fun start(): String
    abstract fun end(): String
}

