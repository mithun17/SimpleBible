package com.mithun.simplebible.utilities

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import com.mithun.simplebible.R
import com.mithun.simplebible.ui.custom.TAG

object VerseFormatter {

    fun formatVerseForDisplay(
        context: Context,
        number: Int,
        verse: String
    ): SpannableStringBuilder {
        val verseNumberText = "${TAG.NUMBER.start()}[$number]${TAG.NUMBER.end()}"
        val fullVerse = "$verseNumberText $verse"
        return fullVerse.toSpannedStyle(context)
    }

    fun String.toSpannedStyle(context: Context): SpannableStringBuilder {
        var temp = this
        val spannableStringBuilder = SpannableStringBuilder()

        // Verse number style
        val startIndexOfNumberTag = temp.indexOf(TAG.NUMBER.start())
        temp = temp.replaceFirst(TAG.NUMBER.start(), "")
        val endIndexOfNumberTag = temp.indexOf(TAG.NUMBER.end())
        temp = temp.replaceFirst(TAG.NUMBER.end(), "")
        spannableStringBuilder.append(temp.substring(startIndexOfNumberTag, endIndexOfNumberTag))
        spannableStringBuilder.setSpan(
            TextAppearanceSpan(context, R.style.Overline),
            startIndexOfNumberTag,
            endIndexOfNumberTag,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        while (temp.contains(TAG.RED.start())) {
            val startIndexOfRedTag = temp.indexOf(TAG.RED.start())

            if (startIndexOfRedTag != 0) {

                val startIndexOfNormalText = spannableStringBuilder.length
                spannableStringBuilder.append(
                    temp.substring(
                        spannableStringBuilder.length,
                        startIndexOfRedTag
                    )
                )
                // Normal text
                spannableStringBuilder.setSpan(
                    TextAppearanceSpan(context, R.style.Body1),
                    startIndexOfNormalText,
                    startIndexOfRedTag,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            // indices of red tag
            temp = temp.replaceFirst(TAG.RED.start(), "")
            val endIndexOfRedTag = temp.indexOf(TAG.RED.end())
            temp = temp.replaceFirst(TAG.RED.end(), "")

            spannableStringBuilder.append(temp.substring(startIndexOfRedTag, endIndexOfRedTag))

            // Red Text
            spannableStringBuilder.setSpan(
                TextAppearanceSpan(context, R.style.Body1_GodText),
                startIndexOfRedTag,
                endIndexOfRedTag,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        if (temp.length > spannableStringBuilder.length) {
            val startIndexOfRemaingText = spannableStringBuilder.length
            spannableStringBuilder.append(temp.substring(startIndexOfRemaingText, temp.lastIndex))
            spannableStringBuilder.setSpan(
                TextAppearanceSpan(context, R.style.Body1),
                startIndexOfRemaingText,
                temp.lastIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableStringBuilder
    }
}
