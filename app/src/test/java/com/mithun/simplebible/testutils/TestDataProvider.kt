package com.mithun.simplebible.testutils

import com.mithun.simplebible.ui.model.Verse

object TestDataProvider {

    fun getTestVerses(): List<Verse> {
        return listOf(
            Verse(1, "verse 1", "verse text 1", isSelected = false, hasNotes = false, isBookmarked = false),
            Verse(2, "verse 2", "verse text 2", isSelected = true, hasNotes = false, isBookmarked = false),
            Verse(3, "verse 3", "verse text 3", isSelected = false, hasNotes = true, isBookmarked = false)
        )
    }
}
