package com.mithun.simplebible.testutils

import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.ui.model.Verse
import com.mithun.simplebible.utilities.ASV_BIBLE_ID

object TestDataProvider {

    fun getTestVerseEntities(): List<VerseEntity> {
        return listOf(
            VerseEntity("1", "JHN.1", ASV_BIBLE_ID, "John 1", "1", "verse text 1", emptyList(), emptyList(), null, null),
            VerseEntity("2", "JHN.2", ASV_BIBLE_ID, "John 2", "2", "verse text 2", emptyList(), emptyList(), null, null),
            VerseEntity("3", "JHN.3", ASV_BIBLE_ID, "John 3", "3", "verse text 3", emptyList(), emptyList(), null, null)
        )
    }

    fun getTestVerses(): List<Verse> {
        return listOf(
            Verse(1, "verse 1", "verse text 1", isSelected = false, hasNotes = false, isBookmarked = false),
            Verse(2, "verse 2", "verse text 2", isSelected = true, hasNotes = false, isBookmarked = false),
            Verse(3, "verse 3", "verse text 3", isSelected = false, hasNotes = true, isBookmarked = false)
        )
    }
}
